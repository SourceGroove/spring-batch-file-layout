package com.github.sourcegroove.batch.item.file;

import com.github.sourcegroove.batch.item.file.editors.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.model.DelimitedFileLayout;
import com.github.sourcegroove.batch.item.file.model.FixedWidthFileLayout;
import com.github.sourcegroove.batch.item.file.model.FileLayout;
import lombok.extern.java.Log;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDate;

@Log
public class FileLayoutItemReaderTest {

    private static final String SAMPLE_CSV = "sample-file.csv";
    private static final String SAMPLE_FIXED = "sample-file.txt";
    private static final String SAMPLE_FIXED_MULTIPLE_TYPES = "sample-file-record-types.txt";

    @Test
    public void givenFixedFileWithMultipleRecordTypesWhenReadThenRead() throws Exception {
        FileLayout layout = new FixedWidthFileLayout()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                    .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                    .prefix("USER*")
                    .column("recordType", 1, 4)
                    .column("username", 5, 10)
                    .column("firstName", 11, 20)
                    .column("lastName", 21, 30)
                    .column("dateOfBirth", 31, 38)
                .record(MockRoleRecord.class)
                    .prefix("ROLE*")
                    .column("recordType", 1, 4)
                    .column("roleKey", 5, 8)
                    .column("role", 9, 20);

        FileLayoutItemReader reader = new FileLayoutItemReader();
        reader.setFileLayout(layout);
        reader.setResource(MockFactory.getResource(SAMPLE_FIXED_MULTIPLE_TYPES));
        reader.open(new ExecutionContext());

        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
        MockFactory.assertSystemAdminRole((MockRoleRecord) reader.read());
        MockFactory.assertUserRole((MockRoleRecord) reader.read());
    }

    @Test
    public void givenFixedFileWhenReadThenRead() throws Exception {
        FileLayout layout = new FixedWidthFileLayout()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .column("recordType", 1, 4)
                .column("username", 5, 10)
                .column("firstName", 11, 20)
                .column("lastName", 21, 30)
                .column("dateOfBirth", 31, 38);

        FileLayoutItemReader reader = new FileLayoutItemReader();
        reader.setFileLayout(layout);
        reader.setResource(MockFactory.getResource(SAMPLE_FIXED));
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

    @Test
    public void givenCsvFileWhenReadThenRead() throws Exception {
        FileLayout layout = new DelimitedFileLayout()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                .column("recordType")
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"));

        FileLayoutItemReader reader = new FileLayoutItemReader();
        reader.setFileLayout(layout);
        reader.setResource(MockFactory.getResource(SAMPLE_CSV));
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

}
