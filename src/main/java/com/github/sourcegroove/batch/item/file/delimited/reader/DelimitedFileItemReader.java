package com.github.sourcegroove.batch.item.file.delimited.reader;

import com.github.sourcegroove.batch.item.file.LayoutItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;

public class DelimitedFileItemReader<T> extends FlatFileItemReader<T> implements LayoutItemReader<T> {

    LineMapper<T> lineMapper;

    public void setLineMapper(LineMapper<T> lineMapper) {
        this.lineMapper = lineMapper;
        super.setLineMapper(lineMapper);
    }

    public LineMapper<T> getLineMapper(){
        return this.lineMapper;
    }
}
