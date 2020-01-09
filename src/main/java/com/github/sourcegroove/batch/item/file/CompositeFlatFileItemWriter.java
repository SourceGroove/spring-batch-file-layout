package com.github.sourcegroove.batch.item.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.support.AbstractFileItemWriter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompositeFlatFileItemWriter<T> extends AbstractFileItemWriter<T> {
    protected static final Log log = LogFactory.getLog(CompositeFlatFileItemWriter.class);
    private Map<Class, LineAggregator> lineAggregators;

    public CompositeFlatFileItemWriter() {
        this.setExecutionContextName(ClassUtils.getShortName(CompositeFlatFileItemWriter.class));
    }

    public void setLineAggregators(Map<Class, LineAggregator> lineAggregators){
        this.lineAggregators = lineAggregators;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(this.lineAggregators, "'lineAggregators' must not be empty");
    }
    @Override
    protected String doWrite(List<? extends T> items) {
        return items
                .stream()
                .map(i -> aggregate(i))
                .collect(Collectors.joining());
    }

    private String aggregate(T item){
       LineAggregator aggregator = this.lineAggregators.get(item.getClass());
       if(aggregator == null){
           throw new IllegalArgumentException("Unsupported targetType '" + item.getClass() + "' - is it in the file layout?");
       }
       return aggregator.aggregate(item) + this.lineSeparator;
    }



}
