package com.github.sourcegroove.batch.item.file.excel.reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;

import java.util.List;
import java.util.stream.Collectors;


public class ExcelRowTokenizer {
    protected final Log log = LogFactory.getLog(getClass());
    private FieldSetFactory fieldSetFactory = new DefaultFieldSetFactory();
    private String[] names;

    public void setNames(String[] names) {
        this.names = names;
    }

    public FieldSet tokenize(List<String> row, int rowNumber){
        List<String> columns = row.stream().collect(Collectors.toList());
        String[] values = columns.toArray(new String[columns.size()]);
        if(values != null && names != null && names.length > values.length){
            throw new RuntimeException("Error tokenizing row: " + rowNumber
                    + " name count " + names.length
                    + " and field value count " + values.length
                    + "  don't match ");
        } else if (names == null){
            return this.fieldSetFactory.create(values);
        } else {
            return this.fieldSetFactory.create(values, names);
        }
    }


    private void logFields(List<String> values, String[] names){
        for(String name : names){
            log.info("Name: " + name);
        }
        for(String value : values){
            log.info("Value: " + value);
        }
    }
}
