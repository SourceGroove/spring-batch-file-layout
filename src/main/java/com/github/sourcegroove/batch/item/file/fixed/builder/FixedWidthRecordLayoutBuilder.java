package com.github.sourcegroove.batch.item.file.fixed.builder;

import com.github.sourcegroove.batch.item.file.fixed.FixedWidthRecordLayout;
import org.springframework.batch.item.file.transform.Range;

import java.beans.PropertyEditor;

public class FixedWidthRecordLayoutBuilder {
    private FixedWidthFileLayoutBuilder layoutBuilder;
    private FixedWidthRecordLayout recordLayout;
    private StringFormatBuilder formatBuilder = new StringFormatBuilder();

    public FixedWidthRecordLayoutBuilder(FixedWidthFileLayoutBuilder layoutBuilder){
        this.layoutBuilder = layoutBuilder;
        this.recordLayout = new FixedWidthRecordLayout();
    }
    public FixedWidthRecordLayout build(){
        this.recordLayout.setFormat(this.formatBuilder.toString());
        return this.recordLayout;
    }
    public FixedWidthFileLayoutBuilder and(){
        return this.layoutBuilder;
    }
    public FixedWidthRecordLayoutBuilder targetType(Class targetType){
        this.recordLayout.setTargetType(targetType);
        return this;
    }
    public FixedWidthRecordLayoutBuilder prefix(String prefix){
        this.recordLayout.setPrefix(prefix);
        return this;
    }
    public FixedWidthRecordLayoutBuilder editor(Class<?> type, PropertyEditor editor){
        this.recordLayout.getEditors().put(type, editor);
        return this;
    }
    public FixedWidthRecordLayoutBuilder column(String name, int start, int end){
        return column(name, start, end, StringFormatBuilder.Format.STRING);
    }
    public FixedWidthRecordLayoutBuilder column(String name, int start, int end, StringFormatBuilder.Format format){
        Range range = new Range(start, end);
        this.recordLayout.getFieldRanges().add(range);
        this.recordLayout.getFieldNames().add(name);
        this.formatBuilder.append(range, format);
        return this;
    }

}
