package com.github.sourcegroove.batch.item.file.delimited.builder;

import com.github.sourcegroove.batch.item.file.CompositeFlatFileItemWriter;
import com.github.sourcegroove.batch.item.file.FileLayoutFieldExtractor;
import com.github.sourcegroove.batch.item.file.delimited.DelimitedFileLayout;
import com.github.sourcegroove.batch.item.file.delimited.DelimitedRecordLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelimitedFileLayoutBuilder {
    protected static final Log log = LogFactory.getLog(DelimitedFileLayoutBuilder.class);

    private DelimitedFileLayout layout = new DelimitedFileLayout();
    private DelimitedRecordLayoutBuilder recordLayoutBuilder;

    public DelimitedFileLayout build(){
        this.layout.getRecordLayouts().add(this.recordLayoutBuilder.build());
        this.layout.setItemWriter(getItemWriter(this.layout));
        this.layout.setItemReader(getItemReader(this.layout));
        return this.layout;
    }
    public DelimitedFileLayoutBuilder delimiter(String delimiter){
        this.layout.setDelimiter(delimiter);
        return this;
    }
    public DelimitedFileLayoutBuilder qualifier(char qualifier){
        this.layout.setQualifier(qualifier);
        return this;
    }
    public DelimitedFileLayoutBuilder linesToSkip(int linesToSkip){
        this.layout.setLinesToSkip(linesToSkip);
        return this;
    }
    public DelimitedRecordLayoutBuilder record(Class targetType){
        if(this.recordLayoutBuilder != null){
            throw new IllegalArgumentException("Record type was already set - only one allowed for delimited file layouts");
        }
        this.recordLayoutBuilder = new DelimitedRecordLayoutBuilder(this);
        this.recordLayoutBuilder.targetType(targetType);
        return this.recordLayoutBuilder;
    }

    public static FlatFileItemReader getItemReader(DelimitedFileLayout layout){
        return new DelimitedItemReaderBuilder()
                .setLayout(layout)
                .build();
    }
    public static CompositeFlatFileItemWriter getItemWriter(DelimitedFileLayout layout) {
        return new DelimitedItemWriterBuilder()
                .setLayout(layout)
                .build();
    }


}
