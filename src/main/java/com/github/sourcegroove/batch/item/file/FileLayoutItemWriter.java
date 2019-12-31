package com.github.sourcegroove.batch.item.file;

import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import com.github.sourcegroove.batch.item.file.layout.RecordLayout;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.util.Assert;

public class FileLayoutItemWriter<T> extends FlatFileItemWriter<T> {
    
    private FileLayout fileLayout;

    public void setFileLayout(FileLayout fileLayout){
        this.fileLayout = fileLayout;
        this.configure();
    }

    @Override
    public void afterPropertiesSet() throws Exception{
        Assert.notNull(this.fileLayout, "The 'fileLayout' property must be set.");
        super.afterPropertiesSet();
    }
    
    private void configure(){
        RecordLayout recordLayout = fileLayout.getRecordLayouts().get(0);
        setLineAggregator(recordLayout.getLineAggregator());
    }
    
    
}
