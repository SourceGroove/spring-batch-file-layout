package com.github.sourcegroove.batch.item.file.layout.fixed;

import com.github.sourcegroove.batch.item.file.writer.composite.CompositeFlatFileItemWriter;
import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import com.github.sourcegroove.batch.item.file.writer.composite.CompositeFlatFileFieldExtractor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedWidthFileLayout implements FileLayout {
    private int linesToSkip = 0;
    private List<FixedWidthRecordLayout> records = new ArrayList<>();

    public FixedWidthFileLayout linesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
        return this;
    }
    public FixedWidthRecordLayout record(Class targetType) {
        this.records.add(new FixedWidthRecordLayout(targetType, this));
        return this.records.get(this.records.size() - 1);
    }
    public CompositeFlatFileItemWriter getItemWriter() {
        Map<Class, LineAggregator> lineAggregators = new HashMap<>();
        for(FixedWidthRecordLayout recordLayout : this.records){
            BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
            extractor.setNames(recordLayout.getColumns());

            CompositeFlatFileFieldExtractor fieldExtractor = new CompositeFlatFileFieldExtractor();
            fieldExtractor.setFieldExtractor(extractor);
            fieldExtractor.setCustomEditors(recordLayout.getEditors());

            FormatterLineAggregator aggregator = new FormatterLineAggregator();
            aggregator.setFieldExtractor(fieldExtractor);
            aggregator.setFormat(recordLayout.getFormat());
            lineAggregators.put(recordLayout.getTargetType(), aggregator);
        }
        CompositeFlatFileItemWriter writer = new CompositeFlatFileItemWriter();
        writer.setLineAggregators(lineAggregators);
        return writer;
    }
    public FlatFileItemReader getItemReader() {
        Map<String, FieldSetMapper> mappers = new HashMap<>();
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        for(FixedWidthRecordLayout recordLayout : this.records) {
            BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
            fieldSetMapper.setTargetType(recordLayout.getTargetType());
            fieldSetMapper.setCustomEditors(recordLayout.getEditors());
            FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(recordLayout.getColumns());
            tokenizer.setColumns(recordLayout.getColumnRanges());
            mappers.put(recordLayout.getPrefix(), fieldSetMapper);
            tokenizers.put(recordLayout.getPrefix(), tokenizer);
        }
        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setFieldSetMappers(mappers);
        lineMapper.setTokenizers(tokenizers);
        FlatFileItemReader reader = new FlatFileItemReader();
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(this.linesToSkip);
        return reader;
    }

}
