package com.github.sourcegroove.batch.item.file.format;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.beans.PropertyEditor;
import java.sql.Timestamp;
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

public class PropertyFormatter {
    public static final String NON_FIELD_PROPERTY = "__FILLER__";

    protected static final Log log = LogFactory.getLog(PropertyFormatter.class);
    private static final String EMPTY_STRING = "";
    private List<String> names;
    private List<Format> formats;
    private Map<Class<?>, PropertyEditor> editors;

    public PropertyFormatter names(List<String> names) {
        this.names = names;
        return this;
    }

    public PropertyFormatter formats(List<Format> formats) {
        this.formats = formats;
        return this;
    }

    public PropertyFormatter editors(Map<Class<?>, PropertyEditor> editors) {
        this.editors = editors;
        return this;
    }

    public void afterPropertiesSet() {
        Assert.notNull(this.names, "The 'names' property must be set.");
        Assert.isTrue(CollectionUtils.isEmpty(this.formats) || this.formats.size() == this.names.size(),
                "The 'formats' size must match 'names' size.");
    }

    public Object formatForWrite(String propertyName, Object propertyValue) {
        if (StringUtils.isBlank(propertyName)) {
            return EMPTY_STRING;
        } else if (propertyValue == null) {
            return getNullValue(propertyName);
        }

        Object formattedValue = formatWithFormatter(propertyName, propertyValue);
        if (formattedValue != null) {
            log.trace("Reformatted property " + propertyName + " of type " + propertyValue.getClass() + " and value " + propertyValue 
                + " to " + formattedValue + " using Format");
            return formattedValue;
        }

        formattedValue = formatWithEditor(propertyValue);
        if (formattedValue != null) {
            log.trace("Reformatted property " + propertyName + " of type " + propertyValue.getClass() + " and value " + propertyValue
                    + " to " + formattedValue + " using Editor");
            return formattedValue;
        }
        
        return propertyValue != null ? propertyValue : getNullValue(propertyName);
    }

    public String formatForRead(String propertyName, Class propertyType, String stringValue) {
        log.trace("Formatting property " + propertyName + " of type " + propertyType + " and value " + stringValue);
        if (StringUtils.isBlank(stringValue)) {
            return stringValue;
        }
        if (StringUtils.isBlank(propertyName)) {
            throw new IllegalArgumentException("Property name required");
        }
        if (propertyType == null) {
            throw new IllegalArgumentException("Property  type required for " + propertyName);
        }

        Format format = getFormat(propertyName);
        //right now we are only reformatting date fields on read
        if (format == null || !format.isDateFormat()) {
            return stringValue;
        }

        DateTimeFormatter df = getDateFormatter(format);
        TemporalAccessor temporal = df.parse(stringValue);
        PropertyEditor editor = getEditor(propertyType);
        if (editor == null) {
            return df.format(temporal);
        }

        Object obj = stringValue;
        if (propertyType == Date.class) {
            obj = Timestamp.valueOf(LocalDateTime.from(temporal));

        } else if (propertyType == LocalDateTime.class) {
            obj = LocalDateTime.from(temporal);

        } else if (propertyType == LocalDate.class) {
            obj = LocalDate.from(temporal);
        }

        editor.setValue(obj);
        return editor.getAsText();
    }


    private Object formatWithFormatter(String propertyName, Object value) {
        int index = this.names.indexOf(propertyName);
        Format format = CollectionUtils.isNotEmpty(this.formats) ? this.formats.get(index) : null;
        
        if (format == null || !format.isDateFormat()) {
            return null;

        } else if (format.isDateFormat() && value instanceof Date) {
            return new SimpleDateFormat(format.getPattern()).format(value);

        } else if (format.isDateFormat() && value instanceof TemporalAccessor) {
            return getDateFormatter(format).format((TemporalAccessor) value);

        } else if (format.isDateFormat() && value instanceof String) {
            DateTimeFormatter df = getDateFormatter(format);
            return getDateFormatter(format).format(df.parse((String) value));

        } else {
            return null;
        }
    }

    private Object formatWithEditor(Object value) {
        if (value == null) {
            return null;
        }
        PropertyEditor editor = getEditor(value.getClass());
        if (editor == null) {
            return null;
        }
        editor.setValue(value);
        return editor.getAsText();
    }

    private Object getNullValue(String propertyName) {
        Format format = getFormat(propertyName);
        if(format == null){
            return EMPTY_STRING;
        } if(format == Format.INTEGER) {
            return 0;
        } else if (format == Format.DECIMAL){
            return 0.0;
        } else {
            return EMPTY_STRING;
        }
    }

    private PropertyEditor getEditor(Class propertyType) {
        return editors != null ? editors.get(propertyType) : null;
    }

    private Format getFormat(String propertyName) {
        return CollectionUtils.isNotEmpty(this.formats) ? this.formats.get(this.names.indexOf(propertyName)) : null;
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

}
