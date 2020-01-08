package com.github.sourcegroove.batch.item.file.layout.fixed;

import com.github.sourcegroove.batch.item.file.FileLayoutFieldExtractor;
import com.github.sourcegroove.batch.item.file.layout.RecordLayout;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.*;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedWidthRecordLayout implements RecordLayout {

    private FixedWidthFileLayout layout;
    private String prefix = "*";
    private Class targetType = null;
    private List<String> fieldNames = new ArrayList<>();
    private List<Range> fieldRanges = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();
    private StringFormatBuilder format = new StringFormatBuilder();

    private FieldSetMapper mapper;
    private LineAggregator lineAggregator;
    private LineTokenizer lineTokenizer;

    public static FixedWidthRecordLayout of(FixedWidthFileLayout layout, Class targetType){
        FixedWidthRecordLayout r = new FixedWidthRecordLayout();
        r.layout = layout;
        r.targetType = targetType;
        return r;
    }
    public FixedWidthFileLayout build(){
        this.mapper = null;
        this.lineAggregator = null;
        this.lineTokenizer = null;
        return this.layout;
    }
    public FixedWidthFileLayout and(){
        return this.layout;
    }
    public FixedWidthRecordLayout prefix(String prefix){
        this.prefix = prefix;
        return this;
    }
    public FixedWidthRecordLayout column(String name, int start, int end){
        return column(name, start, end, StringFormatBuilder.Format.STRING);
    }
    public FixedWidthRecordLayout column(String name, int start, int end, StringFormatBuilder.Format format){
        Range range = new Range(start, end);
        this.format.append(range, format);
        this.fieldNames.add(name);
        this.fieldRanges.add(range);
        return this;
    }
    public FixedWidthRecordLayout editor(Class<?> type, PropertyEditor editor){
        this.editors.put(type, editor);
        return this;
    }
    public Class getTargetType(){
        return this.targetType;
    }
    public String getPrefix(){
        return this.prefix;
    }
    public FieldSetMapper getFieldSetMapper(){
        if(this.mapper == null) {
            BeanWrapperFieldSetMapper mapper = new BeanWrapperFieldSetMapper();
            mapper.setTargetType(this.targetType);
            mapper.setCustomEditors(this.editors);
            this.mapper = mapper;
        }
        return this.mapper;
    }
    public LineAggregator getLineAggregator(){
        if(this.lineAggregator == null) {
            BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
            extractor.setNames(fieldNames.toArray(new String[this.fieldNames.size()]));

            FileLayoutFieldExtractor fieldExtractor = new FileLayoutFieldExtractor();
            fieldExtractor.setFieldExtractor(extractor);
            fieldExtractor.setCustomEditors(this.editors);

            FormatterLineAggregator aggregator = new FormatterLineAggregator();
            aggregator.setFieldExtractor(fieldExtractor);
            aggregator.setFormat(this.format.toString());

            this.lineAggregator = aggregator;
        }
        return this.lineAggregator;
    }
    public LineTokenizer getLineTokenizer(){
        if(this.lineTokenizer == null) {
            FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(fieldNames.toArray(new String[this.fieldNames.size()]));
            tokenizer.setColumns(fieldRanges.toArray(new Range[fieldRanges.size()]));
            this.lineTokenizer = tokenizer;
        }
        return this.lineTokenizer;
    }

}
