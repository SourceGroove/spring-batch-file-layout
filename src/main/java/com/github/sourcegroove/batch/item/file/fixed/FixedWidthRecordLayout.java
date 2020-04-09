package com.github.sourcegroove.batch.item.file.fixed;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.transform.Range;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FixedWidthRecordLayout {
    public enum RecordType {
        RECORD,
        HEADER,
        FOOTER
    }
    private static final String NON_FIELD_COLUMN = "__FILLER__";
    private final Log log = LogFactory.getLog(getClass());
    private FixedWidthLayout fileLayout;
    private Class targetType;
    private String prefix = "*";
    private boolean strict;
    private RecordType recordType;
    private Map<Class<?>, PropertyEditor> readEditors = new HashMap<>();
    private Map<Class<?>, PropertyEditor> writeEditors = new HashMap<>();
    private FixedWidthFormatBuilder format = new FixedWidthFormatBuilder();
    private List<String> columns = new ArrayList<>();
    private List<Range> columnRanges = new ArrayList<>();
    private List<Format> columnFormats = new ArrayList<>();

    public FixedWidthRecordLayout(Class targetType, FixedWidthLayout fileLayout) {
        this.recordType = RecordType.RECORD;
        this.targetType = targetType;
        this.fileLayout = fileLayout;
        this.strict = true;
    }

    public RecordType getRecordType(){
        return this.recordType;
    }
    public Class getTargetType() {
        return this.targetType;
    }
    public boolean isStrict(){
        return this.strict;
    }
    public String getPrefix() {
        return this.prefix;
    }
    public FixedWidthRecordLayout strict(boolean strict) {
        this.strict = strict;
        return this;
    }
    public FixedWidthRecordLayout recordType(RecordType recordType) {
        this.recordType = recordType;
        return this;
    }
    public FixedWidthRecordLayout prefix(String prefix) {
        if (StringUtils.isNotBlank(prefix) && !StringUtils.endsWith(prefix, "*")) {
            prefix += "*";
        }
        this.prefix = prefix;
        return this;
    }

    public Map<Class<?>, PropertyEditor> getReadEditors() {
        return this.readEditors;
    }

    public FixedWidthRecordLayout editor(Class clazz, PropertyEditor editor) {
        this.readEditors.put(clazz, editor);
        this.writeEditors.put(clazz, editor);
        return this;
    }
    public FixedWidthRecordLayout readEditor(Class clazz, PropertyEditor editor) {
        this.readEditors.put(clazz, editor);
        return this;
    }
    public Map<Class<?>, PropertyEditor> getWriteEditors() {
        return this.writeEditors;
    }

    public FixedWidthRecordLayout writeEditor(Class clazz, PropertyEditor editor) {
        this.writeEditors.put(clazz, editor);
        return this;
    }

    public String getFormat() {
        return this.format.toString();
    }

    public Range[] getMappableColumnRanges() {
        List<Range> ranges = new ArrayList<>();
        for (int i = 0; i < this.columns.size(); i++) {
            if(!StringUtils.equals(this.columns.get(i), NON_FIELD_COLUMN)){
                ranges.add(this.columnRanges.get(i));
            }
        }
        return ranges.toArray(new Range[ranges.size()]);
    }

    public String[] getMappableColumns() {
        List<String> mappable = this.columns.stream()
                .filter(c -> !StringUtils.equals(c, NON_FIELD_COLUMN))
                .collect(Collectors.toList());
        return mappable.toArray(new String[mappable.size()]);
    }

    //filler and constant columns (non-mappable)
    public FixedWidthRecordLayout column(int start, int end) {
        return column(NON_FIELD_COLUMN, new Range(start, end), Format.CONSTANT, null);
    }
    public FixedWidthRecordLayout column(int width, String value) {
        return column(NON_FIELD_COLUMN, getRange(width), Format.CONSTANT, value);
    }
    public FixedWidthRecordLayout column(int start, int end, String value) {
        Range range = new Range(start, end);
        return column(NON_FIELD_COLUMN, range, null, value);
    }

    // Field mappable columns
    public FixedWidthRecordLayout column(String name, int width) {
        return column(name, getRange(width), Format.STRING, null);
    }

    public FixedWidthRecordLayout column(String name, int width, Format format) {
        return column(name, getRange(width), format, null);
    }

    public FixedWidthRecordLayout column(String name, int start, int end) {
        return column(name, new Range(start, end), Format.STRING, null);
    }

    public FixedWidthRecordLayout column(String name, int start, int end, Format format) {
        return column(name, new Range(start, end), format, null);
    }

    public FixedWidthRecordLayout column(String name, Range range, Format format, String value) {
        if(format == null){
            format = value != null ? Format.STRING : Format.CONSTANT;
        }
        this.columnRanges.add(range);
        this.columns.add(name);
        this.columnFormats.add(format);
        this.format.append(range, format, value);
        return this;
    }

    public FixedWidthRecordLayout record(Class targetType) {
        return this.fileLayout.record(targetType);
    }
    public FixedWidthRecordLayout record(Class targetType, String prefix) {
        return this.fileLayout.record(targetType, prefix);
    }
    public FixedWidthRecordLayout header(Class targetType) {
        return this.fileLayout.header(targetType);
    }
    public FixedWidthRecordLayout header(Class targetType, String prefix) {
        return this.fileLayout.header(targetType, prefix);
    }
    public FixedWidthRecordLayout footer(Class targetType) {
        return this.fileLayout.footer(targetType);
    }
    public FixedWidthRecordLayout footer(Class targetType, String prefix) {
        return this.fileLayout.footer(targetType, prefix);
    }

    public FixedWidthLayout layout() {
        return this.fileLayout;
    }

    private Range getRange(int width){
        int start = CollectionUtils.isNotEmpty(this.columnRanges) ? this.columnRanges.get(this.columnRanges.size() - 1).getMax() + 1 : 1;
        int end = start + width - 1;
        return new Range(start, end);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder()
                .append(".").append(recordType.name().toLowerCase())
                .append("(").append(this.getTargetType().getSimpleName()).append(".class, \"").append(this.getPrefix()).append("\")\n")
                .append("    //.format(\"").append(this.getFormat()).append("\")\n");
        for (int i = 0; i < this.columns.size(); i++) {
            Format fmt = this.columnFormats.get(i);
            String name = this.columns.get(i);
            Range range = this.columnRanges.get(i);
    
            if(name == NON_FIELD_COLUMN &&  fmt == Format.CONSTANT){
               //str.append("    .column(").append(range.getMin()).append(", ").append(range.getMax()).append(")\n");
            } else if(fmt == Format.STRING) {
                str.append("    .column(\"").append(name).append("\", ").append(range.getMin()).append(", ").append(range.getMax()).append(")\n");
            } else {
                str.append("    .column(\"")
                        .append(name).append("\", ")
                        .append(range.getMin()).append(", ")
                        .append(range.getMax())
                        .append(", ").append("Format.").append(fmt)
                        .append(")\n");
            }
            
        }
        return str.toString();
    }

}
