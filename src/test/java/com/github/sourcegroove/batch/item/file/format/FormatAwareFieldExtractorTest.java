package com.github.sourcegroove.batch.item.file.format;

import com.github.sourcegroove.batch.item.file.format.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import org.junit.Test;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FormatAwareFieldExtractorTest {

    @Test
    public void givenFieldLevelNumberFormatAndNullCorrespondingValueWhenAggregateThenLine()  {
        FormatAwareFieldExtractor extractor = new FormatAwareFieldExtractor();
        extractor.setNames(new String[]{"username", "firstName", "lastName", "dateOfBirth", "age"});
        extractor.setFormats(new Format[]{Format.STRING, Format.STRING, Format.STRING, Format.YYYYMMDD, Format.INTEGER});
        extractor.afterPropertiesSet();
        
        MockUserRecord user = MockFactory.getNeo();
        user.setAge(null);
        
        Object[] fields = extractor.extract(user);
        assertEquals(0, fields[4]);
    }
    
    @Test
    public void givenFieldLevelDateFormatAndNullCorrespondingValueWhenAggregateThenLine()  {
        FormatAwareFieldExtractor extractor = new FormatAwareFieldExtractor();
        extractor.setNames(new String[]{"username", "firstName", "lastName", "dateOfBirth"});
        extractor.setFormats(new Format[]{Format.STRING, Format.STRING, Format.STRING, Format.YYYYMMDD});
        extractor.afterPropertiesSet();
        MockUserRecord user = MockFactory.getNeo();
        user.setDateOfBirth(null);
        Object[] fields = extractor.extract(user);
        assertEquals("", fields[3]);
    }
    @Test
    public void givenFieldLevelDateFormatAndCorrespondingValueWhenAggregateThenLine() {
        FormatAwareFieldExtractor extractor = new FormatAwareFieldExtractor();
        extractor.setNames(new String[]{"username", "firstName", "lastName", "dateOfBirth"});
        extractor.setFormats(new Format[]{Format.STRING, Format.STRING, Format.STRING, Format.YYYYMMDD});
        extractor.afterPropertiesSet();
        MockUserRecord user = MockFactory.getNeo();
        Object[] fields = extractor.extract(user);
        assertEquals(user.getUsername(), fields[0]);
        assertEquals(user.getFirstName(), fields[1]);
        assertEquals(user.getLastName(), fields[2]);
        assertEquals(DateTimeFormatter.ofPattern(Format.YYYYMMDD.getPattern()).format(user.getDateOfBirth()), fields[3]);
    }

    
    @Test
    public void givenFieldSetWithCustomEditorsWhenAggregateThenLine() {
        String dateFormat = "yyyyMMdd";

        Map<Class<?>, PropertyEditor> customEditors = new HashMap<>();
        customEditors.put(LocalDate.class, new LocalDateEditor(dateFormat));

        FormatAwareFieldExtractor extractor = new FormatAwareFieldExtractor();
        extractor.setNames(new String[]{"username", "firstName", "lastName", "dateOfBirth"});
        extractor.setCustomEditors(customEditors);
        extractor.afterPropertiesSet();

        MockUserRecord user = MockFactory.getNeo();
        Object[] fields = extractor.extract(user);
        assertEquals(user.getUsername(), fields[0]);
        assertEquals(user.getFirstName(), fields[1]);
        assertEquals(user.getLastName(), fields[2]);
        assertEquals(DateTimeFormatter.ofPattern(dateFormat).format(user.getDateOfBirth()), fields[3]);
    }


}
