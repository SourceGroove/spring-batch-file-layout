package com.github.sourcegroove.batch.item.file.delimited.writer;

import com.github.sourcegroove.batch.item.file.LayoutItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;

public class DelimitedFileItemWriter<T> extends FlatFileItemWriter<T> implements LayoutItemWriter<T> {
}
