package com.github.sourcegroove.batch.item.file.layout;


import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.file.transform.LineTokenizer;

public interface RecordLayout {
    LineTokenizer getLineTokenizer();
    LineAggregator getLineAggregator();
    FieldSetMapper getFieldSetMapper();
    String getPrefix();
    Class getTargetType();
}
