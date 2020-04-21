package com.github.sourcegroove.batch.item.file.delimited;

import com.github.sourcegroove.batch.item.file.ColumnLayout;
import com.github.sourcegroove.batch.item.file.Layout;
import com.github.sourcegroove.batch.item.file.RecordLayout;
import com.github.sourcegroove.batch.item.file.RecordType;
import com.github.sourcegroove.batch.item.file.format.Format;
import com.github.sourcegroove.batch.item.file.format.FormatAwareFieldExtractor;
import com.github.sourcegroove.batch.item.file.format.FormatAwareFieldSetMapper;
import com.github.sourcegroove.batch.item.file.format.editor.EditorFactory;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DelimitedLayout implements Layout {
    private static final char DEFAULT_QUALIFIER = '"';
    private static final String DEFAULT_DELIMETER = ",";
    
    private int linesToSkip = 0;
    private char qualifier = DEFAULT_QUALIFIER;
    private String delimiter = DEFAULT_DELIMETER;
    private Class targetType;
    private List<String> names = new ArrayList<>();
    private List<Format> formats = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors;

    public DelimitedLayout(){
        this.qualifier = DEFAULT_QUALIFIER;
        this.delimiter = DEFAULT_DELIMETER;
        this.editors = EditorFactory.getDefaultEditors();
    }
    
    public DelimitedLayout dateFormat(String dateFormat) {
        this.editors = EditorFactory.getDefaultEditors(dateFormat);
        return this;
    }

    public DelimitedLayout linesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
        return this;
    }
    public DelimitedLayout qualifier(char qualifier) {
        this.qualifier = qualifier;
        return this;
    }
    public DelimitedLayout delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }
    public DelimitedLayout editor(Class clazz, PropertyEditor editor){
        this.editors.put(clazz, editor);
        return this;
    }
    public DelimitedLayout record(Class targetType) {
        if(this.targetType != null){
            throw new IllegalArgumentException("Record already defined");
        }
        this.targetType = targetType;
        return this;
    }

    public DelimitedLayout column(String name){
        return this.column(name, Format.STRING);
    }

    public DelimitedLayout column(String name, Format format){
        this.names.add(name);
        this.formats.add(format);
        return this;
    }

    public DelimitedLayout layout(){
        return this;
    }

    public DelimitedFileItemReader getItemReader() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames(getNames());
        tokenizer.setQuoteCharacter(this.qualifier);
        tokenizer.setDelimiter(this.delimiter);

        FormatAwareFieldSetMapper fieldSetMapper = new FormatAwareFieldSetMapper();
        fieldSetMapper.setTargetType(this.targetType);
        fieldSetMapper.setCustomEditors(this.editors);
        fieldSetMapper.setNames(this.names);
        fieldSetMapper.setFormats(this.formats);
        
        DefaultLineMapper lineMapper = new DefaultLineMapper();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        
        DelimitedFileItemReader reader = new DelimitedFileItemReader();
        reader.setLineMapper(lineMapper);
        reader.setLinesToSkip(this.linesToSkip);
        return reader;
    }

    public DelimitedFileItemWriter getItemWriter() {

        FormatAwareFieldExtractor extractor = new FormatAwareFieldExtractor<>();
        extractor.setNames(this.names);
        extractor.setFormats(this.formats);
        extractor.setCustomEditors(this.editors);

        DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator();
        lineAggregator.setFieldExtractor(extractor);
        lineAggregator.setDelimiter(this.delimiter);

        DelimitedFileItemWriter writer = new DelimitedFileItemWriter();
        writer.setLineAggregator(lineAggregator);
        return writer;
    }

    private String[] getNames(){
        return this.names.toArray(new String[this.names.size()]);
    }

    public List<RecordLayout> getRecords() {
        List<ColumnLayout> columns = new ArrayList<>();
        for(int i = 0; i < names.size(); i++){
            columns.add(new ColumnLayout().setName(names.get(i)).setFormat(formats.get(i)).setStart(i + 1));
        }
        List<RecordLayout> records = new ArrayList<>();
        records.add(new RecordLayout(){
            public String getType() {
                return RecordType.DETAIL.name();
            }
            public List<ColumnLayout> getColumns() {
                return columns;
            }
        });
        return records;
    }


  
    
}
