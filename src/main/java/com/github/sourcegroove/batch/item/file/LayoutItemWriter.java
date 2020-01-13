package com.github.sourcegroove.batch.item.file;

import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.beans.factory.InitializingBean;

public interface LayoutItemWriter<T> extends ResourceAwareItemWriterItemStream<T>, InitializingBean {
}
