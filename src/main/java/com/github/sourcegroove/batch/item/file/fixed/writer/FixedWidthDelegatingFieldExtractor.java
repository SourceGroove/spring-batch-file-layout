package com.github.sourcegroove.batch.item.file.fixed.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FixedWidthDelegatingFieldExtractor<T> implements FieldExtractor<T>, InitializingBean {
    protected static final Log log = LogFactory.getLog(FixedWidthBeanWrapperFieldExtractor.class);
    private FieldExtractor<T> fieldExtractor;
    private Map<Class<?>, PropertyEditor> customEditors;
    private boolean extractAsStrings;

    /**
     * By default this extractor will extract fields as their native type, but
     * if this is set to true, then they will all be converted to String objects
     * using any provided format mechanisms (property editors or column formats).
     *
     * *note - property editors will always return strings for their types regardless
     * of this value.  This is for passing the extracted fields to a format created from the
     * FixedWidthFormatBuilder when it's usePureFormat is set to false.
     * @param extractAsStrings
     */
    public void setExtractAsStrings(boolean extractAsStrings){
        this.extractAsStrings = extractAsStrings;
    }
    public void setFieldExtractor(FieldExtractor<T> fieldExtractor){
        this.fieldExtractor = fieldExtractor;
    }
    public void setCustomEditors(Map<Class<?>, PropertyEditor> customEditors) {
        this.customEditors = customEditors;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.fieldExtractor, "The 'fieldExtractor' property must be set.");
    }

    @Override
    public Object[] extract(T object) {
        Object[] fields = this.fieldExtractor.extract(object);
        return format(fields);
    }

    private Object[] format(Object[] fields) {
        return Arrays.asList(fields)
                .stream()
                .map(f -> format(f))
                .collect(Collectors.toList())
                .toArray(new Object[fields.length]);
    }

    private Object format(Object field){
        PropertyEditor editor = customEditors != null && field != null ? customEditors.get(field.getClass()) : null;

        if(editor != null){
            editor.setValue(field);
            return editor.getAsText();

        } else if (field != null) {
            return field;

        } else {
            return "";
        }
    }
}
