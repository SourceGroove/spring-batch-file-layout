package com.bitblox.batch.item.file.layout;


import java.beans.PropertyEditor;
import java.util.Map;

public interface RecordLayout {
    Class getTargetType();
    String[] getFieldNameArray();
    Map<Class<?>, PropertyEditor> getEditors();
    String getPrefix();
}
