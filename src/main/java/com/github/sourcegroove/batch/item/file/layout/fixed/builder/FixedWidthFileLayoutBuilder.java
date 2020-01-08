package com.github.sourcegroove.batch.item.file.layout.fixed.builder;

import com.github.sourcegroove.batch.item.file.layout.fixed.FixedWidthFileLayout;
import com.github.sourcegroove.batch.item.file.layout.fixed.FixedWidthRecordLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FixedWidthFileLayoutBuilder {

    private FixedWidthFileLayout layout = new FixedWidthFileLayout();
    private List<FixedWidthRecordLayoutBuilder> recordBuilders = new ArrayList<>();

    public FixedWidthFileLayout build(){
        List<FixedWidthRecordLayout> recordLayouts = this.recordBuilders
                .stream()
                .map(b -> b.build())
                .collect(Collectors.toList());
        this.layout.setRecordLayouts(recordLayouts);
        return this.layout;
    }
    public FixedWidthFileLayoutBuilder linesToSkip(int linesToSkip){
        this.layout.setLinesToSkip(linesToSkip);
        return this;
    }
    public FixedWidthRecordLayoutBuilder record(Class targetType){
        FixedWidthRecordLayoutBuilder recordLayoutBuilder = new FixedWidthRecordLayoutBuilder(this);
        recordLayoutBuilder.targetType(targetType);
        this.recordBuilders.add(recordLayoutBuilder);
        return recordLayoutBuilder;
    }
}
