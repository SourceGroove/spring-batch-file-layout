package com.github.sourcegroove.batch.item.file.fixed;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.beans.PropertyEditor;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FixedWidthPropertyFormatter {
    protected static final Log log = LogFactory.getLog(FixedWidthPropertyFormatter.class);
    private static final String EMPTY_STRING = "";
    private List<String> names;
    private List<Format> formats;
    private Map<Class<?>, PropertyEditor> editors;

    public FixedWidthPropertyFormatter names(List<String> names) {
        this.names = names;
        return this;
    }

    public FixedWidthPropertyFormatter formats(List<Format> formats) {
        this.formats = formats;
        return this;
    }

    public FixedWidthPropertyFormatter editors(Map<Class<?>, PropertyEditor> editors) {
        this.editors = editors;
        return this;
    }

    public void afterPropertiesSet() {
        Assert.notNull(this.names, "The 'names' property must be set.");
        Assert.isTrue(CollectionUtils.isEmpty(this.formats) || this.formats.size() == this.names.size(), 
                "The 'formats' size must match 'names' size.");
    }

    public String formatForWrite(String propertyName, Object propertyValue) {
        if (StringUtils.isBlank(propertyName) || propertyValue == null) {
            return EMPTY_STRING;
        }

        String formattedValue = formatObject(getFormat(propertyName), propertyValue);
        if (formattedValue != null) {
            return formattedValue;
        }

        formattedValue = formatObject(getEditor(propertyValue.getClass()), propertyValue);
        if (formattedValue != null) {
            return formattedValue;
        }
        return propertyValue != null ? propertyValue.toString() : EMPTY_STRING;
    }
    
    public String formatForRead(String propertyName, Class propertyType, String stringValue){
        if(StringUtils.isBlank(stringValue)){
            return stringValue;
        }
        if (StringUtils.isBlank(propertyName) || propertyType == null) {
            throw new IllegalArgumentException("Property name and type are required");
        }
        
        Format format = getFormat(propertyName);
        
        //right now we are only reformatting date fields on read
        if(format == null || !format.isDateFormat()){
            return  stringValue;
        }

        DateTimeFormatter df = getDateFormatter(format);
        TemporalAccessor temporal = df.parse(stringValue);
        PropertyEditor editor = getEditor(propertyType);
        if(editor == null) {
            return df.format(temporal);
        }
        
        Object obj = stringValue;
        if(propertyType == Date.class){
            obj = Timestamp.valueOf(LocalDateTime.from(temporal));

        } else if (propertyType ==  LocalDateTime.class){
            obj = LocalDateTime.from(temporal);
            
        } else if (propertyType == LocalDate.class){
            obj = LocalDate.from(temporal);
        }

        editor.setValue(obj);
        return editor.getAsText();
    }

    
    private String formatObject(Format format, Object value) {
        if (format == null || !format.hasPattern()) {
            return null;

        } else if (format.isDateFormat() && value instanceof Date) {
            return new SimpleDateFormat(format.getPattern()).format(value);

        } else if (format.isDateFormat() && value instanceof TemporalAccessor) {
            return getDateFormatter(format).format((TemporalAccessor) value);
        
        } else if (format.isDateFormat() && value instanceof String) {
            DateTimeFormatter df = getDateFormatter(format);
            return getDateFormatter(format).format(df.parse((String)value));

        } else if (format == Format.DECIMAL) {
            return new DecimalFormat(format.getPattern()).format(value);

        } else {
            return null;
        }
    }
    private String formatObject(PropertyEditor editor, Object value) {
        if (editor == null) {
            return null;
        }
        editor.setValue(value);
        return editor.getAsText();
    }
    
    private DateTimeFormatter getDateFormatter(Format format) {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
                .appendPattern(format.getPattern())
                .optionalStart();
        if (format == Format.YYYY) {
            builder.appendPattern("MMdd");
        } else if (format == Format.YYYYMM || format == Format.MMYYYY) {
            builder.appendPattern("dd");
        }
        return builder
                .optionalEnd()
                .parseDefaulting(ChronoField.MONTH_OF_YEAR, 01)
                .parseDefaulting(ChronoField.DAY_OF_MONTH, 01)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .toFormatter().withResolverStyle(ResolverStyle.SMART);
    }

    private PropertyEditor getEditor(Class<?> clazz) {
        return editors != null ? editors.get(clazz) : null;
    }
    private Format getFormat(String propertyName) {
        if(this.formats == null){
            return null;
        }
        int index = this.names.indexOf(propertyName);
        Format format = this.formats.get(index);
        return format == null || !format.hasPattern() ? null : format;
    }

}
