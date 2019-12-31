package com.bitblox.batch.item.file.layout;

import lombok.Data;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DelimitedRecordLayout implements RecordLayout {

    private Class targetType;
    private String prefix = "*";
    private List<String> fieldNames = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();
    
    public String[] getFieldNameArray(){
        return getFieldNames().toArray(new String[getFieldNames().size()]);
    }

}
