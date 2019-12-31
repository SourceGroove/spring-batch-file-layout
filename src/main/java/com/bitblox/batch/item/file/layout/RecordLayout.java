package com.bitblox.batch.item.file.layout;


import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.file.transform.LineTokenizer;

import java.beans.PropertyEditor;
import java.util.Map;

public interface RecordLayout {
    LineTokenizer getLineTokenizer();
    LineAggregator getLineAggregator();
    FieldSetMapper getFieldSetMapper();
    String getPrefix();
    
}
