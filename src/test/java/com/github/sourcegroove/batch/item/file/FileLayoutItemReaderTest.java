package com.github.sourcegroove.batch.item.file;

import com.github.sourcegroove.batch.item.file.editors.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockRoleRecord;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import com.github.sourcegroove.batch.item.file.model.delimited.DelimitedFileLayout;
import com.github.sourcegroove.batch.item.file.model.FileLayout;
import com.github.sourcegroove.batch.item.file.model.fixed.FixedWidthFileLayout;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDate;

public class FileLayoutItemReaderTest {

    private static final String SAMPLE_CSV = "sample-file.csv";
    private static final String SAMPLE_CSV_SPECIAL_CHAR = "sample-file-with-special-character.csv";
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
                .and()
                .record(MockRoleRecord.class)
                    .prefix("ROLE*")
                    .column("recordType", 1, 4)
                    .column("roleKey", 5, 8)
                    .column("role", 9, 20)
                .build();

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
                .column("dateOfBirth", 31, 38)
                .build();

        FileLayoutItemReader reader = new FileLayoutItemReader();
        reader.setFileLayout(layout);
        reader.setResource(MockFactory.getResource(SAMPLE_FIXED));
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

    @Test
    public void givenCsvFileWithSpecialCharacterWhenReadThenRead() throws Exception {
        FileLayout layout = new DelimitedFileLayout()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                .column("recordType")
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .and();

        FileLayoutItemReader reader = new FileLayoutItemReader();
        reader.setFileLayout(layout);
        reader.setResource(MockFactory.getResource(SAMPLE_CSV_SPECIAL_CHAR));
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
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .build();

        FileLayoutItemReader reader = new FileLayoutItemReader();
        reader.setFileLayout(layout);
        reader.setResource(MockFactory.getResource(SAMPLE_CSV));
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

}
