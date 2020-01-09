package com.github.sourcegroove.batch.item.file.fixed;

import com.github.sourcegroove.batch.item.file.FileLayout;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;

import java.util.ArrayList;
import java.util.List;

public class FixedWidthFileLayout implements FileLayout {
    private int linesToSkip = 0;
    private List<FixedWidthRecordLayout> recordLayouts = new ArrayList<>();
    private ResourceAwareItemWriterItemStream itemWriter;
    private ResourceAwareItemReaderItemStream itemReader;

    public int getLinesToSkip() {
        return linesToSkip;
    }
    public void setLinesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
    }
    public List<FixedWidthRecordLayout> getRecordLayouts() {
        return recordLayouts;
    }
    public void setRecordLayouts(List<FixedWidthRecordLayout> recordLayouts) {
        this.recordLayouts = recordLayouts;
    }
    public ResourceAwareItemWriterItemStream getItemWriter() {
        return itemWriter;
    }
    public void setItemWriter(ResourceAwareItemWriterItemStream itemWriter) {
        this.itemWriter = itemWriter;
    }
    public ResourceAwareItemReaderItemStream getItemReader() {
        return itemReader;
    }
    public void setItemReader(ResourceAwareItemReaderItemStream itemReader) {
        this.itemReader = itemReader;
    }
}
