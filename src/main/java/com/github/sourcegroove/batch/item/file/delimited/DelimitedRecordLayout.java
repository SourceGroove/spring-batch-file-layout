package com.github.sourcegroove.batch.item.file.delimited;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelimitedRecordLayout {
    private Class targetType;
    private List<String> fieldNames = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();

    public Class getTargetType(){
        return this.targetType;
    }
    public void setTargetType(Class targetType){
        this.targetType = targetType;
    }
    public Map<Class<?>, PropertyEditor> getEditors(){
        return this.editors;
    }
    public List<String> getFieldNames(){
        return this.fieldNames;
    }

}
