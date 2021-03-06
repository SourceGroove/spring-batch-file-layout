package com.github.sourcegroove.batch.item.file.format;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindException;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class delegates the actual work to the provided FieldSetMapper, but
 * mutates the field set a bit for columns that have a <tt>Format</tt> provided
 */
public class FormatAwareFieldSetMapper<T> implements FieldSetMapper<T>, InitializingBean {
    protected static final Log log = LogFactory.getLog(FormatAwareFieldSetMapper.class);
    private PropertyFormatter formatter;
    private BeanWrapperFieldSetMapper<T> delegate;
    private Class<? extends T> targetType;
    private Map<Class<?>, PropertyEditor> customEditors;
    private List<String> names;
    private List<Format> formats;
    private BeanWrapperImpl beanWrapper;

    public FormatAwareFieldSetMapper(){
        this.formatter = new PropertyFormatter();
        this.delegate = new BeanWrapperFieldSetMapper<T>();
        this.delegate.setDistanceLimit(0);
        this.delegate.setStrict(false);
    }

    public void setCustomEditors(Map<Class<?>, PropertyEditor> customEditors) {
        this.customEditors = customEditors;
        this.delegate.setCustomEditors(this.customEditors);
        this.formatter.editors(customEditors);
    }
    public void setTargetType(Class<? extends T> type) {
        this.targetType = type;
        this.createBeanWrapper(type);
        this.delegate.setTargetType(type);
    }
    public void setFormats(Format[] formats) {
        setFormats(Arrays.asList(formats));
    }
    public void setFormats(List<Format> formats) {
        this.formats = formats;
        this.formatter.formats(formats);
    }
    public void setNames(String[] names) {
        setNames(Arrays.asList(names));
    }
    public void setNames(List<String> names) {
        Assert.notNull(names, "Names must be non-null");
        this.names = names;
        this.formatter.names(names);
    }
    
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.names, "'names' must be set");
        this.delegate.afterPropertiesSet();
        this.formatter.afterPropertiesSet();
    }

    @Override
    public T mapFieldSet(FieldSet fieldSet) throws BindException {
        FieldSet reformatted = reformat(fieldSet);
        return this.delegate.mapFieldSet(reformatted);
    }

    private FieldSet reformat(FieldSet fieldSet){
        List<String> names = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for(int i = 0; i < fieldSet.getFieldCount(); i++){
            String name = fieldSet.getNames()[i];
            String value = fieldSet.getValues()[i];
            if(!StringUtils.equalsIgnoreCase(name, PropertyFormatter.NON_FIELD_PROPERTY)) {
                names.add(name);
                Class type = this.beanWrapper.getPropertyType(name);
                if(type == null){
                    throw new IllegalArgumentException("Property '" +  name + "' not found on  " + this.targetType);
                }
                values.add(formatter.formatForRead(name, type, value));
            }
        }
        return new DefaultFieldSet(values.toArray(new String[values.size()]), names.toArray(new String[names.size()]));
    }

    private void createBeanWrapper(Class<? extends T> type){
        try {
            this.beanWrapper = new BeanWrapperImpl(type.newInstance());
            this.beanWrapper.setAutoGrowNestedPaths(true);
        } catch (IllegalAccessException | InstantiationException var2) {
            ReflectionUtils.handleReflectionException(var2);
            throw new IllegalStateException("Unable to create bean of type " + type);
        }
    }
   
}
