package com.github.sourcegroove.batch.item.file;

import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.util.Assert;

//AbstractItemCountingItemStreamItemReader
public class FileLayoutItemReader<T> extends FlatFileItemReader<T> {
    protected static final Log log = LogFactory.getLog(FileLayoutItemReader.class);
    private FileLayout fileLayout;

    public void setFileLayout(FileLayout fileLayout){
        this.fileLayout = fileLayout;
        setLineMapper(fileLayout.getLineMapper());
        setLinesToSkip(fileLayout.getLinesToSkip());
    }
    @Override
    public void afterPropertiesSet() throws Exception{
        Assert.notNull(this.fileLayout, "The 'fileLayout' property must be set.");
        Assert.isTrue(this.fileLayout.isValid(), "'fileLayout' is invalid");
        super.afterPropertiesSet();
    }
}
