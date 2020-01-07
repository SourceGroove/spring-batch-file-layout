package com.github.sourcegroove.batch.item.file.model;

import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.batch.item.file.transform.Range;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;


@Log
@Data
public class FixedWidthFileLayout implements FileLayout {
    private int linesToSkip = 0;
    private List<RecordLayout> recordLayouts = new ArrayList<>();
    private FixedWidthRecordLayout currentRecordLayout;

    public FixedWidthFileLayout linesToSkip(int linesToSkip){
        this.linesToSkip = linesToSkip;
        return this;
    }
    public FixedWidthFileLayout record(Class targetType){
        this.currentRecordLayout = new FixedWidthRecordLayout();
        this.currentRecordLayout.setTargetType(targetType);
        this.recordLayouts.add(this.currentRecordLayout);
        return this;
    }
    public FixedWidthFileLayout prefix(String prefix){
        this.currentRecordLayout.setPrefix(prefix);
        return this;
    }
    public FixedWidthFileLayout column(String name, int start, int end){
        this.currentRecordLayout.getFieldNames().add(name);
        this.currentRecordLayout.getFieldRanges().add(new Range(start, end));
        return this;
    }
    public FixedWidthFileLayout editor(Class<?> type, PropertyEditor editor){
        this.currentRecordLayout.getEditors().put(type, editor);
        return this;
    }

}
