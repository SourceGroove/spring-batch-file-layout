package com.github.sourcegroove.batch.item.file.delimited;

import com.github.sourcegroove.batch.item.file.FileLayout;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;

import java.util.ArrayList;
import java.util.List;

public class DelimitedFileLayout implements FileLayout {
    private List<DelimitedRecordLayout> recordLayouts = new ArrayList<>();
    private int linesToSkip = 0;
    private String delimiter = ",";
    private char qualifier = '"';
    private ResourceAwareItemWriterItemStream itemWriter;
    private ResourceAwareItemReaderItemStream itemReader;

    public List<DelimitedRecordLayout> getRecordLayouts() {
        return recordLayouts;
    }
    public void setRecordLayouts(List<DelimitedRecordLayout> recordLayouts) {
        this.recordLayouts = recordLayouts;
    }
    public int getLinesToSkip() {
        return linesToSkip;
    }
    public void setLinesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
    }
    public String getDelimiter() {
        return delimiter;
    }
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
    public char getQualifier() {
        return qualifier;
    }
    public void setQualifier(char qualifier) {
        this.qualifier = qualifier;
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
