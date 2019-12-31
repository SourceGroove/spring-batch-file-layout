package com.bitblox.batch.item.file;

import com.bitblox.batch.item.file.layout.DelimitedFileLayout;
import com.bitblox.batch.item.file.layout.FileLayout;
import com.bitblox.batch.item.file.layout.FixedWidthRecordLayout;
import com.bitblox.batch.item.file.layout.RecordLayout;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
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
            mappers.put(recordLayout.getPrefix(), getFieldSetMapper(recordLayout));
            tokenizers.put(recordLayout.getPrefix(), getLineTokenizer(recordLayout));
        }
        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setFieldSetMappers(mappers);
        lineMapper.setTokenizers(tokenizers);
        
        setLinesToSkip(fileLayout.getLinesToSkip());
        setLineMapper(lineMapper);
        
    }
    protected FieldSetMapper getFieldSetMapper(RecordLayout recordLayout){
        BeanWrapperFieldSetMapper mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(recordLayout.getTargetType());
        mapper.setCustomEditors(recordLayout.getEditors());
        return mapper;
    }
    protected LineTokenizer getLineTokenizer(RecordLayout recordLayout){
        if(recordLayout instanceof FixedWidthRecordLayout){
            FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(recordLayout.getFieldNameArray());
            tokenizer.setColumns(((FixedWidthRecordLayout)recordLayout).getFieldRangeArray());
            return tokenizer;
        } else {
            DelimitedFileLayout l = (DelimitedFileLayout)this.fileLayout;
            DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
            tokenizer.setNames(recordLayout.getFieldNameArray());
            tokenizer.setDelimiter(l.getDelimeter());
            tokenizer.setQuoteCharacter(l.getQualifier());
            return tokenizer;
        }
    }
    
}
