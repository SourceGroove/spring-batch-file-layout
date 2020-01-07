package com.github.sourcegroove.batch.item.file;

import com.github.sourcegroove.batch.item.file.model.FileLayout;
import com.github.sourcegroove.batch.item.file.model.RecordLayout;
import lombok.extern.java.Log;
import org.springframework.batch.item.support.AbstractFileItemWriter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.stream.Collectors;

@Log
public class FileLayoutItemWriter<T> extends  AbstractFileItemWriter<T> {

    private FileLayout fileLayout;

    public FileLayoutItemWriter() {
        this.setExecutionContextName(ClassUtils.getShortName(FileLayoutItemWriter.class));
    }

    public void setFileLayout(FileLayout fileLayout){
        this.fileLayout = fileLayout;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.fileLayout, "The 'fileLayout' property must be set.");
        if (this.append) {
            this.shouldDeleteIfExists = false;
        }
    }

    @Override
    protected String doWrite(List<? extends T> items) {
        return items
                .stream()
                .map(i -> aggregate(i))
                .collect(Collectors.joining());
    }

    private String aggregate(T item){
        RecordLayout recordLayout = this.fileLayout.getRecordLayout(item.getClass());
        if(recordLayout == null){
            throw new IllegalArgumentException("Unsupported record target type " + item.getClass() + ". Is it included in the file format?");
        }
        return recordLayout.getLineAggregator().aggregate(item) + this.lineSeparator;
    }

}
