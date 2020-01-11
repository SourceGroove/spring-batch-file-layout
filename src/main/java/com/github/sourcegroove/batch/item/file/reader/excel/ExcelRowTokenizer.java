package com.github.sourcegroove.batch.item.file.reader.excel;

import org.apache.poi.ss.usermodel.*;
import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;

import java.beans.PropertyEditor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExcelRowTokenizer {
    private FieldSetFactory fieldSetFactory = new DefaultFieldSetFactory();
    private String[] names;
    private FormulaEvaluator formulaEvaluator;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;

    public void setDateFormatter(DateTimeFormatter dateFormatter){
        this.dateFormatter = dateFormatter;
    }
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
            throw new RuntimeException("Error tokenizing row " + row.getRowNum()
                    + " name count " + names.length
                    + " and field value count " + values.length
                    + "  don't match ");
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
            LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            value = dateFormatter.format(ldt);

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
