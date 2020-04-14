package com.github.sourcegroove.batch.item.file.fixed;

import com.github.sourcegroove.batch.item.file.Layout;
import com.github.sourcegroove.batch.item.file.editor.*;
import com.github.sourcegroove.batch.item.file.fixed.reader.FixedWidthFileItemReader;
import com.github.sourcegroove.batch.item.file.fixed.reader.FixedWidthFileItemReaderFactory;
import com.github.sourcegroove.batch.item.file.fixed.writer.FixedWidthFileItemWriter;
import com.github.sourcegroove.batch.item.file.fixed.writer.FixedWidthFileItemWriterFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FixedWidthLayout implements Layout {
    protected final Log log = LogFactory.getLog(getClass());
    private int linesToSkip = 0;
    private boolean writeAsStrings = true;
    private Map<Class<?>, PropertyEditor> readEditors = new HashMap<>();
    private Map<Class<?>, PropertyEditor> writeEditors = new HashMap<>();
    private List<FixedWidthRecordLayout> records = new ArrayList<>();

    public FixedWidthLayout dateFormat(String dateFormat) {
        this.readEditors.putAll(EditorFactory.getDefaultEditors(dateFormat));
        this.writeEditors.putAll(EditorFactory.getDefaultEditors(dateFormat));
        return this;
    }

    public FixedWidthLayout linesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
        return this;
    }

    /*
     * Setting this to true will cause the FixedWidthFormatBuilder to create a
     * line format using all strings (no printf decimals - %2d, etc)
     * and the FixedWidthFileFieldExtract to extract values as Strings.
     *
     * This allows you to pass null objects to those formats that would otherwise cause
     * an exception - i.e. a null Double sent to %2f
     *
     * Basically - it's safer... and therefore is the default
     */
    public FixedWidthLayout writeAsStrings(boolean writeAsStrings) {
        this.writeAsStrings = writeAsStrings;
        return this;
    }

    public FixedWidthLayout editor(Class clazz, PropertyEditor editor) {
        this.readEditors.put(clazz, editor);
        this.writeEditors.put(clazz, editor);
        return this;
    }

    public FixedWidthLayout readEditor(Class clazz, PropertyEditor editor) {
        this.readEditors.put(clazz, editor);
        return this;
    }

    public FixedWidthLayout writeEditor(Class clazz, PropertyEditor editor) {
        this.writeEditors.put(clazz, editor);
        return this;
    }

    public FixedWidthRecordLayout footer(Class targetType) {
        return footer(targetType);
    }

    public FixedWidthRecordLayout footer(Class targetType, String prefix) {
        if (this.getFooterLayout() != null) {
            throw new IllegalArgumentException("Footer already defined");
        }
        return record(targetType, FixedWidthRecordLayout.RecordType.FOOTER, prefix);
    }

    public FixedWidthRecordLayout header(Class targetType) {
        return header(targetType, null);
    }

    public FixedWidthRecordLayout header(Class targetType, String prefix) {
        if (this.getHeaderLayout() != null) {
            throw new IllegalArgumentException("Header already defined");
        }
        return record(targetType, FixedWidthRecordLayout.RecordType.HEADER, prefix);
    }

    public FixedWidthRecordLayout record(Class targetType) {
        return record(targetType, FixedWidthRecordLayout.RecordType.DETAIL, null);
    }

    public FixedWidthRecordLayout record(Class targetType, String prefix) {
        return record(targetType, FixedWidthRecordLayout.RecordType.DETAIL, prefix);
    }

    public boolean isWriteAsStrings() {
        return this.writeAsStrings;
    }

    public int getLinesToSkip() {
        return this.linesToSkip;
    }

    public Map<Class<?>, PropertyEditor> getReadEditors() {
        return this.readEditors;
    }

    public Map<Class<?>, PropertyEditor> getWriteEditors() {
        return this.writeEditors;
    }

    public FixedWidthFileItemWriter getItemWriter() {
        return FixedWidthFileItemWriterFactory.getItemWriter(this);
    }

    public FixedWidthFileItemReader getItemReader() {
        return FixedWidthFileItemReaderFactory.getItemReader(this);
    }

    public List<FixedWidthRecordLayout> getRecordLayouts() {
        return this.records;
    }

    public List<FixedWidthRecordLayout> getDetailLayouts() {
        return this.records.stream()
                .filter(r -> r.getRecordType() == FixedWidthRecordLayout.RecordType.DETAIL)
                .collect(Collectors.toList());
    }

    public FixedWidthRecordLayout getHeaderLayout() {
        return this.records.stream()
                .filter(r -> r.getRecordType() == FixedWidthRecordLayout.RecordType.HEADER)
                .findFirst()
                .orElse(null);
    }

    public FixedWidthRecordLayout getFooterLayout() {
        return this.records.stream()
                .filter(r -> r.getRecordType() == FixedWidthRecordLayout.RecordType.FOOTER)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("FixedWidthLayout:\n")
                .append("new FixedWidthLayout()\n")
                .append("    .linesToSkip(").append(this.linesToSkip).append(")\n");
        this.records.forEach(r -> str.append(r.toString()));
        str.append(".layout();");

        return str.toString();
    }


    private FixedWidthRecordLayout record(Class targetType, FixedWidthRecordLayout.RecordType recordType, String prefix) {
        FixedWidthRecordLayout record = new FixedWidthRecordLayout(targetType, this);
        if (recordType != null) {
            record.recordType(recordType);
        }
        if (prefix != null) {
            record.prefix(prefix);
        }
        this.records.add(record);
        return this.records.get(this.records.size() - 1);
    }


}
