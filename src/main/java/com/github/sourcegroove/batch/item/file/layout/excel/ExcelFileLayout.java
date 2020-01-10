package com.github.sourcegroove.batch.item.file.layout.excel;

import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;

import java.util.ArrayList;
import java.util.List;

public class ExcelFileLayout implements FileLayout {
    protected static final Log log = LogFactory.getLog(ExcelFileLayout.class);

    private List<ExcelSheetLayout> sheets = new ArrayList<>();
    private ExcelSheetLayout getCurrent(){
        return this.sheets.get(this.sheets.size() - 1);
    }

    public ExcelSheetLayout sheet() {
        this.sheets.add(new ExcelSheetLayout(this));
        return getCurrent();
    }
    public ExcelSheetLayout sheet(String name) {
        this.sheets.add(new ExcelSheetLayout(this).name(name));
        return getCurrent();
    }
    public ExcelSheetLayout sheet(int position) {
        this.sheets.add(new ExcelSheetLayout(this).position(position));
        return getCurrent();
    }

    @Override
    public <T> ResourceAwareItemWriterItemStream<T> getItemWriter() {
        return null;
    }

    @Override
    public ExcelItemReader getItemReader() {
        ExcelSheetLayout sheetLayout = getCurrent();
        ExcelRecordLayout recordLayout = sheetLayout.getRecords().get(0);

        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(recordLayout.getTargetType());
        fieldSetMapper.setCustomEditors(recordLayout.getEditors());

        ExcelSheetTokenizer tokenizer = new ExcelSheetTokenizer();
        tokenizer.setLinesToSkip(sheetLayout.getLinesToSkip());
        tokenizer.setNames(recordLayout.getColumns());

        ExcelItemReader itemReader = new ExcelItemReader();
        itemReader.setSheetTokenizer(tokenizer);
        itemReader.setFieldSetMapper(fieldSetMapper);

        return itemReader;
    }

}
