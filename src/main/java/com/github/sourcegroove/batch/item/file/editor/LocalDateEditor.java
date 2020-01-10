package com.github.sourcegroove.batch.item.file.editor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateEditor extends PropertyEditorSupport {
    protected static final Log log = LogFactory.getLog(LocalDateEditor.class);

    private static final String DEFAULT_FORMAT = "yyyyMMdd";
    private DateTimeFormatter formatter;
    private boolean acceptUnixTimestamps = true;

    public LocalDateEditor(){
         this.formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    }
    public LocalDateEditor(String pattern){
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }
    public void setAcceptUnixTimestamps(boolean acceptUnixTimestamps){
        this.acceptUnixTimestamps = acceptUnixTimestamps;
    }

    @Override
    public void setAsText(@Nullable String text) {
        if (StringUtils.isBlank(text)) {
            this.setValue((Object) null);
        } else {
            this.setValue(getDate(text));
        }
    }

    @Override
    public String getAsText() {
        LocalDate value = (LocalDate) this.getValue();
        return value != null ? formatter.format(value) : "";
    }

    private LocalDate getDate(String text) {
        if (this.acceptUnixTimestamps
                && StringUtils.isNumeric(text)
                && StringUtils.length(text) == 12) {
            return new java.sql.Date(Long.valueOf(text)).toLocalDate();
        } else {
            return LocalDate.parse(text, formatter);
        }
    }
}
