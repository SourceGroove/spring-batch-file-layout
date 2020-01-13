package com.github.sourcegroove.batch.item.file.fixed.reader;

import com.github.sourcegroove.batch.item.file.LayoutItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;

public class FixedWidthFileItemReader<T> extends FlatFileItemReader<T> implements LayoutItemReader<T> {
}
