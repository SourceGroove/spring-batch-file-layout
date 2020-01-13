package com.github.sourcegroove.batch.item.file.delimited.reader;

import com.github.sourcegroove.batch.item.file.LayoutItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;

public class DelimitedFileItemReader<T> extends FlatFileItemReader<T> implements LayoutItemReader<T> {
}
