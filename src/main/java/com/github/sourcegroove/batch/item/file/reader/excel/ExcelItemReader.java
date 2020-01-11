package com.github.sourcegroove.batch.item.file.reader.excel;

import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.beans.factory.InitializingBean;

public interface ExcelItemReader<T> extends ResourceAwareItemReaderItemStream<T>, InitializingBean {
}
