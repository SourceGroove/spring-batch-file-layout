package com.github.sourcegroove.batch.item.file.delimited.builder;

import com.github.sourcegroove.batch.item.file.delimited.DelimitedRecordLayout;

import java.beans.PropertyEditor;

public class DelimitedRecordLayoutBuilder {

    private DelimitedFileLayoutBuilder layoutBuilder;
    private DelimitedRecordLayout recordLayout;

    public DelimitedRecordLayoutBuilder(DelimitedFileLayoutBuilder layoutBuilder){
        this.layoutBuilder = layoutBuilder;
        this.recordLayout = new DelimitedRecordLayout();
    }
    public DelimitedRecordLayout build(){
        return this.recordLayout;
    }
    public DelimitedFileLayoutBuilder and(){
        return this.layoutBuilder;
    }
    public DelimitedRecordLayoutBuilder targetType(Class targetType){
        this.recordLayout.setTargetType(targetType);
        return this;
    }
    public DelimitedRecordLayoutBuilder column(String name){
        this.recordLayout.getFieldNames().add(name);
        return this;
    }
    public DelimitedRecordLayoutBuilder editor(Class<?> type, PropertyEditor editor){
        this.recordLayout.getEditors().put(type, editor);
        return this;
    }

}
