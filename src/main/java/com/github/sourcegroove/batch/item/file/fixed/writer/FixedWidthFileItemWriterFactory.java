package com.github.sourcegroove.batch.item.file.fixed.writer;

import com.github.sourcegroove.batch.item.file.fixed.FixedWidthLayout;
import com.github.sourcegroove.batch.item.file.fixed.FixedWidthRecordLayout;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

public class FixedWidthFileItemWriterFactory {

    public static FixedWidthFileItemWriter getItemWriter(FixedWidthLayout file) {
        FixedWidthFileItemWriter writer = new FixedWidthFileItemWriter();
        for (FixedWidthRecordLayout record : file.getDetailLayouts()) {
            writer.setLineAggregator(record.getTargetType(), getLineAggregator(file, record));
        }
        if (file.getHeaderLayout() != null) {
            writer.setHeaderLineAggregator(getLineAggregator(file, file.getHeaderLayout()));
        }
        if (file.getFooterLayout() != null) {
            writer.setFooterLineAggregator(getLineAggregator(file, file.getFooterLayout()));
        }
        return writer;
    }

    private static LineAggregator getLineAggregator(FixedWidthLayout file, FixedWidthRecordLayout record) {
        Map<Class<?>, PropertyEditor> editors = new HashMap<>();
        editors.putAll(file.getWriteEditors());
        editors.putAll(record.getWriteEditors());

        FormatterLineAggregator aggregator = new FormatterLineAggregator();
        aggregator.setFormat(record.getFormat());
        if(file.isWriteAsStrings()) {
            FixedWidthBeanWrapperFieldExtractor fieldExtractor = new FixedWidthBeanWrapperFieldExtractor();
            fieldExtractor.setNames(record.getMappableColumns());
            fieldExtractor.setFormats(record.getMappableColumnFormats());
            fieldExtractor.setRanges(record.getMappableColumnRanges());
            fieldExtractor.setCustomEditors(editors);
            aggregator.setFieldExtractor(fieldExtractor);
        } else {
            BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
            extractor.setNames(record.getColumns());
            FixedWidthDelegatingFieldExtractor fieldExtractor = new FixedWidthDelegatingFieldExtractor();
            fieldExtractor.setFieldExtractor(extractor);
            fieldExtractor.setCustomEditors(record.getReadEditors());
            aggregator.setFieldExtractor(fieldExtractor);
        }
        return aggregator;
    }

}
