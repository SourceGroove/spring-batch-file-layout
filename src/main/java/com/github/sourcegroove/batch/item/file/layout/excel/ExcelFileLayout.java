package com.github.sourcegroove.batch.item.file.layout.excel;


import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.file.transform.LineTokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelFileLayout implements FileLayout {
    private List<ExcelRecordLayout> recordLayouts = new ArrayList<>();
    private int linesToSkip = 0;

    public void setLinesToSkip(int linesToSkip){
        this.linesToSkip = linesToSkip;
    }
    public int getLinesToSkip() {
        return this.linesToSkip;
    }

    @Override
    public LineMapper getLineMapper(){
        Map<String, FieldSetMapper> mappers = new HashMap<>();
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        for(ExcelRecordLayout recordLayout : this.recordLayouts) {
            mappers.put(recordLayout.getPrefix(), recordLayout.getFieldSetMapper());
            tokenizers.put(recordLayout.getPrefix(), recordLayout.getLineTokenizer());
        }
        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setFieldSetMappers(mappers);
        lineMapper.setTokenizers(tokenizers);
        return lineMapper;
    }
    @Override
    public LineAggregator getLineAggregator(Class targetType){
        ExcelRecordLayout recordLayout =  this.recordLayouts.stream()
                .filter(r -> r.getTargetType() == targetType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported record target type " + targetType + ". Is it included in the file format?"));
        return recordLayout.getLineAggregator();
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
}
