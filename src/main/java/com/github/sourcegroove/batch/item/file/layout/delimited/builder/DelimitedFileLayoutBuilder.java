package com.github.sourcegroove.batch.item.file.layout.delimited.builder;

import com.github.sourcegroove.batch.item.file.layout.delimited.DelimitedFileLayout;
import com.github.sourcegroove.batch.item.file.layout.delimited.DelimitedRecordLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DelimitedFileLayoutBuilder {

    private DelimitedFileLayout layout = new DelimitedFileLayout();
    private List<DelimitedRecordLayoutBuilder> recordBuilders = new ArrayList<>();

    public DelimitedFileLayout build(){
        List<DelimitedRecordLayout> recordLayouts = this.recordBuilders
                .stream()
                .map(b -> b.build())
                .collect(Collectors.toList());
        this.layout.setRecordLayouts(recordLayouts);
        return this.layout;
    }
    public DelimitedFileLayoutBuilder delimiter(String delimiter){
        this.layout.setDelimiter(delimiter);
        return this;
    }
    public DelimitedFileLayoutBuilder qualifier(char qualifier){
        this.layout.setQualifier(qualifier);
        return this;
    }
    public DelimitedFileLayoutBuilder linesToSkip(int linesToSkip){
        this.layout.setLinesToSkip(linesToSkip);
        return this;
    }
    public DelimitedRecordLayoutBuilder record(Class targetType){
        DelimitedRecordLayoutBuilder recordLayoutBuilder = new DelimitedRecordLayoutBuilder(this);
        recordLayoutBuilder.targetType(targetType);
        this.recordBuilders.add(recordLayoutBuilder);
        return recordLayoutBuilder;
    }
}
