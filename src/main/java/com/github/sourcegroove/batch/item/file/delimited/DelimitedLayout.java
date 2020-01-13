package com.github.sourcegroove.batch.item.file.delimited;

import com.github.sourcegroove.batch.item.file.delimited.reader.DelimitedFileItemReader;
import com.github.sourcegroove.batch.item.file.delimited.writer.DelimitedFileItemWriter;
import com.github.sourcegroove.batch.item.file.fixed.writer.FixedWidthFileFieldExtractor;
import com.github.sourcegroove.batch.item.file.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.editor.LocalDateTimeEditor;
import com.github.sourcegroove.batch.item.file.Layout;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelimitedLayout implements Layout {
    private int linesToSkip = 0;
    private char qualifier = '"';
    private String delimiter = ",";
    private Class targetType;
    private List<String> columns = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();

    public DelimitedLayout(){
        this.editor(LocalDate.class, new LocalDateEditor());
        this.editor(LocalDateTime.class, new LocalDateTimeEditor());
    }

    public DelimitedLayout linesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
        return this;
    }
    public DelimitedLayout qualifier(char qualifier) {
        this.qualifier = qualifier;
        return this;
    }
    public DelimitedLayout delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }
    public DelimitedLayout editor(Class clazz, PropertyEditor editor){
        this.editors.put(clazz, editor);
        return this;
    }
    public DelimitedLayout record(Class targetType) {
        if(this.targetType != null){
            throw new IllegalArgumentException("Record already defined");
        }
        this.targetType = targetType;
        return this;
    }
    public DelimitedLayout column(String name){
        this.columns.add(name);
        return this;
    }

    public DelimitedLayout layout(){
        return this;
    }

    public DelimitedFileItemReader getItemReader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(getColumns());
        tokenizer.setQuoteCharacter(this.qualifier);
        tokenizer.setDelimiter(this.delimiter);

        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(this.targetType);
        fieldSetMapper.setCustomEditors(this.editors);

        DefaultLineMapper lineMapper = new DefaultLineMapper();
        lineMapper.setFieldSetMapper(fieldSetMapper);
        lineMapper.setLineTokenizer(tokenizer);

        DelimitedFileItemReader reader = new DelimitedFileItemReader();
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(this.linesToSkip);
        return reader;
    }

    public DelimitedFileItemWriter getItemWriter() {

        BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
        extractor.setNames(getColumns());

        FixedWidthFileFieldExtractor fieldExtractor = new FixedWidthFileFieldExtractor();
        fieldExtractor.setFieldExtractor(extractor);
        fieldExtractor.setCustomEditors(this.editors);

        DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator();
        lineAggregator.setFieldExtractor(extractor);
        lineAggregator.setDelimiter(this.delimiter);

        DelimitedFileItemWriter writer = new DelimitedFileItemWriter();
        writer.setLineAggregator(lineAggregator);
        return writer;
    }

    private String[] getColumns(){
        return this.columns.toArray(new String[this.columns.size()]);
    }
}
