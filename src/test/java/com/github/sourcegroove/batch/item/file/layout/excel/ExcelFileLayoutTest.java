package com.github.sourcegroove.batch.item.file.layout.excel;

import com.github.sourcegroove.batch.item.file.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;

import java.time.LocalDate;

import static org.junit.Assert.assertNull;

public class ExcelFileLayoutTest {

    private static final String SAMPLE_FILE = "sample-file.xlsx";

    @Test
    public void givenFileWhenReadFirstSheetThenRead() throws Exception {
        FileLayout layout = new ExcelFileLayout()
                .sheetIndex(0)
                .linesToSkip(1)
                    .sheet(MockUserRecord.class)
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
        assertNull(reader.read());
    }

    @Test
    public void givenFileWhenReadMiddleSheetOnlyThenRead() throws Exception {
        FileLayout layout = new ExcelFileLayout()
                .linesToSkip(1)
                .sheet(MockUserRecord.class)
                .sheetIndex(1)
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
        assertNull(reader.read());
    }

    @Test
    public void givenFileWhenReadAllSheetsThenRead() throws Exception {
        FileLayout layout = new ExcelFileLayout()
                .linesToSkip(1)
                .sheet(MockUserRecord.class)
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
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }
}
