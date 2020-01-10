package com.github.sourcegroove.batch.item.file.layout.excel;

import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;

import java.beans.PropertyEditor;
import java.util.*;

public class ExcelFileLayout implements FileLayout {
    protected static final Log log = LogFactory.getLog(ExcelFileLayout.class);


    private Class targetType;
    private List<String> columns = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();
    private Set<Integer> sheetsToRead;
    private int linesToSkip = 0;

    public ExcelFileLayout sheet(Class targetType) {
        if(this.targetType != null){
            throw new IllegalArgumentException("Record already defined");
        }
        this.targetType = targetType;
        return this;
    }
    public ExcelFileLayout sheetIndex(int sheetIndex){
        if(this.sheetsToRead == null){
            this.sheetsToRead = new HashSet<>();
        }
        this.sheetsToRead.add(sheetIndex);
        return this;
    }
    public ExcelFileLayout linesToSkip(int linesToSkip){
        this.linesToSkip = linesToSkip;
        return this;
    }
    public ExcelFileLayout column(String column){
        this.columns.add(column);
        return this;
    }
    public ExcelFileLayout editor(Class clazz, PropertyEditor editor){
        this.editors.put(clazz, editor);
        return this;
    }
    public ExcelFileLayout layout(){
        return this;
    }

    @Override
    public <T> ResourceAwareItemWriterItemStream<T> getItemWriter() {
        return null;
    }

    public ExcelItemReader getItemReader(){

        ExcelRowTokenizer tokenizer = new ExcelRowTokenizer();
        tokenizer.setNames(getColumns());

        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(this.targetType);
        fieldSetMapper.setCustomEditors(this.editors);

        ExcelRowMapper rowMapper = new ExcelRowMapper();
        rowMapper.setFieldSetMapper(fieldSetMapper);
        rowMapper.setRowTokenizer(tokenizer);

        ExcelItemReader itemReader = new ExcelItemReader();
        itemReader.setLinesToSkip(this.linesToSkip);
        itemReader.setSheetsToRead(this.sheetsToRead);
        itemReader.setRowMapper(rowMapper);

        return itemReader;
    }

    private String[] getColumns(){
        return this.columns.toArray(new String[this.columns.size()]);
    }

}
