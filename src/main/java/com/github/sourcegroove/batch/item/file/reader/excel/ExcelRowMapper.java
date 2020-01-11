package com.github.sourcegroove.batch.item.file.reader.excel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

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

    public T mapRow(Row row)  {
        FieldSet fieldSet = this.rowTokenizer.tokenize(row);
        try {
            return this.fieldSetMapper.mapFieldSet(fieldSet);
        } catch (BindException e) {
            throw new RuntimeException("Error mapping fieldset", e);
        }
    }
}
