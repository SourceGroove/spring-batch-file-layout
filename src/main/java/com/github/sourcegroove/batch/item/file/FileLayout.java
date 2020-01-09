package com.github.sourcegroove.batch.item.file;

import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;

public interface FileLayout {
    <T> ResourceAwareItemWriterItemStream<T> getItemWriter();
    <T> ResourceAwareItemReaderItemStream<T> getItemReader();

}
