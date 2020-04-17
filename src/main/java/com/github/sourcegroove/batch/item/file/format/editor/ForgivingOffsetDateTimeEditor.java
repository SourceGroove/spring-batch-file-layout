package com.github.sourcegroove.batch.item.file.format.editor;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class ForgivingOffsetDateTimeEditor extends PropertyEditorSupport {
    private DateTimeFormatter formatter;

    public ForgivingOffsetDateTimeEditor(){
        this.formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    }
    public ForgivingOffsetDateTimeEditor(String pattern){
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public void setAsText(@Nullable String text) {
        if (!StringUtils.hasText(text)) {
            this.setValue((Object)null);
        } else if(text.contains("T") || text.contains("Z")){
            this.setValue(ZonedDateTime.parse(text, formatter));
        } else {
            LocalDate day = LocalDate.parse(text, formatter);
            OffsetDateTime zone = OffsetDateTime.of(day, LocalTime.MIN, OffsetDateTime.now().getOffset());
            this.setValue(zone);
        }
    }
    @Override
    public String getAsText() {
        OffsetDateTime value = (OffsetDateTime)this.getValue();
        return value != null ? formatter.format(value) : "";
    }
}
