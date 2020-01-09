package com.github.sourcegroove.batch.item.file.delimited.builder;

import com.github.sourcegroove.batch.item.file.CompositeFlatFileItemWriter;
import com.github.sourcegroove.batch.item.file.FileLayoutFieldExtractor;
import com.github.sourcegroove.batch.item.file.delimited.DelimitedFileLayout;
import com.github.sourcegroove.batch.item.file.delimited.DelimitedRecordLayout;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.util.HashMap;
import java.util.Map;

public class DelimitedItemWriterBuilder {
    private DelimitedFileLayout layout;

    public DelimitedItemWriterBuilder setLayout(DelimitedFileLayout layout){
        this.layout = layout;
        return this;
    }

    public CompositeFlatFileItemWriter build() {
        Map<Class, LineAggregator> lineAggregators = new HashMap<>();
        for(DelimitedRecordLayout recordLayout : layout.getRecordLayouts()){
            BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
            extractor.setNames(recordLayout.getFieldNames().toArray(new String[recordLayout.getFieldNames().size()]));

            FileLayoutFieldExtractor fieldExtractor = new FileLayoutFieldExtractor();
            fieldExtractor.setFieldExtractor(extractor);
            fieldExtractor.setCustomEditors(recordLayout.getEditors());

            DelimitedLineAggregator aggregator = new DelimitedLineAggregator();
            aggregator.setFieldExtractor(extractor);
            aggregator.setDelimiter(layout.getDelimiter());

            lineAggregators.put(recordLayout.getTargetType(), aggregator);
        }

        CompositeFlatFileItemWriter writer = new CompositeFlatFileItemWriter();
        writer.setLineAggregators(lineAggregators);
        return writer;
    }
}
