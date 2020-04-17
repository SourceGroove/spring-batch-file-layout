package com.github.sourcegroove.batch.item.file.format.editor;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateEditor extends PropertyEditorSupport {
    protected static final Log log = LogFactory.getLog(LocalDateEditor.class);

    private SimpleDateFormat formatter;

    public DateEditor() {
        this.formatter = new SimpleDateFormat("yyyyMMdd");
    }

    public DateEditor(String pattern) {
        this.formatter = new SimpleDateFormat(pattern);
    }

    @Override
    public void setAsText(@Nullable String text) {
        if (StringUtils.isBlank(text)) {
            this.setValue((Object) null);
        } else {
            try {
                this.setValue(formatter.parse(text));
            } catch (ParseException e) {
                throw new RuntimeException("Unable to parse date '" + text + "' with pattern '" + formatter.toPattern() + "'");
            }
        }
    }

    @Override
    public String getAsText() {
        Date value = (Date) this.getValue();
        return value != null ? formatter.format(value) : "";
    }
}
