package com.github.sourcegroove.batch.item.file.writer;

import com.github.sourcegroove.batch.item.file.decorator.PropertyEditorFieldExtractorDecorator;
import com.github.sourcegroove.batch.item.file.layout.fixed.StringFormatBuilder;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockRoleRecord;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import com.github.sourcegroove.batch.item.file.writer.CompositeFlatFileItemWriter;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.batch.item.file.transform.LineAggregator;

import java.util.*;

import static org.junit.Assert.assertTrue;

public class CompositeFlatFileItemWriterTest {

    @Test
    public void givenLineAggregatorWriteAndReadAlotThenPerformant() throws Exception {
        String format = new StringFormatBuilder().append(1, 10).toString();

        Map<Class, LineAggregator> lineAggregators = new HashMap<>();
        lineAggregators.put(MockUserRecord.class, getLineAggregator(Arrays.asList("firstName"), format));
        lineAggregators.put(MockRoleRecord.class, getLineAggregator(Arrays.asList("role"), format));

        CompositeFlatFileItemWriter writer = new CompositeFlatFileItemWriter();
        writer.setLineAggregators(lineAggregators);
        writer.setResource(MockFactory.createResource("composite-flat-file-item-writer-output.txt"));
        writer.afterPropertiesSet();

        List<Object> records = new ArrayList<>();
        records.addAll(MockFactory.getUsers(10000));
        records.addAll(MockFactory.getRoles(10000));

        StopWatch watch = new StopWatch();
        watch.start();
        writer.open(new ExecutionContext());
        writer.write(records);
        writer.close();
        watch.stop();
        assertTrue("Time=" + watch.getTime(), watch.getTime() <= 500);
    }


    private static LineAggregator getLineAggregator(List<String> fieldNames, String format){
        BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
        extractor.setNames(fieldNames.toArray(new String[fieldNames.size()]));
        PropertyEditorFieldExtractorDecorator fieldExtractor = new PropertyEditorFieldExtractorDecorator();
        fieldExtractor.setFieldExtractor(extractor);
        FormatterLineAggregator aggregator = new FormatterLineAggregator();
        aggregator.setFieldExtractor(fieldExtractor);
        aggregator.setFormat(format);
        return aggregator;
    }

}
