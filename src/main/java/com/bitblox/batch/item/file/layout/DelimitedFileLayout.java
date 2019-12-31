package com.bitblox.batch.item.file.layout;

import lombok.Data;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

@Data
public class DelimitedFileLayout implements FileLayout {

    private int linesToSkip = 0;
    private String delimeter = ",";
    private char qualifier = '"';
    
    private List<RecordLayout> recordLayouts = new ArrayList<>();
    private DelimitedRecordLayout currentRecordLayout;


    public DelimitedFileLayout delimeter(String delimeter){
        this.delimeter = delimeter;
        return this;
    }

    public DelimitedFileLayout qualifier(char qualifier){
        this.qualifier = qualifier;
        return this;
    }
    public DelimitedFileLayout linesToSkip(int linesToSkip){
        this.linesToSkip = linesToSkip;
        return this;
    }
    public DelimitedFileLayout record(Class targetType){
        this.currentRecordLayout = new DelimitedRecordLayout();
        this.currentRecordLayout.setTargetType(targetType);
        this.recordLayouts.add(this.currentRecordLayout);
        return this;
    }
    public DelimitedFileLayout prefix(String prefix){
        this.currentRecordLayout.setPrefix(prefix);
        return this;
    }
    public DelimitedFileLayout column(String name){
        this.currentRecordLayout.getFieldNames().add(name);
        return this;
    }
    public DelimitedFileLayout editor(Class<?> type, PropertyEditor editor){
        this.currentRecordLayout.getEditors().put(type, editor);
        return this;
    }

}
