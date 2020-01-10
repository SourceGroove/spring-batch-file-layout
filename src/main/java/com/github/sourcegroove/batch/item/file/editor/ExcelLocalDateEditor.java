package com.github.sourcegroove.batch.item.file.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExcelLocalDateEditor extends PropertyEditorSupport {
    protected static final Log log = LogFactory.getLog(ExcelLocalDateEditor.class);
    private static final String DEFAULT_FORMAT = "yyyyMMdd";
    private DateTimeFormatter formatter;
    public ExcelLocalDateEditor(){
        this.formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT);
    }
    public ExcelLocalDateEditor(String pattern){
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public void setAsText(@Nullable String text) {
        if (!StringUtils.hasText(text)) {
            this.setValue((Object)null);
        } else {
            Timestamp ts = new Timestamp(Long.parseLong(text));
            LocalDate ld = ts.toLocalDateTime().toLocalDate();
            String txt = ld.format(formatter);
            log.info("setAsText txt: " + txt);
            this.setValue(txt);
        }
    }
    @Override
    public String getAsText() {
        LocalDate value = (LocalDate)this.getValue();
        String text = value != null ? formatter.format(value) : "";
        log.info("getAsText: " + text);
        return text;
    }


}
