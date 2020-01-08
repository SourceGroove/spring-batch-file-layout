package com.github.sourcegroove.batch.item.file.layout.excel;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.file.transform.LineTokenizer;

public class ExcelRecordLayout {

    public LineTokenizer getLineTokenizer() {
        return null;
    }

    public LineAggregator getLineAggregator() {
        return null;
    }

    public FieldSetMapper getFieldSetMapper() {
        return null;
    }

    public String getPrefix() {
        return null;
    }

    public Class getTargetType() {
        return null;
    }
}
