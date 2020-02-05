package com.github.sourcegroove.batch.item.file.editor;

import org.apache.poi.ss.formula.functions.Offset;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeEditor extends PropertyEditorSupport {
    private DateTimeFormatter formatter;

    public OffsetDateTimeEditor(){
        this.formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    }
    public OffsetDateTimeEditor(String pattern){
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public void setAsText(@Nullable String text) {
        if (!StringUtils.hasText(text)) {
            this.setValue((Object)null);
        } else {
            this.setValue(OffsetDateTime.parse(text, formatter));
        }
    }
    @Override
    public String getAsText() {
        OffsetDateTime value = (OffsetDateTime)this.getValue();
        return value != null ? formatter.format(value) : "";
    }
}
