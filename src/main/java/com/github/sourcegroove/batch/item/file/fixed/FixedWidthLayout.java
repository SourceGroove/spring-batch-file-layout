package com.github.sourcegroove.batch.item.file.fixed;

import com.github.sourcegroove.batch.item.file.Layout;
import com.github.sourcegroove.batch.item.file.editor.DateEditor;
import com.github.sourcegroove.batch.item.file.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.editor.LocalDateTimeEditor;
import com.github.sourcegroove.batch.item.file.editor.OffsetDateTimeEditor;
import com.github.sourcegroove.batch.item.file.fixed.reader.FixedWidthFileItemReader;
import com.github.sourcegroove.batch.item.file.fixed.writer.FixedWidthBeanWrapperFieldExtractor;
import com.github.sourcegroove.batch.item.file.fixed.writer.FixedWidthFileItemWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.*;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FixedWidthLayout implements Layout {
    protected final Log log = LogFactory.getLog(getClass());
    private int linesToSkip = 0;
    private boolean writeAsStrings = true;
    private Map<Class<?>, PropertyEditor> readEditors = new HashMap<>();
    private Map<Class<?>, PropertyEditor> writeEditors = new HashMap<>();
    private List<FixedWidthRecordLayout> records = new ArrayList<>();
    
    public FixedWidthLayout(){
    }

    public FixedWidthLayout defaultEditors(){
        this.readEditors.putAll(getDefaultEditors());
        this.writeEditors.putAll(getDefaultEditors());
        return this;
    }
    public FixedWidthLayout defaultReadEditors(){
        this.readEditors.putAll(getDefaultEditors());
        return this;
    }
    public FixedWidthLayout defaultWriteEditors(){
        this.writeEditors.putAll(getDefaultEditors());
        return this;
    }
    public FixedWidthLayout linesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
        return this;
    }

    /**
     * Setting this to true will cause the FixedWidthFormatBuilder to create a
     * line format using all strings (no printf decimals - %2d, etc)
     * and the FixedWidthFileFieldExtract to extract values as Strings.
     * 
     * This allows you to pass null objects to those formats that would otherwise cause
     * an exception - i.e. a null Double sent to %2f
     * 
     * Basically - it's safer... and therefore is the default
     * 
     * @param writeAsStrings <tt>true</tt> to write as strings
     * @return this builder
     */
    public FixedWidthLayout writeAsStrings(boolean writeAsStrings) {
        this.writeAsStrings = writeAsStrings;
        return this;
    }
    public boolean isWriteAsStrings() {
        return this.writeAsStrings;
    }

    public FixedWidthLayout editor(Class clazz, PropertyEditor editor) {
        this.readEditors.put(clazz, editor);
        this.writeEditors.put(clazz, editor);
        return this;
    }
    public FixedWidthLayout readEditor(Class clazz, PropertyEditor editor) {
        this.readEditors.put(clazz, editor);
        return this;
    }
    public FixedWidthLayout writeEditor(Class clazz, PropertyEditor editor) {
        this.writeEditors.put(clazz, editor);
        return this;
    }
    
    public FixedWidthRecordLayout footer(Class targetType) {
        return footer(targetType);
    }
    
    public FixedWidthRecordLayout footer(Class targetType, String prefix) {
        if (this.getFooterLayout() != null) {
            throw new IllegalArgumentException("Footer already defined");
        }
        return record(targetType, FixedWidthRecordLayout.RecordType.FOOTER, prefix);
    }

    public FixedWidthRecordLayout header(Class targetType) {
        return header(targetType, null);
    }

    public FixedWidthRecordLayout header(Class targetType, String prefix) {
        if (this.getHeaderLayout() != null) {
            throw new IllegalArgumentException("Header already defined");
        }
        return record(targetType, FixedWidthRecordLayout.RecordType.HEADER, prefix);
    }

    public FixedWidthRecordLayout record(Class targetType) {
        return record(targetType, FixedWidthRecordLayout.RecordType.RECORD, null);
    }

    public FixedWidthRecordLayout record(Class targetType, String prefix) {
        return record(targetType, FixedWidthRecordLayout.RecordType.RECORD, prefix);
    }

    public FixedWidthFileItemWriter getItemWriter() {

        FixedWidthFileItemWriter writer = new FixedWidthFileItemWriter();
        for (FixedWidthRecordLayout recordLayout : this.getRecordLayouts()) {
            writer.setLineAggregator(recordLayout.getTargetType(), getLineAggregator(recordLayout));
        }
        if (this.getHeaderLayout() != null) {
            writer.setHeaderLineAggregator(getLineAggregator(this.getHeaderLayout()));
        }
        if (this.getFooterLayout() != null) {
            writer.setFooterLineAggregator(getLineAggregator(this.getFooterLayout()));
        }
        return writer;
    }
    private LineAggregator getLineAggregator(FixedWidthRecordLayout recordLayout) {
        FixedWidthBeanWrapperFieldExtractor fieldExtractor = new FixedWidthBeanWrapperFieldExtractor();
        fieldExtractor.setExtractAsStrings(this.writeAsStrings);
        fieldExtractor.setNames(recordLayout.getMappableColumns());
        fieldExtractor.setFormats(recordLayout.getMappableColumnFormats());
        fieldExtractor.setCustomEditors(getWriteEditors(recordLayout));
        
        FormatterLineAggregator aggregator = new FormatterLineAggregator();
        aggregator.setFieldExtractor(fieldExtractor);
        aggregator.setFormat(recordLayout.getFormat());

        return aggregator;
    }

    public FixedWidthFileItemReader getItemReader() {
        Map<String, FieldSetMapper> mappers = new HashMap<>();
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        for (FixedWidthRecordLayout recordLayout : this.records) {
            BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
            fieldSetMapper.setDistanceLimit(0);
            // we need this to always be false so it doesn't blow up on our 'filler' columns when trying to map
            fieldSetMapper.setStrict(false);
            fieldSetMapper.setTargetType(recordLayout.getTargetType());
            fieldSetMapper.setCustomEditors(getReadEditors(recordLayout));
            FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setStrict(recordLayout.isStrict());
            tokenizer.setNames(recordLayout.getColumns());
            tokenizer.setColumns(recordLayout.getColumnRanges());
            mappers.put(recordLayout.getPrefix(), fieldSetMapper);
            tokenizers.put(recordLayout.getPrefix(), tokenizer);
        }

        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setFieldSetMappers(mappers);
        lineMapper.setTokenizers(tokenizers);

        FixedWidthFileItemReader reader = new FixedWidthFileItemReader();
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(this.linesToSkip);

        return reader;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("FixedWidthLayout:\n")
                .append("new FixedWidthLayout()\n")
                .append("    .linesToSkip(").append(this.linesToSkip).append(")\n");
        this.records.forEach(r -> str.append(r.toString()));
        str.append(".layout();");

        return str.toString();
    }
    

    private Map<Class<?>, PropertyEditor> getReadEditors(FixedWidthRecordLayout recordLayout){
        Map<Class<?>, PropertyEditor> customEditors = new HashMap<>();
        customEditors.putAll(this.readEditors);
        customEditors.putAll(recordLayout.getReadEditors());
        return customEditors;
    }
    private Map<Class<?>, PropertyEditor> getWriteEditors(FixedWidthRecordLayout recordLayout){
        Map<Class<?>, PropertyEditor> customEditors = new HashMap<>();
        customEditors.putAll(this.writeEditors);
        customEditors.putAll(recordLayout.getWriteEditors());
        return customEditors;
    }
    private FixedWidthRecordLayout record(Class targetType, FixedWidthRecordLayout.RecordType recordType, String prefix) {
        FixedWidthRecordLayout record = new FixedWidthRecordLayout(targetType, this);
        if (recordType != null) {
            record.recordType(recordType);
        }
        if (prefix != null) {
            record.prefix(prefix);
        }
        this.records.add(record);
        return this.records.get(this.records.size() - 1);
    }

    private List<FixedWidthRecordLayout> getRecordLayouts() {
        return this.records.stream()
                .filter(r -> r.getRecordType() == FixedWidthRecordLayout.RecordType.RECORD)
                .collect(Collectors.toList());
    }

    private FixedWidthRecordLayout getHeaderLayout() {
        return this.records.stream()
                .filter(r -> r.getRecordType() == FixedWidthRecordLayout.RecordType.HEADER)
                .findFirst()
                .orElse(null);
    }

    private FixedWidthRecordLayout getFooterLayout() {
        return this.records.stream()
                .filter(r -> r.getRecordType() == FixedWidthRecordLayout.RecordType.FOOTER)
                .findFirst()
                .orElse(null);
    }
    
    private Map<Class<?>, PropertyEditor> getDefaultEditors(){
        Map<Class<?>, PropertyEditor> map = new HashMap<>();
        map.put(LocalDate.class, new LocalDateEditor());
        map.put(LocalDateTime.class, new LocalDateTimeEditor());
        map.put(OffsetDateTime.class, new OffsetDateTimeEditor());
        map.put(Date.class, new DateEditor());
        return map;
    }

}
