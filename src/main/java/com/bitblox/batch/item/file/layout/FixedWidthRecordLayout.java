package com.bitblox.batch.item.file.layout;

import com.bitblox.batch.item.file.FileLayoutFieldExtractor;
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

    private Class targetType;
    private String prefix = "*";
    private List<String> fieldNames = new ArrayList<>();
    private List<Range> fieldRanges = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();

    public Map<Class<?>, PropertyEditor> getEditors(){
        return this.editors;
    }
    public List<Range> getFieldRanges(){
        return this.fieldRanges;
    }
    protected void setFieldRanges(List<Range> fieldRanges){
        this.fieldRanges = fieldRanges;
    }
    public List<String> getFieldNames(){
        return this.fieldNames;
    }
    protected void setFieldNames(List<String> fieldNames){
        this.fieldNames = fieldNames;
    }
    public void setTargetType(Class targetType){
        this.targetType = targetType;
    }
    public String getPrefix(){
        return this.prefix;
    }
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }
    public FieldSetMapper getFieldSetMapper(){
        BeanWrapperFieldSetMapper mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(this.targetType);
        mapper.setCustomEditors(this.editors);
        return mapper;
    }
    
    public LineAggregator getLineAggregator(){
        BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
        extractor.setNames(getFieldNameArray());

        FileLayoutFieldExtractor fieldExtractor = new FileLayoutFieldExtractor();
        fieldExtractor.setFieldExtractor(extractor);
        fieldExtractor.setCustomEditors(this.editors);
        
        FormatterLineAggregator aggregator = new FormatterLineAggregator();
        aggregator.setFieldExtractor(fieldExtractor);
        aggregator.setFormat(getFormat());
        
        return aggregator;
    }
    public LineTokenizer getLineTokenizer(){
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames(getFieldNameArray());
        tokenizer.setColumns(getFieldRangeArray());
        return tokenizer;
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
