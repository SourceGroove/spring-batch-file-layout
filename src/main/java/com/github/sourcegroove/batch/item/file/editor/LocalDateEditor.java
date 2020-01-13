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

    private DateTimeFormatter formatter;

    public LocalDateEditor(){
         this.formatter = DateTimeFormatter.BASIC_ISO_DATE;
    }
    public LocalDateEditor(String pattern){
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }
    @Override
    public void setAsText(@Nullable String text) {
        if (StringUtils.isBlank(text)) {
            this.setValue((Object) null);
        } else {
            this.setValue(LocalDate.parse(text, formatter));
        }
    }

    @Override
    public String getAsText() {
        LocalDate value = (LocalDate) this.getValue();
        return value != null ? formatter.format(value) : "";
    }


}
