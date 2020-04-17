package com.github.sourcegroove.batch.item.file.delimited;

import com.github.sourcegroove.batch.item.file.LayoutItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;

public class DelimitedFileItemWriter<T> extends FlatFileItemWriter<T> implements LayoutItemWriter<T> {

    public LineAggregator<T> getLineAggregator() {
        return this.lineAggregator;
    }
}
