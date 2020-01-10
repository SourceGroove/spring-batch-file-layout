package com.github.sourcegroove.batch.item.file.layout.excel;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelRecordLayout {
    private ExcelSheetLayout sheetLayout;
    private Class targetType;
    private List<String> columns = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();

    public ExcelRecordLayout(Class targetType, ExcelSheetLayout sheetLayout){
        this.targetType = targetType;
        this.sheetLayout = sheetLayout;
    }
    public Class getTargetType(){
        return this.targetType;
    }
    public String[] getColumns(){
        return this.columns.toArray(new String[this.columns.size()]);
    }
    public ExcelRecordLayout column(String column){
        this.columns.add(column);
        return this;
    }
    public Map<Class<?>, PropertyEditor> getEditors(){
        return this.editors;
    }
    public ExcelRecordLayout editor(Class clazz, PropertyEditor editor){
        this.editors.put(clazz, editor);
        return this;
    }
    public ExcelRecordLayout record(Class targetType){
        return this.sheetLayout.record(targetType);
    }
    public ExcelFileLayout layout(){
        return this.sheetLayout.layout();
    }
    public ExcelSheetLayout sheet(){
        return this.sheetLayout;
    }
}
