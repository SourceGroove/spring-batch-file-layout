package com.github.sourcegroove.batch.item.file.editor;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;

public class LocalDateTimeEditor extends PropertyEditorSupport {
    private DateTimeFormatter formatter;

    public LocalDateTimeEditor() {
        this.formatter = getFormatter("yyyy-MM-dd");
    }

    public LocalDateTimeEditor(String pattern) {
        this.formatter = getFormatter(pattern);

    }

    @Override
    public void setAsText(@Nullable String text) {
        if (!StringUtils.hasText(text)) {
            this.setValue((Object) null);
        } else {
            this.setValue(LocalDateTime.parse(text, formatter));
        }
    }

    @Override
    public String getAsText() {
        LocalDateTime value = (LocalDateTime) this.getValue();
        return value != null ? formatter.format(value) : "";
    }

    private DateTimeFormatter getFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .appendPattern(pattern)
                .optionalStart()
                .appendPattern(" HH:mm:ss")
                .optionalEnd()
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter()
                .withResolverStyle(ResolverStyle.SMART);
    }
}
