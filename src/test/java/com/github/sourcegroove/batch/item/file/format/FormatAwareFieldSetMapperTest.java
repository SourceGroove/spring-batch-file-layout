package com.github.sourcegroove.batch.item.file.format;

import com.github.sourcegroove.batch.item.file.format.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class FormatAwareFieldSetMapperTest {

    @Test
    public void givenDateFormatWhenMapThenDate() throws Exception {
        String[] names = new String[]{"username", "dateOfBirth"};
        Format[] formats = new Format[]{Format.STRING, Format.YYYYMMDD};
        String[] values = new String[]{"USER",  "19780930"};
        
        Map<Class<?>, PropertyEditor> editors = new HashMap<>();
        editors.put(LocalDate.class, new LocalDateEditor(Format.YYYYMMDD.getPattern()));
        
        FormatAwareFieldSetMapper<MockUserRecord> mapper = new FormatAwareFieldSetMapper();
        mapper.setTargetType(MockUserRecord.class);
        mapper.setCustomEditors(editors);
        mapper.setNames(names);
        mapper.setFormats(formats);
        mapper.afterPropertiesSet();
        
        FieldSet fieldSet = new DefaultFieldSet(values, names);
        MockUserRecord record = mapper.mapFieldSet(fieldSet);
        Assert.assertNotNull(record);
        Assert.assertEquals("USER", record.getUsername());
        Assert.assertEquals(LocalDate.of(1978,9,30), record.getDateOfBirth());
        
    }
}
