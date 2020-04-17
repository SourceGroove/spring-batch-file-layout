package com.github.sourcegroove.batch.item.file.excel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.List;

public class ExcelRowMapper<T> {
    protected final Log log = LogFactory.getLog(getClass());

    private ExcelRowTokenizer rowTokenizer;
    private FieldSetMapper<T> fieldSetMapper;

    public void setRowTokenizer(ExcelRowTokenizer rowTokenizer) {
        this.rowTokenizer = rowTokenizer;
    }
    public void setFieldSetMapper(FieldSetMapper<T> fieldSetMapper) {
        this.fieldSetMapper = fieldSetMapper;
    }

    public T mapRow(List<String> row, int rowNumber)  {
        log.trace("Mapping row " + rowNumber + " with " + row.size() + " values");
        FieldSet fieldSet = this.rowTokenizer.tokenize(row, rowNumber);
        try {
            return this.fieldSetMapper.mapFieldSet(fieldSet);
        } catch (BindException e) {
            throw new RuntimeException("Error mapping fieldset at row " + rowNumber, e);
        }
    }

}
