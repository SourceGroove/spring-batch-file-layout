package com.github.sourcegroove.batch.item.file.reader.excel;

import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.beans.factory.InitializingBean;

import java.util.Set;

/**
 * Note that dates will be serialized as ISO dates, so you're date editors will need to use that format.
 * @param <T>
 */
public interface ExcelItemReader<T> extends ResourceAwareItemReaderItemStream<T>, InitializingBean {
    void setLinesToSkip(int linesToSkip);
    void setSheetsToRead(Set<Integer> sheetsToRead);
    void setRowMapper(ExcelRowMapper<T> rowMapper);
}
