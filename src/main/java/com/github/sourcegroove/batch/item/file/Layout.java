package com.github.sourcegroove.batch.item.file;

import java.util.List;

public interface Layout {
    <T> LayoutItemWriter<T> getItemWriter();
    <T> LayoutItemReader<T> getItemReader();
    
    List<RecordLayout> getRecords();

}
