package com.github.sourcegroove.batch.item.file.layout.fixed;

import com.github.sourcegroove.batch.item.file.FileLayoutFieldExtractor;
import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.*;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedWidthFileLayout implements FileLayout {
    protected static final Log log = LogFactory.getLog(FixedWidthFileLayout.class);
    private int linesToSkip = 0;
    private List<FixedWidthRecordLayout> recordLayouts = new ArrayList<>();

    public int getLinesToSkip() {
        return linesToSkip;
    }
    public void setLinesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
    }
    public void setRecordLayouts(List<FixedWidthRecordLayout> recordLayouts) {
        this.recordLayouts = recordLayouts;
    }

    @Override
    public LineMapper getLineMapper(){
        Map<String, FieldSetMapper> mappers = new HashMap<>();
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        for(FixedWidthRecordLayout recordLayout : this.recordLayouts) {
            mappers.put(recordLayout.getPrefix(), getFieldSetMapper(recordLayout.getTargetType(), recordLayout.getEditors()));
            tokenizers.put(recordLayout.getPrefix(), getLineTokenizer(recordLayout.getFieldNames(), recordLayout.getFieldRanges()));
        }
        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setFieldSetMappers(mappers);
        lineMapper.setTokenizers(tokenizers);
        return lineMapper;
    }
    @Override
    public LineAggregator getLineAggregator(Class targetType){
        FixedWidthRecordLayout recordLayout =  this.recordLayouts.stream()
                .filter(r -> r.getTargetType() == targetType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported record target type " + targetType + ". Is it included in the file format?"));
        return getLineAggregator(recordLayout.getFieldNames(), recordLayout.getFormat(), recordLayout.getEditors());
    }
    @Override
    public boolean isValid(){
        return CollectionUtils.isNotEmpty(this.recordLayouts)
                && !this.recordLayouts
                .stream()
                .filter(r -> r.getTargetType() == null)
                .findFirst()
                .isPresent();
    }


    private FieldSetMapper getFieldSetMapper(Class targetType, Map<Class<?>, PropertyEditor> editors){
        BeanWrapperFieldSetMapper mapper = new BeanWrapperFieldSetMapper();
        mapper.setTargetType(targetType);
        mapper.setCustomEditors(editors);
        return mapper;
    }
    private  LineAggregator getLineAggregator(List<String> fieldNames, String format, Map<Class<?>, PropertyEditor> editors){
        BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
        extractor.setNames(fieldNames.toArray(new String[fieldNames.size()]));

        FileLayoutFieldExtractor fieldExtractor = new FileLayoutFieldExtractor();
        fieldExtractor.setFieldExtractor(extractor);
        fieldExtractor.setCustomEditors(editors);

        FormatterLineAggregator aggregator = new FormatterLineAggregator();
        aggregator.setFieldExtractor(fieldExtractor);
        aggregator.setFormat(format);

        return aggregator;
    }
    private LineTokenizer getLineTokenizer(List<String> fieldNames, List<Range> fieldRanges){
        FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
        tokenizer.setNames(fieldNames.toArray(new String[fieldNames.size()]));
        tokenizer.setColumns(fieldRanges.toArray(new Range[fieldRanges.size()]));
        return tokenizer;
    }


}
