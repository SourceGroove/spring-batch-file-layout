package com.github.sourcegroove.batch.item.file.layout.delimited;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelimitedRecordLayout {
    private DelimitedFileLayout fileLayout;
    private Class targetType;
    private List<String> columns = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();

    public DelimitedRecordLayout(Class targetType, DelimitedFileLayout fileLayout){
        this.targetType = targetType;
        this.fileLayout = fileLayout;
    }
    public Class getTargetType(){
        return this.targetType;
    }
    public List<String> getColumns(){
        return this.columns;
    }
    public DelimitedRecordLayout column(String column){
        this.columns.add(column);
        return this;
    }
    public Map<Class<?>, PropertyEditor> getEditors(){
        return this.editors;
    }
    public DelimitedRecordLayout editor(Class clazz, PropertyEditor editor){
        this.editors.put(clazz, editor);
        return this;
    }
    public DelimitedRecordLayout record(Class targetType){
        return this.fileLayout.record(targetType);
    }
    public DelimitedFileLayout layout(){
        return this.fileLayout;
    }
}
