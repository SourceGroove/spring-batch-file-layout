package com.bitblox.batch.item.file;

import lombok.extern.java.Log;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log
public class FileLayoutFieldExtractor<T> implements FieldExtractor<T>, InitializingBean {
    private FieldExtractor<T> fieldExtractor;
    private Map<Class<?>, PropertyEditor> customEditors;

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
        List<Object> formattedFields = new ArrayList<>();
        for(Object field : fields){
            PropertyEditor editor = this.customEditors.get(field.getClass());
            if(editor != null){
                editor.setValue(field);
                formattedFields.add(editor.getAsText());
            } else {
                formattedFields.add(field);
            }

        }
        return formattedFields.toArray(new Object[formattedFields.size()]);
    }

   
}
