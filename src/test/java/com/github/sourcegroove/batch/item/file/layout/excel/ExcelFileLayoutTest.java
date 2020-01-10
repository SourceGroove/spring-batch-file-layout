package com.github.sourcegroove.batch.item.file.layout.excel;

import com.github.sourcegroove.batch.item.file.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;

import java.time.LocalDate;

public class ExcelFileLayoutTest {

    private static final String SAMPLE_FILE = "sample-file.xlsx";
    private static final String SAMPLE_FILE_MULTIPLE_SHEETS_SAME_LAYOUT = "sample-file-multiple-sheets-same-layout.xlsx";
    private static final String SAMPLE_FILE_MULTIPLE_SHEETS_AND_LAYOUTS = "sample-file-multiple-sheets.xlsx";

    @Test
    public void givenFileWhenReadThenRead() throws Exception {
        FileLayout layout = new ExcelFileLayout()
                .sheet()
                    .linesToSkip(1)
                    .record(MockUserRecord.class)
                    .column("recordType")
                    .column("username")
                    .column("firstName")
                    .column("lastName")
                    .column("dateOfBirth")
                    .editor(LocalDate.class, new LocalDateEditor())
                .layout();

        ResourceAwareItemReaderItemStream<MockUserRecord> reader = layout.getItemReader();
        reader.setResource(MockFactory.getResource(SAMPLE_FILE));
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

    @Test
    public void givenFileWithMultipleSheetsOfTheSameLayoutWhenReadThenRead() throws Exception {
        FileLayout layout = new ExcelFileLayout()
                .sheet()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                .column("recordType")
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .editor(LocalDate.class, new LocalDateEditor())
                .layout();

        ResourceAwareItemReaderItemStream<MockUserRecord> reader = layout.getItemReader();
        reader.setResource(MockFactory.getResource(SAMPLE_FILE_MULTIPLE_SHEETS_SAME_LAYOUT));
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }
}
