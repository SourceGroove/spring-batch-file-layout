package com.github.sourcegroove.batch.item.file.fixed.reader;

import com.github.sourcegroove.batch.item.file.fixed.FixedWidthLayout;
import com.github.sourcegroove.batch.item.file.fixed.FixedWidthRecordLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.Range;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

public class FixedWidthFileItemReaderFactory {
    protected static final Log log = LogFactory.getLog(FixedWidthFileItemReaderFactory.class);
    public static FixedWidthFileItemReader getItemReader(FixedWidthLayout file) {
        Map<String, FieldSetMapper> mappers = new HashMap<>();
        Map<String, LineTokenizer> tokenizers = new HashMap<>();

        for (FixedWidthRecordLayout record : file.getRecordLayouts()) {
            Map<Class<?>, PropertyEditor> editors = new HashMap<>();
            editors.putAll(file.getReadEditors());
            editors.putAll(record.getReadEditors());

            FixedLengthTokenizer tokenizer = new FixedLengthTokenizer();
            tokenizer.setStrict(record.isStrict());
            tokenizer.setNames(record.getColumns());
            tokenizer.setColumns(record.getColumnRanges());
            tokenizers.put(record.getPrefix(), tokenizer);

            FixedWidthFormatFieldSetMapper fieldSetMapper = new FixedWidthFormatFieldSetMapper();
            fieldSetMapper.setTargetType(record.getTargetType());
            fieldSetMapper.setNames(record.getColumns());
            fieldSetMapper.setFormats(record.getColumnFormats());
            fieldSetMapper.setCustomEditors(editors);
            mappers.put(record.getPrefix(), fieldSetMapper);

            log.debug("Added " + record.getRecordType() 
                    + " layout with prefix='" + record.getPrefix()
                    + "', line length=" + record.getLineLength()
                    + " to reader");

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
