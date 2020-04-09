package com.github.sourcegroove.batch.item.file.fixed.writer;

import com.github.sourcegroove.batch.item.file.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.fixed.Format;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import org.junit.Test;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FixedWidthDelegatingFieldExtractorTest {


    @Test
    public void givenFieldSetWithCustomEditorsWhenAggregateThenLine() throws Exception {
        String dateFormat = "yyyyMMdd";

        BeanWrapperFieldExtractor extractor = new BeanWrapperFieldExtractor();
        extractor.setNames(new String[]{"username", "firstName", "lastName", "dateOfBirth"});

        Map<Class<?>, PropertyEditor> customEditors = new HashMap<>();
        customEditors.put(LocalDate.class, new LocalDateEditor(dateFormat));

        FixedWidthDelegatingFieldExtractor decorator = new FixedWidthDelegatingFieldExtractor();
        decorator.setFieldExtractor(extractor);
        decorator.setCustomEditors(customEditors);
        decorator.afterPropertiesSet();

        MockUserRecord user = MockFactory.getNeo();
        Object[] fields = decorator.extract(user);
        assertEquals(user.getUsername(), fields[0]);
        assertEquals(user.getFirstName(), fields[1]);
        assertEquals(user.getLastName(), fields[2]);
        assertEquals(DateTimeFormatter.ofPattern(dateFormat).format(user.getDateOfBirth()), fields[3]);
    }


}
