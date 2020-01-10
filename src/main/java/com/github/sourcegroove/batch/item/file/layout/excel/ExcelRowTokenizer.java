package com.github.sourcegroove.batch.item.file.layout.excel;

import org.apache.poi.ss.usermodel.*;
import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ExcelRowTokenizer {
    private FieldSetFactory fieldSetFactory = new DefaultFieldSetFactory();
    private FormulaEvaluator formulaEvaluator;

    private String[] names;

    public void setFormEvaluator(FormulaEvaluator formEvaluator){
        this.formulaEvaluator = formEvaluator;
    }
    public void setNames(String[] names) {
        this.names = names;
    }

    public FieldSet tokenize(Row row){
        List<String> v = new ArrayList<>();

        Iterator<Cell> cellIterator = row.cellIterator();
        while(cellIterator.hasNext()){
            Cell cell = cellIterator.next();
            v.add(getValue(cell));
        }

        String[] values = v.toArray(new String[v.size()]);
        if(values != null && names != null && names.length != values.length){
            throw new RuntimeException("Field name count and field value count don't match ");
        } else if (names == null){
            return this.fieldSetFactory.create(values);
        } else {
            return this.fieldSetFactory.create(values, names);
        }
    }

    public String getValue(Cell cell){
        String value = "";

        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            value = String.valueOf(date.getTime());

        } else if (cell.getCellType() == CellType.NUMERIC) {
            value = String.valueOf(cell.getNumericCellValue());

        } else if (cell.getCellType() == CellType.BOOLEAN){
            value = String.valueOf(cell.getBooleanCellValue());

        } else if (cell.getCellType() == CellType.FORMULA && this.formulaEvaluator != null){
            value = formulaEvaluator.evaluate(cell).formatAsString();

        } else {
            value = cell.getStringCellValue();
        }

        return value;
    }



}
