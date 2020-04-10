package com.github.sourcegroove.batch.item.file.fixed;

import com.github.sourcegroove.batch.item.file.Layout;
import com.github.sourcegroove.batch.item.file.LayoutItemReader;
import com.github.sourcegroove.batch.item.file.LayoutItemWriter;
import com.github.sourcegroove.batch.item.file.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockRoleRecord;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class FixedWidthLayoutTest {
    private static final Log log = LogFactory.getLog(FixedWidthLayoutTest.class);
    private static final String SAMPLE_FIXED = "sample-file.txt";
    private static final String SAMPLE_FIXED_MULTIPLE_TYPES = "sample-file-record-types.txt";


    @Test
    public void givenLeadingFillerWhenCreateThenCorrectRanges() throws Exception {
        Layout layout = new FixedWidthLayout()
                .header(MockUserRecord.class, "HEAD")
                    .column(1, 20, "HEAD")
                    .column(21, 22, "X")
                .record(MockUserRecord.class)
                    .column(10, "0123456789")
                    .column("fieldName", 5)
                .footer(MockUserRecord.class, "FOOT")
                    .column(1, 20, "HEAD")
                    .column(21, 22, "X")
                .layout();
        log.info(layout);
    }
    @Test
    public void givenFixedLayoutWithMultipleRecordTypesWhenWriteAndReadAlotThenPerformant() throws Exception {
        Layout layout = new FixedWidthLayout()
                .record(MockUserRecord.class)
                .readEditor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .prefix("USER*")
                .column("recordType", 1, 4)
                .column("username", 5, 10)
                .column("firstName", 11, 20)
                .column("lastName", 31, 40)
                .column("dateOfBirth", 41, 48)
                .record(MockRoleRecord.class)
                .prefix("ROLE*")
                .column("recordType", 1, 4)
                .column("roleKey", 5, 8)
                .column("role", 9, 20)
                .layout();

        log.info(layout);

        List<Object> records = new ArrayList<>();
        records.addAll(MockFactory.getUsers(10000));
        records.addAll(MockFactory.getRoles(10000));

        Resource file = MockFactory.createResource("sample-file-output-load.txt");
        LayoutItemWriter writer = layout.getItemWriter();
        writer.setResource(file);
        writer.open(new ExecutionContext());

        StopWatch watch = new StopWatch();
        watch.start();
        writer.write(records);
        watch.stop();
        assertTrue("Time=" + watch.getTime(), watch.getTime() <= 500);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenFixedLayoutWhenWriteRecordTypesNotInLayoutThenError() throws Exception {
        Layout layout = new FixedWidthLayout()
                .record(MockUserRecord.class)
                .readEditor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .prefix("user*")
                .column("username", 1, 10)
                .column("firstName", 11, 20)
                .column("lastName", 31, 40)
                .column("dateOfBirth", 41, 48)
                .layout();

        log.info(layout);

        Resource file = MockFactory.createResource("sample-file-output-missing-record-type.txt");
        LayoutItemWriter writer = layout.getItemWriter();
        writer.setResource(file);
        writer.open(new ExecutionContext());
        writer.write(MockFactory.getRoles());
    }

    @Test
    public void givenFixedLayoutWithMultipleRecordTypesWhenWrittenThenAllTypesWritten() throws Exception {
        Layout layout = new FixedWidthLayout()
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .record(MockUserRecord.class)
                .prefix("USER*")
                .strict(false)
                .column("recordType", 1, 4)
                .column("username", 5, 10)
                .column("firstName", 11, 20)
                .column("lastName", 31, 40)
                .column(41, 50)
                .column("dateOfBirth", 51, 58)
                .record(MockRoleRecord.class)
                .prefix("ROLE*")
                .strict(false)
                .column("recordType", 1, 4)
                .column("roleKey", 5, 8)
                .column("role", 9, 20)
                .column(21, 30)
                .layout();

        log.info(layout);

        List<Object> records = new ArrayList<>();
        records.addAll(MockFactory.getUsers());
        records.addAll(MockFactory.getRoles());

        Resource file = MockFactory.createResource("sample-file-output-multiple-record-types.txt");
        LayoutItemWriter writer = layout.getItemWriter();
        writer.setResource(file);
        writer.open(new ExecutionContext());
        writer.write(records);

        //read it back in and validate....
        ResourceAwareItemReaderItemStream reader = layout.getItemReader();
        reader.setResource(file);
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
        MockFactory.assertSystemAdminRole((MockRoleRecord) reader.read());
        MockFactory.assertUserRole((MockRoleRecord) reader.read());
    }

    @Test
    public void givenFixedLayoutAndRecordsWithFillerWhenWriteThenWrittenWithFiller() throws Exception {
        Layout layout = new FixedWidthLayout()
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .record(MockUserRecord.class)
                .prefix("USER*")
                .column("recordType", 1, 4)
                .column("username", 5, 10)
                .column("firstName", 11, 20)
                .column("lastName", 31, 40)
                .column("dateOfBirth", 41, 48)
                .layout();

        log.info(layout);

        Resource file = MockFactory.createResource("sample-file-output-no-filler.txt");
        LayoutItemWriter<MockUserRecord> writer = layout.getItemWriter();
        writer.setResource(file);
        writer.open(new ExecutionContext());
        writer.write(MockFactory.getUsers());

        //read it back in and validate....
        ResourceAwareItemReaderItemStream reader = layout.getItemReader();
        reader.setResource(file);
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

    @Test
    public void givenFixedLayoutAndRecordsWithNoFillerWhenWriteThenWriten() throws Exception {
        Layout layout = new FixedWidthLayout()
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .record(MockUserRecord.class)
                .prefix("USER*")
                .column("recordType", 1, 4)
                .column("username", 5, 10)
                .column("firstName", 11, 20)
                .column("lastName", 21, 30)
                .column("dateOfBirth", 31, 38)
                .layout();

        log.info(layout);

        Resource file = MockFactory.createResource("sample-file-output-no-filler.txt");
        LayoutItemWriter<MockUserRecord> writer = layout.getItemWriter();
        writer.setResource(file);
        writer.open(new ExecutionContext());
        writer.write(MockFactory.getUsers());

        //read it back in and validate....
        ResourceAwareItemReaderItemStream reader = layout.getItemReader();
        reader.setResource(file);
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

    @Test
    public void givenFixedFileWithMultipleRecordTypesWhenReadThenRead() throws Exception {
        Layout layout = new FixedWidthLayout()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                .strict(false)
                .readEditor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .prefix("USER*")
                .column("recordType", 1, 4)
                .column("username", 5, 10)
                .column("firstName", 11, 20)
                .column("lastName", 21, 30)
                .column(31, 35, "XX")
                .column(36, 40)
                .column("dateOfBirth", 41, 48)
                .record(MockRoleRecord.class)
                .strict(false)
                .prefix("ROLE*")
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

    @Test
    public void givenFixedFileWhenReadThenRead() throws Exception {
        Layout layout = new FixedWidthLayout()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                .readEditor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .column("recordType", 1, 4)
                .column("username", 5, 10)
                .column("firstName", 11, 20)
                .column("lastName", 21, 30)
                .column("dateOfBirth", 31, 38)
                .layout();

        log.info(layout);

        LayoutItemReader reader = layout.getItemReader();
        reader.setResource(MockFactory.getResource(SAMPLE_FIXED));
        reader.open(new ExecutionContext());
        log.info(reader.getClass());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

}
