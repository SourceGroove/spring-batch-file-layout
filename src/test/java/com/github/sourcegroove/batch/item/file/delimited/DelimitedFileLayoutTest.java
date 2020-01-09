package com.github.sourcegroove.batch.item.file.delimited;

import com.github.sourcegroove.batch.item.file.FileLayout;
import com.github.sourcegroove.batch.item.file.delimited.builder.DelimitedFileLayoutBuilder;
import com.github.sourcegroove.batch.item.file.editors.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;

import java.time.LocalDate;

public class DelimitedFileLayoutTest {
    private static final String SAMPLE_CSV = "sample-file.csv";
    private static final String SAMPLE_CSV_SPECIAL_CHAR = "sample-file-with-special-character.csv";

    @Test
    public void givenCsvFileWithSpecialCharacterWhenReadThenRead() throws Exception {
        FileLayout layout = new DelimitedFileLayoutBuilder()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                .column("recordType")
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .and().build();

        ResourceAwareItemReaderItemStream reader = layout.getItemReader();
        reader.setResource(MockFactory.getResource(SAMPLE_CSV_SPECIAL_CHAR));
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

    @Test
    public void givenCsvFileWhenReadThenRead() throws Exception {
        FileLayout layout = new DelimitedFileLayoutBuilder()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                .column("recordType")
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .and().build();

        ResourceAwareItemReaderItemStream reader = layout.getItemReader();
        reader.setResource(MockFactory.getResource(SAMPLE_CSV));
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }
}
