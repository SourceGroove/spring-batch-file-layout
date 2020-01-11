package com.github.sourcegroove.batch.item.file.reader.excel.simple;

import com.github.sourcegroove.batch.item.file.layout.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.layout.editor.LocalDateTimeEditor;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import com.github.sourcegroove.batch.item.file.reader.excel.ExcelRowMapper;
import com.github.sourcegroove.batch.item.file.reader.excel.ExcelRowTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;

import java.beans.PropertyEditor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SimpleExcelItemReaderTest {
    protected final Log log = LogFactory.getLog(getClass());
    private static final String SAMPLE_FILE_5000 = "sample-file-5000-records.xlsx";
    private static final String SAMPLE_FILE_100000 = "sample-file-100000-records.xlsx";
    private static final String SAMPLE_FILE = "sample-file.xlsx";

    @Test
    public void givenFileWith100kRecordsWhenReadThenRead() throws Exception {
        SimpleExceltemReader<MockUserRecord> reader = getReader(SAMPLE_FILE_100000, null);
        reader.open(new ExecutionContext());
        for(int i = 0; i < 100000; i++) {
            assertNotNull(reader.read());
        }
    }

    @Test
    public void givenFileWith5KRecordsWhenReadThenRead() throws Exception {
        SimpleExceltemReader<MockUserRecord> reader = getReader(SAMPLE_FILE_5000, null);
        reader.open(new ExecutionContext());
        for(int i = 0; i < 5000; i++) {
            assertNotNull(reader.read());
        }
    }

    @Test
    public void givenFileWhenReadFirstSheetThenRead() throws Exception {

        SimpleExceltemReader reader = getReader(SAMPLE_FILE, 0);
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
        assertNull(reader.read());
    }


    private SimpleExceltemReader getReader(String filename, Integer sheet){
        Map<Class<?>, PropertyEditor> editors = new HashMap<>();
        editors.put(LocalDate.class, new LocalDateEditor());
        editors.put(LocalDateTime.class, new LocalDateTimeEditor());

        BeanWrapperFieldSetMapper fieldSetMapper = new BeanWrapperFieldSetMapper();
        fieldSetMapper.setTargetType(MockUserRecord.class);
        fieldSetMapper.setCustomEditors(editors);

        ExcelRowTokenizer tokenizer = new ExcelRowTokenizer();
        tokenizer.setNames(new String[]{"recordType", "username", "firstName","lastName","dateOfBirth"});
        ExcelRowMapper rowMapper = new ExcelRowMapper();
        rowMapper.setFieldSetMapper(fieldSetMapper);
        rowMapper.setRowTokenizer(tokenizer);

        Set<Integer> sheets = null;
        if(sheet != null){
            sheets = new HashSet<>();
            sheets.add(sheet);
        }

        SimpleExceltemReader reader = new SimpleExceltemReader();
        reader.setLinesToSkip(1);
        reader.setSheetsToRead(sheets);
        reader.setRowMapper(rowMapper);
        reader.setResource(MockFactory.getResource(filename));

        return reader;
    }
}
