package com.github.sourcegroove.batch.item.file.model.fixed;

import com.github.sourcegroove.batch.item.file.FileLayoutFieldExtractor;
import com.github.sourcegroove.batch.item.file.model.RecordLayout;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.*;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedWidthRecordLayout implements RecordLayout {
    private enum ColumnType {
        STRING,
        INTEGER,
        ZD, // left aligned number
        DECIMAL,
        DATE,
        MONTH,
        CONSTANT;
    }
    private FixedWidthFileLayout layout;
    private String prefix = "*";
    private Class targetType = null;
    private List<String> fieldNames = new ArrayList<>();
    private List<Range> fieldRanges = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();

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
        this.fieldNames.add(name);
        this.fieldRanges.add(new Range(start, end));
        return this;
    }
    public FixedWidthRecordLayout editor(Class<?> type, PropertyEditor editor){
        this.editors.put(type, editor);
        return this;
    }

    protected void setFieldRanges(List<Range> fieldRanges){
        this.fieldRanges = fieldRanges;
    }
    protected void setFieldNames(List<String> fieldNames){
        this.fieldNames = fieldNames;
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
            extractor.setNames(getFieldNameArray());

            FileLayoutFieldExtractor fieldExtractor = new FileLayoutFieldExtractor();
            fieldExtractor.setFieldExtractor(extractor);
            fieldExtractor.setCustomEditors(this.editors);

            FormatterLineAggregator aggregator = new FormatterLineAggregator();
            aggregator.setFieldExtractor(fieldExtractor);
            aggregator.setFormat(getFormat());

            this.lineAggregator = aggregator;
        }
        return this.lineAggregator;
    }
    public LineTokenizer getLineTokenizer(){
        if(this.lineTokenizer == null) {
            FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(getFieldNameArray());
            tokenizer.setColumns(getFieldRangeArray());
            this.lineTokenizer = tokenizer;
        }
        return this.lineTokenizer;
    }

    public String getFormat(){
        StringBuilder format = new StringBuilder();
        Range previous = null;
        for(int i = 0; i < this.fieldRanges.size(); i++){
            Range range = this.fieldRanges.get(i);

            int gap = previous != null ? range.getMin() - 1 - previous.getMax() : 0;
            if(gap > 0){
                format.append(getFormat(ColumnType.CONSTANT, gap));
            }

            int width = range.getMax() - range.getMin() + 1;
            format.append(getFormat(ColumnType.STRING, width));
            previous = range;
        }
        return format.toString();
    }

    private String getFormat(ColumnType type, int width){
        String fmt = null;
        if (type == ColumnType.CONSTANT) {
            fmt = StringUtils.rightPad(" ", width);

        } else if (type == ColumnType.DECIMAL) {
            fmt = "%0" + width + ".2f";

        } else if (type == ColumnType.INTEGER) {
            fmt = "%0" + width + "d";

        } else if (type == ColumnType.ZD){
            fmt = "%-" + width + "." + width + "s";

        } else if (type == ColumnType.DATE) {
            fmt = "%tY%<tm%<td";

        } else if (type == ColumnType.MONTH) {
            fmt = "%tm%<tY";

        } else {
            String flags = "-";
            fmt = "%" + flags + width + "." + width + "s";
        }
        return fmt;
    }

    private String[] getFieldNameArray(){
        return fieldNames.toArray(new String[this.fieldNames.size()]);
    }
    private Range[] getFieldRangeArray(){
        return fieldRanges.toArray(new Range[fieldRanges.size()]);
    }
}
