package com.github.sourcegroove.batch.item.file.model.delimited;

import com.github.sourcegroove.batch.item.file.model.FileLayout;
import com.github.sourcegroove.batch.item.file.model.RecordLayout;

import java.util.ArrayList;
import java.util.List;

public class DelimitedFileLayout implements FileLayout {

    private int linesToSkip = 0;
    private String delimeter = ",";
    private char qualifier = '"';

    private List<RecordLayout> recordLayouts = new ArrayList<>();
    private DelimitedRecordLayout currentRecordLayout;


    @Override
    public List<RecordLayout> getRecordLayouts() {
        return this.recordLayouts;
    }

    @Override
    public int getLinesToSkip() {
        return this.linesToSkip;
    }

    public DelimitedFileLayout delimeter(String delimeter){
        this.delimeter = delimeter;
        return this;
    }

    public DelimitedFileLayout qualifier(char qualifier){
        this.qualifier = qualifier;
        return this;
    }
    public DelimitedFileLayout linesToSkip(int linesToSkip){
        this.linesToSkip = linesToSkip;
        return this;
    }
    public DelimitedRecordLayout record(Class targetType){
        this.currentRecordLayout = DelimitedRecordLayout.of(this, targetType);
        this.recordLayouts.add(this.currentRecordLayout);
        return this.currentRecordLayout;
    }

}
