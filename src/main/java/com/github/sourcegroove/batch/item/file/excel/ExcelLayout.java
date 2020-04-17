package com.github.sourcegroove.batch.item.file.excel;

import com.github.sourcegroove.batch.item.file.format.editor.DateEditor;
import com.github.sourcegroove.batch.item.file.format.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.format.editor.LocalDateTimeEditor;
import com.github.sourcegroove.batch.item.file.format.editor.OffsetDateTimeEditor;
import com.github.sourcegroove.batch.item.file.Layout;
import com.github.sourcegroove.batch.item.file.LayoutItemWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

public class ExcelLayout implements Layout {
    protected static final Log log = LogFactory.getLog(ExcelLayout.class);

    private Class targetType;
    private List<String> columns = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();
    private Set<Integer> sheetsToRead;
    private boolean streamFile = true;
    private int linesToSkip = 0;

    public ExcelLayout(){
        this.editor(LocalDate.class, new LocalDateEditor());
        this.editor(LocalDateTime.class, new LocalDateTimeEditor());
        this.editor(OffsetDateTime.class, new OffsetDateTimeEditor());
        this.editor(Date.class, new DateEditor());
    }
    public ExcelLayout sheet(Class targetType) {
        if(this.targetType != null){
            throw new IllegalArgumentException("Record already defined");
        }
        this.targetType = targetType;
        return this;
    }
    public ExcelLayout sheetIndex(int sheetIndex){
        if(this.sheetsToRead == null){
            this.sheetsToRead = new HashSet<>();
        }
        this.sheetsToRead.add(sheetIndex);
        return this;
    }
    public ExcelLayout linesToSkip(int linesToSkip){
        this.linesToSkip = linesToSkip;
        return this;
    }
    public ExcelLayout column(String column){
        this.columns.add(column);
        return this;
    }
    public ExcelLayout editor(Class clazz, PropertyEditor editor){
        this.editors.put(clazz, editor);
        return this;
    }
    public ExcelLayout streamFile(boolean streamFile){
        this.streamFile = streamFile;
        return this;
    }
    public ExcelLayout layout(){
        return this;
    }

    @Override
    public LayoutItemWriter getItemWriter() {
        return null;
    }

    @Override
    public ExcelItemReader getItemReader(){

        ExcelRowTokenizer tokenizer = new ExcelRowTokenizer();
        tokenizer.setNames(getColumns());

        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(this.targetType);
        fieldSetMapper.setCustomEditors(this.editors);

        ExcelRowMapper rowMapper = new ExcelRowMapper();
        rowMapper.setFieldSetMapper(fieldSetMapper);
        rowMapper.setRowTokenizer(tokenizer);

        ExcelItemReader itemReader = this.streamFile ? new StreamingExcelItemReader() : new SimpleExcelItemReader();
        itemReader.setLinesToSkip(this.linesToSkip);
        itemReader.setSheetsToRead(this.sheetsToRead);
        itemReader.setRowMapper(rowMapper);

        return itemReader;
    }

    private String[] getColumns(){
        return this.columns.toArray(new String[this.columns.size()]);
    }

}
