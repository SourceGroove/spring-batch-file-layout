package com.github.sourcegroove.batch.item.file.delimited.builder;

import com.github.sourcegroove.batch.item.file.delimited.DelimitedFileLayout;
import com.github.sourcegroove.batch.item.file.delimited.DelimitedRecordLayout;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

public class DelimitedItemReaderBuilder {

    private DelimitedFileLayout layout;

    public DelimitedItemReaderBuilder setLayout(DelimitedFileLayout layout){
        this.layout = layout;
        return this;
    }

    public FlatFileItemReader build(){
        DelimitedRecordLayout recordLayout = layout.getRecordLayouts().get(0);
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(recordLayout.getFieldNames().toArray(new String[recordLayout.getFieldNames().size()]));
        tokenizer.setQuoteCharacter(layout.getQualifier());
        tokenizer.setDelimiter(layout.getDelimiter());

        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(recordLayout.getTargetType());
        fieldSetMapper.setCustomEditors(recordLayout.getEditors());

        DefaultLineMapper lineMapper = new DefaultLineMapper();
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(tokenizer);

        FlatFileItemReader reader = new FlatFileItemReader();
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(layout.getLinesToSkip());
        return reader;
    }
}
