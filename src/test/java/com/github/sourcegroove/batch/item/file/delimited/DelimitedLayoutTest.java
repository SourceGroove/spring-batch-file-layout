package com.github.sourcegroove.batch.item.file.delimited;

import com.github.sourcegroove.batch.item.file.excel.ExcelLayout;
import com.github.sourcegroove.batch.item.file.excel.ExcelLayoutTest;
import com.github.sourcegroove.batch.item.file.format.editor.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.Layout;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import com.github.sourcegroove.batch.item.file.LayoutItemReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;

import java.time.LocalDate;

public class DelimitedLayoutTest {
    private static final String SAMPLE_CSV = "sample-file.csv";
    private static final String SAMPLE_CSV_SPECIAL_CHAR = "sample-file-with-special-character.csv";

    private static final Log log = LogFactory.getLog(DelimitedLayoutTest.class);

    @Test
    public void givenLayoutWhenRecordsThenRecords() throws Exception {
        Layout layout = new DelimitedLayout()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                .column("recordType")
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .layout();

        layout.getRecords().forEach(r -> {
            log.info("Record " + r.getType());
            r.getColumns().forEach(c -> {
                log.info("Column " + " "
                        + c.getName()
                        + " " + c.getFormat()
                        + " " + c.getStart()
                        + " " + c.getEnd());
            });
        });
    }


    @Test
    public void givenCsvFileWithSpecialCharacterWhenReadThenRead() throws Exception {
        Layout layout = new DelimitedLayout()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                .column("recordType")
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .layout();

        LayoutItemReader reader = layout.getItemReader();
        reader.setResource(MockFactory.getResource(SAMPLE_CSV_SPECIAL_CHAR));
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

    @Test
    public void givenCsvFileWhenReadThenRead() throws Exception {
        Layout layout = new DelimitedLayout()
                .linesToSkip(1)
                .record(MockUserRecord.class)
                .column("recordType")
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .layout();

        LayoutItemReader reader = layout.getItemReader();
        reader.setResource(MockFactory.getResource(SAMPLE_CSV));
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

}
