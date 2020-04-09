package com.github.sourcegroove.batch.item.file.fixed.writer;

import com.github.sourcegroove.batch.item.file.fixed.Format;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.beans.PropertyEditor;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FixedWidthBeanWrapperFieldExtractor<T> implements FieldExtractor<T>, InitializingBean {
    protected static final Log log = LogFactory.getLog(FixedWidthBeanWrapperFieldExtractor.class);
    private static final String EMPTY_STRING = "";
    private Map<Class<?>, PropertyEditor> customEditors;
    private List<Format> formats;
    private List<String> names;
    private boolean extractAsStrings;

    /*
     * By default this extractor will extract fields as their native type, but
     * if this is set to true, then they will all be converted to String objects
     * using any provided format mechanisms (property editors or column formats).
     * <p>
     * *note - property editors will always return strings for their types regardless
     * of this value.  This is for passing the extracted fields to a format created from the
     * FixedWidthFormatBuilder when it's usePureFormat is set to false.
     *
     * @param extractAsStrings <tt>true</tt> to pre format all values to String
     */
    public void setExtractAsStrings(boolean extractAsStrings) {
        this.extractAsStrings = extractAsStrings;
    }

    public void setCustomEditors(Map<Class<?>, PropertyEditor> customEditors) {
        this.customEditors = customEditors;
    }

    public void setFormats(Format[] formats) {
        this.formats = Arrays.asList(formats);
    }

    public void setNames(String[] names) {
        Assert.notNull(names, "Names must be non-null");
        this.names = Arrays.asList(names);
    }

    public void afterPropertiesSet() {
        Assert.notNull(this.names, "The 'names' property must be set.");
        Assert.isTrue(CollectionUtils.isEmpty(this.formats) || this.formats.size() == this.names.size(), "The 'formats' size must match 'names' size.");
    }

    public Object[] extract(T item) {
        BeanWrapper bw = new BeanWrapperImpl(item);
        List<Object> values = this.names.stream()
                .map(name -> getValue(name, bw))
                .map(v -> v == null ? EMPTY_STRING : v)
                .collect(Collectors.toList());
        return values.toArray();
    }

    private Object getValue(String propertyName, BeanWrapper bw) {
        if (StringUtils.isBlank(propertyName)) {
            return null;
        }
        Object value = bw.getPropertyValue(propertyName);
        if (value == null) {
            return null;
        }
        
        Object formattedValue = getValueUsingFormat(propertyName, value);
        if(formattedValue != null){
            return formattedValue;
        }
        
        formattedValue = getValueUsingPropertyEditor(value);
        if(formattedValue != null){
            return formattedValue;
        }
        
        return value.toString();
    }

    private Object getValueUsingPropertyEditor(Object value){
        if(customEditors == null){
            return null;
        }
        PropertyEditor editor = customEditors.get(value.getClass());
        if (editor != null) {
            editor.setValue(value);
            return editor.getAsText();
        }
        
        return null;
    }
    private Object getValueUsingFormat(String propertyName, Object value) {
        if (CollectionUtils.isEmpty(this.formats)) {
            return null;
        }
        
        int index = this.names.indexOf(propertyName);
        Format format = this.formats.get(index);
        if (format == null || !format.hasPattern()) {
            return null;

        } else if (format.isDateFormat() && value instanceof Date) {
            return new SimpleDateFormat(format.getPattern()).format(value);
            
        } else if (format.isDateFormat() && value instanceof TemporalAccessor) {
            return DateTimeFormatter.ofPattern(format.getPattern()).format((TemporalAccessor)value);
            
        } else if (format == Format.DECIMAL) {
            return new DecimalFormat(format.getPattern()).format(value);
            
        } else {
            return null;
        }
    }

}
