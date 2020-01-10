package com.github.sourcegroove.batch.item.file.layout.delimited;

import com.github.sourcegroove.batch.item.file.decorator.PropertyEditorFieldExtractorDecorator;
import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import com.github.sourcegroove.batch.item.file.writer.CompositeFlatFileItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelimitedFileLayout implements FileLayout {
    private int linesToSkip = 0;
    private char qualifier = '"';
    private String delimiter = ",";
    private List<DelimitedRecordLayout> records = new ArrayList<>();

    public DelimitedFileLayout linesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
        return this;
    }
    public DelimitedFileLayout qualifier(char qualifier) {
        this.qualifier = qualifier;
        return this;
    }
    public DelimitedFileLayout delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }
    public DelimitedRecordLayout record(Class targetType) {
        this.records.add(new DelimitedRecordLayout(targetType, this));
        return this.records.get(this.records.size() - 1);
    }
    public FlatFileItemReader getItemReader() {
        DelimitedRecordLayout recordLayout = this.records.get(0);
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(recordLayout.getColumns().toArray(new String[recordLayout.getColumns().size()]));
        tokenizer.setQuoteCharacter(this.qualifier);
        tokenizer.setDelimiter(this.delimiter);

        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(recordLayout.getTargetType());
        fieldSetMapper.setCustomEditors(recordLayout.getEditors());

        DefaultLineMapper lineMapper = new DefaultLineMapper();
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(tokenizer);

        FlatFileItemReader reader = new FlatFileItemReader();
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(this.linesToSkip);
        return reader;
    }

    public CompositeFlatFileItemWriter getItemWriter() {
        Map<Class, LineAggregator> lineAggregators = new HashMap<>();
        for(DelimitedRecordLayout recordLayout : this.records){
            BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
            extractor.setNames(recordLayout.getColumns().toArray(new String[recordLayout.getColumns().size()]));
            PropertyEditorFieldExtractorDecorator fieldExtractor = new PropertyEditorFieldExtractorDecorator();
            fieldExtractor.setFieldExtractor(extractor);
            fieldExtractor.setCustomEditors(recordLayout.getEditors());
            DelimitedLineAggregator aggregator = new DelimitedLineAggregator();
            aggregator.setFieldExtractor(extractor);
            aggregator.setDelimiter(this.delimiter);
            lineAggregators.put(recordLayout.getTargetType(), aggregator);
        }

        CompositeFlatFileItemWriter writer = new CompositeFlatFileItemWriter();
        writer.setLineAggregators(lineAggregators);
        return writer;
    }
}
