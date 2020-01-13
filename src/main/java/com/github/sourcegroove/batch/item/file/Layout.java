package com.github.sourcegroove.batch.item.file;

public interface Layout {
    <T> LayoutItemWriter<T> getItemWriter();
    <T> LayoutItemReader<T> getItemReader();

}
