package com.github.sourcegroove.batch.item.file;

import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import com.github.sourcegroove.batch.item.file.layout.RecordLayout;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class FileLayoutItemReader<T> extends FlatFileItemReader<T> {
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
        Map<String, FieldSetMapper> mappers = new HashMap<>();
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        for(RecordLayout recordLayout : fileLayout.getRecordLayouts()) {
            mappers.put(recordLayout.getPrefix(), recordLayout.getFieldSetMapper());
            tokenizers.put(recordLayout.getPrefix(), recordLayout.getLineTokenizer());
        }
        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setFieldSetMappers(mappers);
        lineMapper.setTokenizers(tokenizers);
        
        setLinesToSkip(fileLayout.getLinesToSkip());
        setLineMapper(lineMapper);
    }
   
    
}
