package com.github.sourcegroove.batch.item.file.fixed;

import com.github.sourcegroove.batch.item.file.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.editor.LocalDateTimeEditor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.file.transform.Range;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedWidthRecordLayout  {
    private FixedWidthLayout fileLayout;
    private Class targetType;
    private String prefix = "*";
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();
    private FixedWidthFormatBuilder format = new FixedWidthFormatBuilder();
    private List<String> columns = new ArrayList<>();
    private List<Range> columnRanges = new ArrayList<>();

    public FixedWidthRecordLayout(Class targetType, FixedWidthLayout fileLayout){
        this.targetType = targetType;
        this.fileLayout = fileLayout;
        this.editor(LocalDate.class, new LocalDateEditor());
        this.editor(LocalDateTime.class, new LocalDateTimeEditor());
    }
    public Class getTargetType(){
        return this.targetType;
    }
    public String getPrefix(){
        return this.prefix;
    }
    public FixedWidthRecordLayout prefix(String prefix){
        this.prefix = prefix;
        return this;
    }
    public Map<Class<?>, PropertyEditor> getEditors(){
        return this.editors;
    }
    public FixedWidthRecordLayout editor(Class clazz, PropertyEditor editor){
        this.editors.put(clazz, editor);
        return this;
    }
    public String getFormat(){
        return this.format.toString();
    }
    public Range[] getColumnRanges() {
        return this.columnRanges.toArray(new Range[this.columnRanges.size()]);
    }
    public String[] getColumns(){
        return this.columns.toArray(new String[this.columns.size()]);
    }
    public FixedWidthRecordLayout column(String name, int width){
        return column(name, width, FixedWidthFormatBuilder.Format.STRING);
    }
    public FixedWidthRecordLayout column(String name, int width, FixedWidthFormatBuilder.Format format){
        int start = CollectionUtils.isNotEmpty(this.columnRanges) ? this.columnRanges.get(this.columnRanges.size() - 1).getMax() + 1 : 1;
        int end = start + width - 1;
        return column(name, start, end, format);
    }
    public FixedWidthRecordLayout column(String name, int start, int end){
        return column(name, start, end, FixedWidthFormatBuilder.Format.STRING);
    }
    public FixedWidthRecordLayout column(String name, int start, int end, FixedWidthFormatBuilder.Format format){
        Range range = new Range(start, end);
        this.columnRanges.add(range);
        this.columns.add(name);
        this.format.append(range, format);
        return this;
    }
    public FixedWidthRecordLayout record(Class targetType){
        return this.fileLayout.record(targetType);
    }
    public FixedWidthLayout layout(){
        return this.fileLayout;
    }
}
