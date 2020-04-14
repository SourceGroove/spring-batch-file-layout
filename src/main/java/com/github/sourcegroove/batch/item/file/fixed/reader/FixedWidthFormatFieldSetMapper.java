package com.github.sourcegroove.batch.item.file.fixed.reader;

import com.github.sourcegroove.batch.item.file.fixed.FixedWidthPropertyFormatter;
import com.github.sourcegroove.batch.item.file.fixed.Format;
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
 * mutes the field set a bit for columns that have a <tt>Format</tt> provided
 */
public class FixedWidthFormatFieldSetMapper<T> implements FieldSetMapper<T>, InitializingBean {
    protected static final Log log = LogFactory.getLog(FixedWidthFormatFieldSetMapper.class);
    private String[] names;
    private Map<Class<?>, PropertyEditor> customEditors;
    private Format[] formats;
    private BeanWrapperImpl beanWrapper;

    private FixedWidthPropertyFormatter formatter;
    private BeanWrapperFieldSetMapper<T> delegate;

    public FixedWidthFormatFieldSetMapper(){
        this.formatter = new FixedWidthPropertyFormatter();
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
        this.createBeanWrapper(type);
        this.delegate.setTargetType(type);
    }
    public void setFormats(Format[] formats) {
        this.formats = formats;
        this.formatter.formats(Arrays.asList(formats));
    }
    public void setNames(String[] names) {
        Assert.notNull(names, "Names must be non-null");
        this.names = names;
        this.formatter.names(Arrays.asList(names));
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.names, "'names' must be set");
        this.delegate.afterPropertiesSet();
        this.formatter.afterPropertiesSet();
    }

    @Override
    public T mapFieldSet(FieldSet fieldSet) throws BindException {
        FieldSet reformatted = reformat(fieldSet);
        return delegate.mapFieldSet(reformatted);
    }

    public FieldSet reformat(FieldSet fieldSet){
        List<String> names = new ArrayList<>();
        List<String> values = new ArrayList<>();
        for(int i = 0; i < fieldSet.getFieldCount(); i++){
            String name = fieldSet.getNames()[i];
            String value = fieldSet.getValues()[i];
            if(!StringUtils.equalsIgnoreCase(name, FixedWidthPropertyFormatter.NON_FIELD_PROPERTY)) {
                names.add(name);
                Class type = this.beanWrapper.getPropertyType(name);
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
    private void logFieldSet(FieldSet fieldSet){
        for(int i = 0; i < fieldSet.getFieldCount(); i++){
            log.debug("Field " + fieldSet.getNames()[i] + "=" + fieldSet.getValues()[i]);
        }
    }
}
