package com.github.sourcegroove.batch.item.file.fixed;

import com.github.sourcegroove.batch.item.file.ColumnLayout;
import com.github.sourcegroove.batch.item.file.RecordLayout;
import com.github.sourcegroove.batch.item.file.RecordType;
import com.github.sourcegroove.batch.item.file.format.Format;
import com.github.sourcegroove.batch.item.file.format.PropertyFormatter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.transform.Range;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FixedWidthRecordLayout implements RecordLayout {


    private FixedWidthLayout fileLayout;
    private Class targetType;
    private String prefix = "*";
    private boolean strict;
    private RecordType recordType;
    private Map<Class<?>, PropertyEditor> readEditors = new HashMap<>();
    private Map<Class<?>, PropertyEditor> writeEditors = new HashMap<>();
    private FixedWidthLineFormatBuilder format;
    private List<String> columnNames = new ArrayList<>();
    private List<Range> columnRanges = new ArrayList<>();
    private List<Format> columnFormats = new ArrayList<>();

    public FixedWidthRecordLayout(Class targetType, FixedWidthLayout fileLayout) {
        this.recordType = RecordType.DETAIL;
        this.targetType = targetType;
        this.fileLayout = fileLayout;
        this.strict = true;
        this.format = new FixedWidthLineFormatBuilder();
    }

    public String getType() {
        return this.recordType != null ? this.recordType.name() : RecordType.DETAIL.name();
    }

    public List<ColumnLayout> getColumns() {
        List<ColumnLayout> columns = new ArrayList<>();
        for (int i = 0; i < this.columnNames.size(); i++) {
            Range r = this.columnRanges.get(i);
            columns.add(new ColumnLayout()
                    .setName(this.columnNames.get(i))
                    .setFormat(this.columnFormats.get(i))
                    .setStart(r.getMin())
                    .setEnd(r.getMax())
            );
        }
        return columns;
    }

    public RecordType getRecordType() {
        return this.recordType;
    }

    public Class getTargetType() {
        return this.targetType;
    }

    public boolean isStrict() {
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

    public Range[] getColumnRanges() {
        return this.columnRanges.toArray(new Range[this.columnRanges.size()]);
    }

    public int getLineLength() {
        return CollectionUtils.isEmpty(this.columnRanges) ? 0 :
                this.columnRanges.get(this.columnRanges.size() - 1).getMax();
    }

    public String[] getColumnNames() {
        return this.columnNames.toArray(new String[this.columnNames.size()]);
    }

    public Format[] getColumnFormats() {
        return this.columnFormats.toArray(new Format[columnFormats.size()]);
    }

    public String[] getMappableColumns() {
        List<String> mappable = this.columnNames.stream()
                .filter(c -> !StringUtils.equals(c, PropertyFormatter.NON_FIELD_PROPERTY))
                .collect(Collectors.toList());
        return mappable.toArray(new String[mappable.size()]);
    }

    public Format[] getMappableColumnFormats() {
        List<Format> formats = new ArrayList<>();
        for (int i = 0; i < this.columnNames.size(); i++) {
            if (!StringUtils.equals(this.columnNames.get(i), PropertyFormatter.NON_FIELD_PROPERTY)) {
                formats.add(this.columnFormats.get(i));
            }
        }
        return formats.toArray(new Format[formats.size()]);
    }

    //filler and constant columns (non-mappable)
    public FixedWidthRecordLayout column(int start, int end) {
        return column(PropertyFormatter.NON_FIELD_PROPERTY, new Range(start, end), Format.FILLER, null);
    }

    public FixedWidthRecordLayout column(int width, String value) {
        return column(PropertyFormatter.NON_FIELD_PROPERTY, getRange(width), Format.FILLER, value);
    }

    public FixedWidthRecordLayout column(int start, int end, String value) {
        Range range = new Range(start, end);
        return column(PropertyFormatter.NON_FIELD_PROPERTY, range, null, value);
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
        if (format == null) {
            format = value != null ? Format.STRING : Format.FILLER;
        }
        this.columnRanges.add(range);
        this.columnNames.add(name);
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

    private Range getRange(int width) {
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

        for (int i = 0; i < this.columnNames.size(); i++) {
            String name = this.columnNames.get(i);
            Range range = this.columnRanges.get(i);
            Format fmt = this.columnFormats.get(i);

            str.append("    .column(\"")
                    .append(name).append("\", ")
                    .append(range.getMin()).append(", ")
                    .append(range.getMax())
                    .append(", ").append("Format.").append(fmt)
                    .append(")\n");
        }
        return str.toString();
    }
    
}
