package com.github.sourcegroove.batch.item.file;

import com.github.sourcegroove.batch.item.file.editors.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.model.FileLayout;
import com.github.sourcegroove.batch.item.file.model.FixedWidthFileLayout;
import lombok.extern.java.Log;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@Log
public class FileLayoutItemWriterTest {
    private static final String EXPORT_DIR = "./target/test-classes/files/";

    @Test
    public void givenFixedLayoutWithMultipleRecordTypesWhenWriteAndReadAlotThenPerformant() throws Exception {
        FileLayout layout = new FixedWidthFileLayout()
                .record(MockUserRecord.class)
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
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
                .column("role", 9, 20);

        Resource file = new FileSystemResource(EXPORT_DIR + "sample-file-output-load.txt");

        List<Object> records = new ArrayList<>();
        records.addAll(MockFactory.getUsers(10000));
        records.addAll(MockFactory.getRoles(10000));

        FileLayoutItemWriter writer = new FileLayoutItemWriter();
        writer.setFileLayout(layout);
        writer.setResource(file);
        writer.open(new ExecutionContext());

        StopWatch watch = new StopWatch();
        watch.start();
        writer.write(records);
        watch.stop();
        assertTrue("Time=" + watch.getTime(), watch.getTime() <= 400);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenFixedLayoutWhenWriteRecordTypesNotInLayoutThenError() throws Exception {
        FileLayout layout = new FixedWidthFileLayout()
                .record(MockUserRecord.class)
                    .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                    .prefix("user*")
                    .column("username", 1, 10)
                    .column("firstName", 11, 20)
                    .column("lastName", 31, 40)
                    .column("dateOfBirth", 41, 48);

        Resource file = new FileSystemResource(EXPORT_DIR + "sample-file-output-missing-record-type.txt");

        FileLayoutItemWriter writer = new FileLayoutItemWriter();
        writer.setFileLayout(layout);
        writer.setResource(file);
        writer.open(new ExecutionContext());
        writer.write(MockFactory.getRoles());
    }

    @Test
    public void givenFixedLayoutWithMultipleRecordTypesWhenWrittenThenAllTypesWritten() throws Exception {
        FileLayout layout = new FixedWidthFileLayout()
                .record(MockUserRecord.class)
                    .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
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
                    .column("role", 9, 20);

        Resource file = new FileSystemResource(EXPORT_DIR + "sample-file-output-multiple-record-types.txt");

        List<Object> records = new ArrayList<>();
        records.addAll(MockFactory.getUsers());
        records.addAll(MockFactory.getRoles());

        FileLayoutItemWriter writer = new FileLayoutItemWriter();
        writer.setFileLayout(layout);
        writer.setResource(file);
        writer.open(new ExecutionContext());
        writer.write(records);

        //read it back in and validate....
        FileLayoutItemReader reader = new FileLayoutItemReader();
        reader.setFileLayout(layout);
        reader.setResource(file);
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
        MockFactory.assertSystemAdminRole((MockRoleRecord) reader.read());
        MockFactory.assertUserRole((MockRoleRecord) reader.read());
    }

    @Test
    public void givenFixedLayoutAndRecordsWithFillerWhenWriteThenWrittenWithFiller() throws Exception {
        FileLayout layout = new FixedWidthFileLayout()
                .record(MockUserRecord.class)
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .prefix("USER*")
                .column("recordType", 1, 4)
                .column("username", 5, 10)
                .column("firstName", 11, 20)
                .column("lastName", 31, 40)
                .column("dateOfBirth", 41, 48);

        Resource file = new FileSystemResource(EXPORT_DIR + "sample-file-output-filler.txt");

        FileLayoutItemWriter<MockUserRecord> writer = new FileLayoutItemWriter<>();
        writer.setFileLayout(layout);
        writer.setResource(file);
        writer.open(new ExecutionContext());
        writer.write(MockFactory.getUsers());

        //read it back in and validate....
        FileLayoutItemReader reader = new FileLayoutItemReader();
        reader.setFileLayout(layout);
        reader.setResource(file);
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

    @Test
    public void givenFixedLayoutAndRecordsWithNoFillerWhenWriteThenWriten() throws Exception {
        FileLayout layout = new FixedWidthFileLayout()
                .record(MockUserRecord.class)
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .prefix("USER*")
                .column("recordType", 1, 4)
                .column("username", 5, 10)
                .column("firstName", 11, 20)
                .column("lastName", 21, 30)
                .column("dateOfBirth", 31, 38);

        Resource file = new FileSystemResource(EXPORT_DIR + "sample-file-output-no-filler.txt");
        FileLayoutItemWriter<MockUserRecord> writer = new FileLayoutItemWriter<>();
        writer.setFileLayout(layout);
        writer.setResource(file);
        writer.open(new ExecutionContext());
        writer.write(MockFactory.getUsers());

        //read it back in and validate....
        FileLayoutItemReader reader = new FileLayoutItemReader();
        reader.setFileLayout(layout);
        reader.setResource(file);
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

}
