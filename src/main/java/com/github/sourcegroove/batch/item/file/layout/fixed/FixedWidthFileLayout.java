package com.github.sourcegroove.batch.item.file.layout.fixed;

import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import com.github.sourcegroove.batch.item.file.layout.RecordLayout;

import java.util.ArrayList;
import java.util.List;


public class FixedWidthFileLayout implements FileLayout {
    private int linesToSkip = 0;
    private List<RecordLayout> recordLayouts = new ArrayList<>();
    private FixedWidthRecordLayout currentRecordLayout;

    @Override
    public List<RecordLayout> getRecordLayouts() {
        return this.recordLayouts;
    }
    @Override
    public int getLinesToSkip() {
        return this.linesToSkip;
    }

    public FixedWidthFileLayout linesToSkip(int linesToSkip){
        this.linesToSkip = linesToSkip;
        return this;
    }
    public FixedWidthRecordLayout record(Class targetType){
        this.currentRecordLayout = FixedWidthRecordLayout.of(this, targetType);
        this.recordLayouts.add(this.currentRecordLayout);
        return this.currentRecordLayout;
    }

}
