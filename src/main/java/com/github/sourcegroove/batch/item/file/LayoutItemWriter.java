package com.github.sourcegroove.batch.item.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.beans.factory.InitializingBean;

public interface LayoutItemWriter<T> extends ResourceAwareItemWriterItemStream<T>, InitializingBean {
    Log log = LogFactory.getLog(LayoutItemWriter.class);

    default void enableHeaderCallback(Object object){
        log.debug("enableHeaderCallback not implemented in writer");
    }
    default void enableFooterCallback(Object object){
        log.debug("enableFooterCallback not implemented in writer");
    }
}
