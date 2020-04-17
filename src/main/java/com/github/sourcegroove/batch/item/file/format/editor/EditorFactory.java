package com.github.sourcegroove.batch.item.file.format.editor;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditorFactory {

    public static Map<Class<?>, PropertyEditor> getDefaultEditors() {
        Map<Class<?>, PropertyEditor> map = new HashMap<>();
        map.put(LocalDate.class, new LocalDateEditor());
        map.put(LocalDateTime.class, new LocalDateTimeEditor());
        map.put(OffsetDateTime.class, new OffsetDateTimeEditor());
        map.put(Date.class, new DateEditor());
        return map;
    }
    public static Map<Class<?>, PropertyEditor> getDefaultEditors(String dateFormat) {
        Map<Class<?>, PropertyEditor> map = new HashMap<>();
        map.put(LocalDate.class, new LocalDateEditor(dateFormat));
        map.put(LocalDateTime.class, new LocalDateTimeEditor(dateFormat));
        map.put(OffsetDateTime.class, new OffsetDateTimeEditor(dateFormat));
        map.put(Date.class, new DateEditor(dateFormat));
        return map;
    }
}
