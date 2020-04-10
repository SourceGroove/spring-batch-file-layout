package com.github.sourcegroove.batch.item.file.fixed.reader;

import com.github.sourcegroove.batch.item.file.Layout;
import com.github.sourcegroove.batch.item.file.LayoutItemReader;
import com.github.sourcegroove.batch.item.file.editor.DateEditor;
import com.github.sourcegroove.batch.item.file.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.editor.LocalDateTimeEditor;
import com.github.sourcegroove.batch.item.file.fixed.FixedWidthLayout;
import com.github.sourcegroove.batch.item.file.fixed.Format;
import com.github.sourcegroove.batch.item.file.mock.MockDateRecord;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;

public class FixedWidthFileItemReaderTest {
    protected static final Log log = LogFactory.getLog(FixedWidthFileItemReaderTest.class);
    private static final String SAMPLE_REFORMAT_FILE = "date-reformat-file.txt";
    
   
    @Test
    public void givenLayoutWithDifferentFormatsWhenReadThanRead() throws Exception {
        Layout layout = new FixedWidthLayout()
                .record(MockDateRecord.class)
                    .readEditor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                    .column("type", 1, 3)
                    .column("year", 4, 7, Format.YYYY)
                    .column("month", 8, 13, Format.YYYYMM)
                    .column("day", 14, 21, Format.YYYYMMDD)
                .layout();

        log.info(layout);

        LayoutItemReader<MockDateRecord> reader = layout.getItemReader();
        reader.setResource(MockFactory.getResource(SAMPLE_REFORMAT_FILE));
        reader.open(new ExecutionContext());
        MockDateRecord record = reader.read();
        assertEquals("DTL", record.getType());
        assertEquals(LocalDate.of(1999, 1, 1), record.getYear());
        assertEquals(LocalDate.of(2020, 2, 1), record.getMonth());
        assertEquals(LocalDate.of(1978, 9, 30), record.getDay());
    }

    @Test
    public void givenLayoutAndEditorWithDifferentFormatsWhenReadThanRead() throws Exception {
        Layout layout = new FixedWidthLayout()
                .record(MockDateRecord.class)
                .readEditor(LocalDate.class, new LocalDateEditor("MM/dd/yyyy"))
                .column("type", 1, 3)
                .column("year", 4, 7, Format.YYYY)
                .column("month", 8, 13, Format.YYYYMM)
                .column("day", 14, 21, Format.YYYYMMDD)
                .layout();

        log.info(layout);

        LayoutItemReader<MockDateRecord> reader = layout.getItemReader();
        reader.setResource(MockFactory.getResource(SAMPLE_REFORMAT_FILE));
        reader.open(new ExecutionContext());
        MockDateRecord record = reader.read();
        assertEquals("DTL", record.getType());
        assertEquals(LocalDate.of(1999, 1, 1), record.getYear());
        assertEquals(LocalDate.of(2020, 2, 1), record.getMonth());
        assertEquals(LocalDate.of(1978, 9, 30), record.getDay());
    }

    @Test
    public void givenLayoutAndEditorWithDifferentFormatsAndDateTypesWhenReadThanRead() throws Exception {
        Layout layout = new FixedWidthLayout()
                .record(MockDateRecord.class)
                .readEditor(Date.class, new DateEditor("yyyy-MM-dd"))
                .readEditor(LocalDate.class, new LocalDateEditor("MM/dd/yyyy"))
                .readEditor(LocalDateTime.class, new LocalDateTimeEditor("yyyy/MM/dd"))
                .column("type", 1, 3)
                .column("dateField", 4, 7, Format.YYYY)
                .column("localDateField", 8, 13, Format.YYYYMM)
                .column("localDateTimeField", 14, 21, Format.YYYYMMDD)
                .layout();

        log.info(layout);

        LayoutItemReader<MockDateRecord> reader = layout.getItemReader();
        reader.setResource(MockFactory.getResource(SAMPLE_REFORMAT_FILE));
        reader.open(new ExecutionContext());
        MockDateRecord record = reader.read();
        assertEquals("DTL", record.getType());
        
        Date date = Date.from(LocalDate.of(1999, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        assertEquals(date, record.getDateField());
        assertEquals(LocalDate.of(2020, 2, 1), record.getLocalDateField());
        assertEquals(LocalDateTime.of(1978, 9, 30, 0, 0), record.getLocalDateTimeField());
    }
    
}
