package com.github.sourcegroove.batch.item.file;

import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.support.AbstractFileItemWriter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.stream.Collectors;

public class FileLayoutItemWriter<T> extends AbstractFileItemWriter<T> {
    protected static final Log log = LogFactory.getLog(FileLayoutItemWriter.class);
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
        Assert.isTrue(this.fileLayout.isValid(), "'fileLayout' is invalid");
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
        return fileLayout
                .getLineAggregator(item.getClass())
                .aggregate(item) + this.lineSeparator;
    }
}
