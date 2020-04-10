package com.github.sourcegroove.batch.item.file.fixed.reader;

import com.github.sourcegroove.batch.item.file.fixed.FixedWidthLayout;
import com.github.sourcegroove.batch.item.file.fixed.FixedWidthRecordLayout;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

public class FixedWidthFileItemReaderFactory {

    public static FixedWidthFileItemReader getItemReader(FixedWidthLayout file) {
        Map<String, FieldSetMapper> mappers = new HashMap<>();
        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        
        for (FixedWidthRecordLayout record : file.getRecordLayouts()) {
            Map<Class<?>, PropertyEditor> editors = new HashMap<>();
            editors.putAll(file.getReadEditors());
            editors.putAll(record.getReadEditors());
            
            FixedWidthFormatFieldSetMapper fieldSetMapper = new FixedWidthFormatFieldSetMapper();
            fieldSetMapper.setTargetType(record.getTargetType());
            fieldSetMapper.setNames(record.getMappableColumns());
            fieldSetMapper.setFormats(record.getMappableColumnFormats());
            fieldSetMapper.setCustomEditors(editors);
            mappers.put(record.getPrefix(), fieldSetMapper);

            FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setStrict(record.isStrict());
            tokenizer.setNames(record.getMappableColumns());
            tokenizer.setColumns(record.getMappableColumnRanges());
            tokenizers.put(record.getPrefix(), tokenizer);
        }

        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setFieldSetMappers(mappers);
        lineMapper.setTokenizers(tokenizers);

        FixedWidthFileItemReader reader = new FixedWidthFileItemReader();
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(file.getLinesToSkip());

        return reader;
    }

}
