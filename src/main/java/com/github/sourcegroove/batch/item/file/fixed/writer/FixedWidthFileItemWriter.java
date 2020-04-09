package com.github.sourcegroove.batch.item.file.fixed.writer;

import com.github.sourcegroove.batch.item.file.LayoutItemWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.support.AbstractFileItemWriter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FixedWidthFileItemWriter<T> extends AbstractFileItemWriter<T> implements LayoutItemWriter<T> {
    protected static final Log log = LogFactory.getLog(FixedWidthFileItemWriter.class);
    private LineAggregator headerLineAggregator;
    private LineAggregator footerLineAggregator;
    private Map<Class, LineAggregator<T>> lineAggregators;

    public FixedWidthFileItemWriter() {
        super();
        this.setName(ClassUtils.getShortName(this.getClass()));
    }
    
    public LineAggregator getHeaderLineAggregator(){
        return this.headerLineAggregator;
    }
    public LineAggregator getFooterLineAggregator(){
        return this.footerLineAggregator;
    }
    public Map<Class, LineAggregator<T>> getLineAggregators(){
        return this.lineAggregators;
    }
    public LineAggregator<T> getLineAggregator(Class clazz){
        return this.lineAggregators.get(clazz);
    }
    
    public void setLineAggregator(Class clazz, LineAggregator lineAggregator) {
        if (this.lineAggregators == null) {
            this.lineAggregators = new HashMap<>();
        }
        this.lineAggregators.put(clazz, lineAggregator);
    }

    public void setLineAggregators(Map<Class, LineAggregator<T>> lineAggregators) {
        this.lineAggregators = lineAggregators;
    }

    public void setHeaderLineAggregator(LineAggregator lineAggregator) {
        this.headerLineAggregator = lineAggregator;
    }

    public void setFooterLineAggregator(LineAggregator lineAggregator) {
        this.footerLineAggregator = lineAggregator;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notEmpty(this.lineAggregators, "'lineAggregators' must not be empty");
    }

    @Override
    protected String doWrite(List<? extends T> items) {
        return items
                .stream()
                .map(i -> aggregate(i))
                .collect(Collectors.joining());
    }

    @Override
    public void enableHeaderCallback(Object object) {
        if (this.headerLineAggregator != null) {
            this.setHeaderCallback(new FlatFileHeaderCallback() {
                @Override
                public void writeHeader(Writer writer) throws IOException {
                    writer.write(headerLineAggregator.aggregate(object));
                }
            });
        }
    }

    @Override
    public void enableFooterCallback(Object object) {
        if (this.footerLineAggregator != null) {
            this.setFooterCallback(new FlatFileFooterCallback() {
                @Override
                public void writeFooter(Writer writer) throws IOException {
                    writer.write(footerLineAggregator.aggregate(object));
                }
            });
        }
    }

    private String aggregate(T item) {
        LineAggregator<T> aggregator = this.getLineAggregator(item.getClass());
        if (aggregator == null) {
            throw new IllegalArgumentException("Unsupported targetType '" + item.getClass() + "' - is it in the file layout?");
        }
        return aggregator.aggregate(item) + this.lineSeparator;
    }


}
