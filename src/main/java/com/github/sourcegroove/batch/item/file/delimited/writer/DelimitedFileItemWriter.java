package com.github.sourcegroove.batch.item.file.delimited.writer;

import com.github.sourcegroove.batch.item.file.LayoutItemWriter;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.io.IOException;
import java.io.Writer;

public class DelimitedFileItemWriter<T> extends FlatFileItemWriter<T> implements LayoutItemWriter<T> {

}
