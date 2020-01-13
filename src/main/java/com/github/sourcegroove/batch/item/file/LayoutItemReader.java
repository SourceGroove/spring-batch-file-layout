package com.github.sourcegroove.batch.item.file;

import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.beans.factory.InitializingBean;

public interface LayoutItemReader<T> extends ResourceAwareItemReaderItemStream<T>, InitializingBean {
}
