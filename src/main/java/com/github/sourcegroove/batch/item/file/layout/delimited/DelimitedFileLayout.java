package com.github.sourcegroove.batch.item.file.layout.delimited;

import com.github.sourcegroove.batch.item.file.FileLayoutFieldExtractor;
import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import org.apache.commons.collections4.CollectionUtils;
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

public class DelimitedFileLayout implements FileLayout {
    private List<DelimitedRecordLayout> recordLayouts = new ArrayList<>();
    private int linesToSkip = 0;
    private String delimiter = ",";
    private char qualifier = '"';

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
    public void setQualifier(char qualifier) {
        this.qualifier = qualifier;
    }
    public void setLinesToSkip(int linesToSkip){
        this.linesToSkip = linesToSkip;
    }
    public void setRecordLayouts(List<DelimitedRecordLayout> recordLayouts) {
        this.recordLayouts = recordLayouts;
    }

    @Override
    public int getLinesToSkip() {
        return this.linesToSkip;
    }
    @Override
    public LineMapper getLineMapper(){
        Map<String, FieldSetMapper> mappers = new HashMap<>();
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        for(DelimitedRecordLayout recordLayout : this.recordLayouts) {
            mappers.put(recordLayout.getPrefix(), getFieldSetMapper(recordLayout.getTargetType(), recordLayout.getEditors()));
            tokenizers.put(recordLayout.getPrefix(), getLineTokenizer(recordLayout.getFieldNames()));
        }
        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setFieldSetMappers(mappers);
        lineMapper.setTokenizers(tokenizers);
        return lineMapper;
    }
    @Override
    public LineAggregator getLineAggregator(Class targetType){
        DelimitedRecordLayout recordLayout = this.recordLayouts.stream()
                    .filter(r -> r.getTargetType() == targetType)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported record target type " + targetType + ". Is it included in the file format?"));
        return getLineAggregator(recordLayout.getTargetType());
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
    private LineAggregator getLineAggregator(List<String> fieldNames, Map<Class<?>, PropertyEditor> editors){
        BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
        extractor.setNames(fieldNames.toArray(new String[fieldNames.size()]));

        FileLayoutFieldExtractor fieldExtractor = new FileLayoutFieldExtractor();
        fieldExtractor.setFieldExtractor(extractor);
        fieldExtractor.setCustomEditors(editors);

        DelimitedLineAggregator aggregator = new DelimitedLineAggregator();
        aggregator.setFieldExtractor(extractor);
        aggregator.setDelimiter(this.delimiter);
        return aggregator;
    }
    private LineTokenizer getLineTokenizer(List<String> fieldNames) {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(fieldNames.toArray(new String[fieldNames.size()]));
        tokenizer.setQuoteCharacter(this.qualifier);
        return tokenizer;
    }

}
