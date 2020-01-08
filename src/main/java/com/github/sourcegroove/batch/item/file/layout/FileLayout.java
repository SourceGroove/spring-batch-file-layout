package com.github.sourcegroove.batch.item.file.layout;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.LineAggregator;

public interface FileLayout {

    int getLinesToSkip();
    boolean isValid();
    LineMapper getLineMapper();
    LineAggregator getLineAggregator(Class targetType);

}
