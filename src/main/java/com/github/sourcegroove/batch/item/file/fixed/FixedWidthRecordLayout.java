package com.github.sourcegroove.batch.item.file.fixed;

import org.springframework.batch.item.file.transform.Range;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedWidthRecordLayout  {
    private String prefix = "*";
    private Class targetType;
    private List<String> fieldNames = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();
    private List<Range> fieldRanges = new ArrayList<>();
    private String format;

    public Class getTargetType(){
        return this.targetType;
    }
    public void setTargetType(Class targetType){
        this.targetType = targetType;
    }
    public String getPrefix(){
        return this.prefix;
    }
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }
    public Map<Class<?>, PropertyEditor> getEditors(){
        return this.editors;
    }
    public List<String> getFieldNames(){
        return this.fieldNames;
    }
    public void setFormat(String format){
        this.format = format;
    }
    public String getFormat(){
        return this.format;
    }
    public List<Range> getFieldRanges() {
        return fieldRanges;
    }

}
