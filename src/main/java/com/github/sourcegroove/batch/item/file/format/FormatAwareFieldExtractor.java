package com.github.sourcegroove.batch.item.file.format;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.beans.PropertyEditor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormatAwareFieldExtractor<T> implements FieldExtractor<T>, InitializingBean {
    private static final Log log = LogFactory.getLog(FormatAwareFieldExtractor.class);
    PropertyFormatter formatter = new PropertyFormatter();
    private Map<Class<?>, PropertyEditor> customEditors;
    private List<Format> formats;
    private List<String> names;

    public void setCustomEditors(Map<Class<?>, PropertyEditor> customEditors) {
        this.formatter.editors(customEditors);
        this.customEditors = customEditors;
    }

    public void setFormats(Format[] formats) {
        setFormats(Arrays.asList(formats));
    }

    public void setFormats(List<Format> formats) {
        this.formatter.formats(formats);
        this.formats = formats;
    }

    public void setNames(String[] names) {
        setNames(Arrays.asList(names));
    }

    public void setNames(List<String> names) {
        Assert.notNull(names, "Names must be non-null");
        this.formatter.names(names);
        this.names = names;
    }

    public void afterPropertiesSet() {
        Assert.notNull(this.names, "The 'names' property must be set.");
        Assert.isTrue(CollectionUtils.isEmpty(this.formats) || this.formats.size() == this.names.size(), "The 'formats' size must match 'names' size.");
    }

    public Object[] extract(T item) {
        BeanWrapper bw = new BeanWrapperImpl(item);
        List<Object> values = this.names.stream()
                .map(name -> formatter.formatForWrite(name, bw.getPropertyValue(name)))
                .collect(Collectors.toList());
        return values.toArray();
    }

}
