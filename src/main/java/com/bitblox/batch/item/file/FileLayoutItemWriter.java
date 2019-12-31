package com.bitblox.batch.item.file;

import com.bitblox.batch.item.file.layout.FixedWidthRecordLayout;
import com.bitblox.batch.item.file.layout.FileLayout;
import com.bitblox.batch.item.file.layout.RecordLayout;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FormatterLineAggregator;
import org.springframework.util.Assert;

public class FileLayoutItemWriter<T> extends FlatFileItemWriter<T> {
    
    private FileLayout fileLayout;

    public void setFileLayout(FileLayout fileLayout){
        this.fileLayout = fileLayout;
        this.configure();
    }

    @Override
    public void afterPropertiesSet() throws Exception{
        Assert.notNull(this.fileLayout, "The 'fileLayout' property must be set.");
        super.afterPropertiesSet();
    }
    
    private void configure(){
        RecordLayout recordLayout = fileLayout.getRecordLayouts().get(0);
        BeanWrapperFieldExtractor<T> extractor = new BeanWrapperFieldExtractor();
        extractor.setNames(recordLayout.getFieldNameArray());
        FileLayoutFieldExtractor fieldExtractor = new FileLayoutFieldExtractor();
        fieldExtractor.setFieldExtractor(extractor);
        fieldExtractor.setCustomEditors(recordLayout.getEditors());
        
        if(recordLayout instanceof FixedWidthRecordLayout){
            FormatterLineAggregator aggregator = new FormatterLineAggregator();
            aggregator.setFieldExtractor(fieldExtractor);
            aggregator.setFormat(((FixedWidthRecordLayout)recordLayout).getFormat());
            setLineAggregator(aggregator);
        } else {
            DelimitedLineAggregator aggregator = new DelimitedLineAggregator();
            aggregator.setFieldExtractor(fieldExtractor);
            setLineAggregator(aggregator);
        }
    }
    
    
}
