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
import com.github.sourcegroove.batch.item.file.mock.MockRoleRecord;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static junit.framework.TestCase.assertEquals;

public class FixedWidthFileItemReaderTest {
    protected static final Log log = LogFactory.getLog(FixedWidthFileItemReaderTest.class);
    private static final String SAMPLE_REFORMAT_FILE = "date-reformat-file.txt";
    private static final String SAMPLE_FIXED_MULTIPLE_TYPES = "sample-file-record-types.txt";


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

    @Test
    public void givenFixedFileWithMultipleRecordTypesWhenReadThenRead() throws Exception {
        Layout layout = new FixedWidthLayout()
                .linesToSkip(1)
                .record(MockUserRecord.class, "USER")
                .readEditor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .column("recordType", 1, 4)
                .column("username", 5, 10)
                .column("firstName", 11, 20)
                .column("lastName", 21, 30)
                .column(31, 35, "XX")
                .column(36, 40)
                .column("dateOfBirth", 41, 48)
                .footer(MockRoleRecord.class, "ROLE")
                .strict(false)
                .column("recordType", 1, 4)
                .column("roleKey", 5, 8)
                .column("role", 9, 20)
                .layout();

        log.info(layout);

        LayoutItemReader reader = layout.getItemReader();
        reader.setResource(MockFactory.getResource(SAMPLE_FIXED_MULTIPLE_TYPES));
        reader.open(new ExecutionContext());

        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
        MockFactory.assertSystemAdminRole((MockRoleRecord) reader.read());
        MockFactory.assertUserRole((MockRoleRecord) reader.read());
    }

}
