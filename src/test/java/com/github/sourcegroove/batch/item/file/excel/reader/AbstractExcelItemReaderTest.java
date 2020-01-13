package com.github.sourcegroove.batch.item.file.excel.reader;

import com.github.sourcegroove.batch.item.file.excel.reader.ExcelItemReader;
import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import com.github.sourcegroove.batch.item.file.mock.MockUserRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public abstract class AbstractExcelItemReaderTest {

    protected final Log log = LogFactory.getLog(getClass());
    private static final String SAMPLE_FILE_5000 = "sample-file-5000-records.xlsx";
    private static final String SAMPLE_FILE_100000 = "sample-file-100000-records.xlsx";
    private static final String SAMPLE_FILE = "sample-file.xlsx";

    public abstract ExcelItemReader getReader(String filename, Integer sheet);

    @Test
    public void givenFileWith100kRecordsWhenReadThenRead() throws Exception {
        ExcelItemReader<MockUserRecord> reader = getReader(SAMPLE_FILE_100000, null);
        reader.open(new ExecutionContext());
        for(int i = 0; i < 100000; i++) {
            assertNotNull(reader.read());
        }
    }

    @Test
    public void givenFileWith5KRecordsWhenReadThenRead() throws Exception {
        ExcelItemReader<MockUserRecord> reader = getReader(SAMPLE_FILE_5000, null);
        reader.open(new ExecutionContext());
        for(int i = 0; i < 5000; i++) {
            assertNotNull(reader.read());
        }
    }

    @Test
    public void givenFileWhenReadFirstSheetThenRead() throws Exception {
        ExcelItemReader reader = getReader(SAMPLE_FILE, 0);
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
        assertNull(reader.read());
    }

}
