package com.github.sourcegroove.batch.item.file.fixed.builder;

import com.github.sourcegroove.batch.item.file.CompositeFlatFileItemWriter;
import com.github.sourcegroove.batch.item.file.FileLayoutFieldExtractor;
import com.github.sourcegroove.batch.item.file.fixed.FixedWidthFileLayout;
import com.github.sourcegroove.batch.item.file.fixed.FixedWidthRecordLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.*;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FixedWidthFileLayoutBuilder {
    protected static final Log log = LogFactory.getLog(FixedWidthFileLayoutBuilder.class);

    private FixedWidthFileLayout layout = new FixedWidthFileLayout();
    private List<FixedWidthRecordLayoutBuilder> recordBuilders = new ArrayList<>();

    public FixedWidthFileLayout build(){
        List<FixedWidthRecordLayout> recordLayouts = this.recordBuilders
                .stream()
                .map(b -> b.build())
                .collect(Collectors.toList());

        this.layout.setRecordLayouts(recordLayouts);
        this.layout.setItemWriter(getItemWriter(this.layout));
        this.layout.setItemReader(getItemReader(this.layout));
        return this.layout;
    }
    public FixedWidthFileLayoutBuilder linesToSkip(int linesToSkip){
        this.layout.setLinesToSkip(linesToSkip);
        return this;
    }
    public FixedWidthRecordLayoutBuilder record(Class targetType){
        FixedWidthRecordLayoutBuilder recordLayoutBuilder = new FixedWidthRecordLayoutBuilder(this);
        recordLayoutBuilder.targetType(targetType);
        this.recordBuilders.add(recordLayoutBuilder);
        return recordLayoutBuilder;
    }

    public static FlatFileItemReader getItemReader(FixedWidthFileLayout layout){
        Map<String, FieldSetMapper> mappers = new HashMap<>();
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        for(FixedWidthRecordLayout recordLayout : layout.getRecordLayouts()) {

            BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
            fieldSetMapper.setTargetType(recordLayout.getTargetType());
            fieldSetMapper.setCustomEditors(recordLayout.getEditors());

            FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setNames(recordLayout.getFieldNames().toArray(new String[recordLayout.getFieldNames().size()]));
            tokenizer.setColumns(recordLayout.getFieldRanges().toArray(new Range[recordLayout.getFieldRanges().size()]));

            mappers.put(recordLayout.getPrefix(), fieldSetMapper);
            tokenizers.put(recordLayout.getPrefix(), tokenizer);
        }

        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setFieldSetMappers(mappers);
        lineMapper.setTokenizers(tokenizers);

        FlatFileItemReader reader = new FlatFileItemReader();
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(layout.getLinesToSkip());
        return reader;
    }

    public static CompositeFlatFileItemWriter getItemWriter(FixedWidthFileLayout layout) {
        Map<Class, LineAggregator> lineAggregators = new HashMap<>();
        for(FixedWidthRecordLayout recordLayout : layout.getRecordLayouts()){
            lineAggregators.put(recordLayout.getTargetType(), getLineAggregator(recordLayout.getFieldNames(), recordLayout.getFormat(), recordLayout.getEditors()));
        }
        CompositeFlatFileItemWriter writer = new CompositeFlatFileItemWriter();
        writer.setLineAggregators(lineAggregators);
        return writer;
    }

    public static LineAggregator getLineAggregator(List<String> fieldNames, String format, Map<Class<?>, PropertyEditor> editors){
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


}
