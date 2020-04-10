package com.github.sourcegroove.batch.item.file.fixed.writer;

import com.github.sourcegroove.batch.item.file.fixed.FixedWidthPropertyFormatter;
import com.github.sourcegroove.batch.item.file.fixed.Format;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.beans.PropertyEditor;
import java.util.*;
import java.util.stream.Collectors;


public class FixedWidthBeanWrapperFieldExtractor<T> implements FieldExtractor<T>, InitializingBean {
    protected static final Log log = LogFactory.getLog(FixedWidthBeanWrapperFieldExtractor.class);
    FixedWidthPropertyFormatter formatter = new FixedWidthPropertyFormatter();
    private Map<Class<?>, PropertyEditor> customEditors;
    private List<Format> formats;
    private List<String> names;
    
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
        formatter.names(this.names);
        formatter.editors(this.customEditors);
        formatter.formats(this.formats);
        BeanWrapper bw = new BeanWrapperImpl(item);
        List<Object> values = this.names.stream()
                .map(name -> formatter.formatForWrite(name, bw.getPropertyValue(name)))
                .collect(Collectors.toList());
        return values.toArray();
    }

}
