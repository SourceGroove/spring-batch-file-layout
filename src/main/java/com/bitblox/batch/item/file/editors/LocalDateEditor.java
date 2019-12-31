package com.bitblox.batch.item.file.editors;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LocalDateEditor extends PropertyEditorSupport {

    private static final String DEFAULT_FORMAT = "yyyyMMdd";
    private DateTimeFormatter formatter;

    public LocalDateEditor(){
         this.formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    }
    public LocalDateEditor(String pattern){
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }


    @Override
    public void setAsText(@Nullable String text) {
        if (!StringUtils.hasText(text)) {
            this.setValue((Object)null);
        } else {
            try {
                this.setValue(LocalDate.parse(text, formatter));
            } catch (Throwable t) {
                log.error("Unable to parse LocalDate from {} using {}", text, formatter.toFormat());
            }
        }
    }
    @Override
    public String getAsText() {
        LocalDate value = (LocalDate)this.getValue();
        return value != null ? formatter.format(value) : "";
    }
}
