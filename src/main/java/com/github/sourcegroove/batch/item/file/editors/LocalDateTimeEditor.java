package com.github.sourcegroove.batch.item.file.editors;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeEditor extends PropertyEditorSupport {

    private static final String DEFAULT_FORMAT = "yyyyMMdd HH:mm:ss";
    private DateTimeFormatter formatter;

    public LocalDateTimeEditor(){
        this.formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    }
    public LocalDateTimeEditor(String pattern){
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }


    @Override
    public void setAsText(@Nullable String text) {
        if (!StringUtils.hasText(text)) {
            this.setValue((Object)null);
        } else {
            this.setValue(LocalDateTime.parse(text, formatter));
        }
    }
    @Override
    public String getAsText() {
        LocalDateTime value = (LocalDateTime)this.getValue();
        return value != null ? formatter.format(value) : "";
    }
}
