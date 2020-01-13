package com.github.sourcegroove.batch.item.file.excel.reader;

import com.github.sourcegroove.batch.item.file.LayoutItemReader;

import java.util.Set;

/**
 * Note that dates will be serialized as ISO dates, so you're date editors will need to use that format.
 *
 * If you use the ExcelLayout to build your reader/writers, it will add editors for LocalDate and LocalDateTime using
 * this format by default.
 *
 * @param <T>
 */
public interface ExcelItemReader<T> extends LayoutItemReader<T> {
    void setLinesToSkip(int linesToSkip);
    void setSheetsToRead(Set<Integer> sheetsToRead);
    void setRowMapper(ExcelRowMapper<T> rowMapper);
}
