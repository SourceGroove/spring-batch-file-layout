package com.github.sourcegroove.batch.item.file.reader.excel.streaming;

import com.github.sourcegroove.batch.item.file.layout.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.layout.editor.LocalDateTimeEditor;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import com.github.sourcegroove.batch.item.file.reader.excel.ExcelRowMapper;
import com.github.sourcegroove.batch.item.file.reader.excel.ExcelRowTokenizer;
import com.github.sourcegroove.batch.item.file.reader.excel.simple.SimpleExceltemReader;
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

public class StreamingExcelItemReaderTest {

    private static final String SAMPLE_FILE = "sample-file.xlsx";

    @Test
    public void givenFileWhenReadFirstSheetThenRead() throws Exception {
        StreamingExcelItemReader reader = getReader(SAMPLE_FILE, null);
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
        //assertNull(reader.read());
    }

    private StreamingExcelItemReader getReader(String filename, Integer sheet){
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

        StreamingExcelItemReader reader = new StreamingExcelItemReader();
        reader.setLinesToSkip(1);
        reader.setSheetsToRead(sheets);
        reader.setRowMapper(rowMapper);
        reader.setResource(MockFactory.getResource(filename));

        return reader;
    }
}
