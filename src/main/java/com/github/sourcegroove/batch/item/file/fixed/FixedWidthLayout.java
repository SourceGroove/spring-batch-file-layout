package com.github.sourcegroove.batch.item.file.fixed;

import com.github.sourcegroove.batch.item.file.Layout;
import com.github.sourcegroove.batch.item.file.RecordLayout;
import com.github.sourcegroove.batch.item.file.RecordType;
import com.github.sourcegroove.batch.item.file.format.FormatAwareFieldExtractor;
import com.github.sourcegroove.batch.item.file.format.FormatAwareFieldSetMapper;
import com.github.sourcegroove.batch.item.file.format.editor.EditorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.file.transform.LineTokenizer;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedWidthLayout implements Layout {
    protected final Log log = LogFactory.getLog(getClass());
    private int linesToSkip = 0;
    private Map<Class<?>, PropertyEditor> readEditors = new HashMap<>();
    private Map<Class<?>, PropertyEditor> writeEditors = new HashMap<>();
    private List<FixedWidthRecordLayout> records = new ArrayList<>();

    public List<RecordLayout> getRecords(){
        return new ArrayList<RecordLayout>(this.records);
    }
    
    public FixedWidthLayout dateFormat(String dateFormat) {
        this.readEditors.putAll(EditorFactory.getDefaultEditors(dateFormat));
        this.writeEditors.putAll(EditorFactory.getDefaultEditors(dateFormat));
        return this;
    }

    public FixedWidthLayout linesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
        return this;
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
        this.records.stream().filter(r -> r.getRecordType() == RecordType.HEADER)
                .findFirst()
                .ifPresent(r -> new IllegalArgumentException("Footer already defined"));
        return record(targetType, RecordType.FOOTER, prefix);
    }
    public FixedWidthRecordLayout header(Class targetType) {
        return header(targetType, null);
    }
    public FixedWidthRecordLayout header(Class targetType, String prefix) {
        this.records.stream().filter(r -> r.getRecordType() == RecordType.HEADER)
                .findFirst()
                .ifPresent(r -> new IllegalArgumentException("Header already defined"));
        return record(targetType, RecordType.HEADER, prefix);
    }

    public FixedWidthRecordLayout record(Class targetType) {
        return record(targetType, RecordType.DETAIL, null);
    }

    public FixedWidthRecordLayout record(Class targetType, String prefix) {
        return record(targetType, RecordType.DETAIL, prefix);
    }

    public Map<Class<?>, PropertyEditor> getReadEditors() {
        return this.readEditors;
    }

    public Map<Class<?>, PropertyEditor> getWriteEditors() {
        return this.writeEditors;
    }


    public FixedWidthFileItemWriter getItemWriter() {
        FixedWidthFileItemWriter writer = new FixedWidthFileItemWriter();
        for (FixedWidthRecordLayout record : this.records) {
            LineAggregator aggregator = getLineAggregator(record);
            if (record.getRecordType() == RecordType.HEADER) {
                writer.setHeaderLineAggregator(aggregator);
            } else if (record.getRecordType() == RecordType.FOOTER) {
                writer.setFooterLineAggregator(aggregator);
            } else {
                writer.setLineAggregator(record.getTargetType(), aggregator);
            }
        }
        return writer;
    }

    private LineAggregator getLineAggregator(FixedWidthRecordLayout record) {
        Map<Class<?>, PropertyEditor> editors = new HashMap<>();
        editors.putAll(this.getWriteEditors());
        editors.putAll(record.getWriteEditors());
        FormatterLineAggregator aggregator = new FormatterLineAggregator();
        aggregator.setFormat(record.getFormat());
        FormatAwareFieldExtractor fieldExtractor = new FormatAwareFieldExtractor<>();
        fieldExtractor.setNames(record.getMappableColumns());
        fieldExtractor.setFormats(record.getMappableColumnFormats());
        fieldExtractor.setCustomEditors(editors);
        aggregator.setFieldExtractor(fieldExtractor);
        return aggregator;
    }

    public FixedWidthFileItemReader getItemReader() {
        Map<String, FieldSetMapper> mappers = new HashMap<>();
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        for (FixedWidthRecordLayout record : this.records) {
            Map<Class<?>, PropertyEditor> editors = new HashMap<>();
            editors.putAll(this.getReadEditors());
            editors.putAll(record.getReadEditors());
            FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setStrict(record.isStrict());
            tokenizer.setNames(record.getColumnNames());
            tokenizer.setColumns(record.getColumnRanges());
            tokenizers.put(record.getPrefix(), tokenizer);
            
            FormatAwareFieldSetMapper fieldSetMapper = new FormatAwareFieldSetMapper();
            fieldSetMapper.setTargetType(record.getTargetType());
            fieldSetMapper.setNames(record.getColumnNames());
            fieldSetMapper.setFormats(record.getColumnFormats());
            fieldSetMapper.setCustomEditors(editors);
            mappers.put(record.getPrefix(), fieldSetMapper);
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


    private FixedWidthRecordLayout record(Class targetType, RecordType recordType, String prefix) {
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


}
