package com.github.sourcegroove.batch.item.file.layout.excel;

import java.util.ArrayList;
import java.util.List;

public class ExcelSheetLayout {
    private ExcelFileLayout fileLayout;
    private int position = -1;
    private String name;
    private int linesToSkip = 0;

    private List<ExcelRecordLayout> records = new ArrayList<>();

    public ExcelSheetLayout(ExcelFileLayout fileLayout){
        this.fileLayout = fileLayout;
    }
    public ExcelSheetLayout position(int position){
        this.position = position;
        return this;
    }
    public ExcelSheetLayout name(String name){
        this.name = name;
        return this;
    }
    public ExcelSheetLayout linesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
        return this;
    }
    public int getLinesToSkip(){
        return this.linesToSkip;
    }
    public ExcelRecordLayout record(Class targetType) {
        this.records.add(new ExcelRecordLayout(targetType, this));
        return this.records.get(this.records.size() - 1);
    }
    public List<ExcelRecordLayout> getRecords(){
        return this.records;
    }
    public ExcelFileLayout layout(){
        return this.fileLayout;
    }

}
