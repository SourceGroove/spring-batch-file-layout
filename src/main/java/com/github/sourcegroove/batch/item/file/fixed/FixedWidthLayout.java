package com.github.sourcegroove.batch.item.file.fixed;

import com.github.sourcegroove.batch.item.file.Layout;
import com.github.sourcegroove.batch.item.file.fixed.reader.FixedWidthFileItemReader;
import com.github.sourcegroove.batch.item.file.fixed.writer.FixedWidthFileFieldExtractor;
import com.github.sourcegroove.batch.item.file.fixed.writer.FixedWidthFileItemWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedWidthLayout implements Layout {
    protected final Log log = LogFactory.getLog(getClass());
    private int linesToSkip = 0;
    private List<FixedWidthRecordLayout> records = new ArrayList<>();

    public FixedWidthLayout linesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
        return this;
    }
    public FixedWidthRecordLayout record(Class targetType) {
        this.records.add(new FixedWidthRecordLayout(targetType, this));
        return this.records.get(this.records.size() - 1);
    }
    public FixedWidthFileItemWriter getItemWriter() {
        Map<Class, LineAggregator> lineAggregators = new HashMap<>();
        for(FixedWidthRecordLayout recordLayout : this.records){
            BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
            extractor.setNames(recordLayout.getColumns());

            FixedWidthFileFieldExtractor fieldExtractor = new FixedWidthFileFieldExtractor();
            fieldExtractor.setFieldExtractor(extractor);
            fieldExtractor.setCustomEditors(recordLayout.getEditors());

            FormatterLineAggregator aggregator = new FormatterLineAggregator();
            aggregator.setFieldExtractor(fieldExtractor);
            aggregator.setFormat(recordLayout.getFormat());
            lineAggregators.put(recordLayout.getTargetType(), aggregator);
        }
        FixedWidthFileItemWriter writer = new FixedWidthFileItemWriter();
        writer.setLineAggregators(lineAggregators);
        return writer;
    }
    public FixedWidthFileItemReader getItemReader() {
        Map<String, FieldSetMapper> mappers = new HashMap<>();
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        for(FixedWidthRecordLayout recordLayout : this.records) {
            BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
            fieldSetMapper.setDistanceLimit(0);
            fieldSetMapper.setTargetType(recordLayout.getTargetType());
            fieldSetMapper.setCustomEditors(recordLayout.getEditors());
            FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(recordLayout.getColumns());
            tokenizer.setColumns(recordLayout.getColumnRanges());
            mappers.put(recordLayout.getPrefix(), fieldSetMapper);
            tokenizers.put(recordLayout.getPrefix(), tokenizer);
            
            for(int i = 0; i < recordLayout.getColumnRanges().length; i++){
                log.info("Created reader with column: "
                        + recordLayout.getColumns()[i]
                        + ": " 
                        + recordLayout.getColumnRanges()[i].getMin() 
                        + "-" 
                        + recordLayout.getColumnRanges()[i].getMax());
            }
            
        }
        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setFieldSetMappers(mappers);
        lineMapper.setTokenizers(tokenizers);

        FixedWidthFileItemReader reader = new FixedWidthFileItemReader();
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(this.linesToSkip);
        return reader;
    }

}
