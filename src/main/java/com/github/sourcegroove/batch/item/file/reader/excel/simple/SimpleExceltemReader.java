package com.github.sourcegroove.batch.item.file.reader.excel.simple;

import com.github.sourcegroove.batch.item.file.reader.excel.ExcelItemReader;
import com.github.sourcegroove.batch.item.file.reader.excel.ExcelRowMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Iterator;
import java.util.Set;

public class SimpleExceltemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements ExcelItemReader<T> {
    protected final Log log = LogFactory.getLog(getClass());

    private Resource resource;
    private ExcelRowMapper<T> rowMapper;
    private int linesToSkip = 0;
    private Set<Integer> sheetsToRead;

    private Workbook workbook;
    private InputStream workbookStream;
    private Sheet sheet;
    private Iterator<Row> rowIterator;
    private int sheetIndex = -1;
    private int rowNumber = -1;

    public SimpleExceltemReader() {
        super();
        this.setName(ClassUtils.getShortName(this.getClass()));
    }

    @Override
    protected T doRead() throws Exception {
        if(this.rowIterator != null && this.rowIterator.hasNext()){
            return readNextRow();
        } else {
            return readNextSheet();
        }
    }

    @Override
    protected void doOpen() throws Exception {
        Assert.isTrue(this.resource.exists(), "resource does not exist");
        Assert.isTrue(this.resource.isReadable(), "resource is not readable");

        try {
            this.workbookStream = this.resource.getInputStream();
            if (!this.workbookStream.markSupported() && !(this.workbookStream instanceof PushbackInputStream)) {
                throw new IllegalStateException("InputStream MUST either support mark/reset, or be wrapped as a PushbackInputStream");
            }
            this.workbook = WorkbookFactory.create(this.workbookStream);
            this.workbook.setMissingCellPolicy(Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        } catch (IOException e) {
            throw new RuntimeException("Error opening workbook", e);
        }
    }

    @Override
    protected void doClose() throws Exception {
        this.workbook.close();
        if (workbookStream != null) {
            workbookStream.close();
        }
        this.workbook = null;
        this.workbookStream = null;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.resource, "'resource' not set");
        Assert.notNull(this.rowMapper, "'rowMapper' not set");
    }
    public void setLinesToSkip(int linesToSkip){
        this.linesToSkip = linesToSkip;
    }
    public void setSheetsToRead(Set<Integer> sheetsToRead){
        this.sheetsToRead = sheetsToRead;
    }
    public void setRowMapper(ExcelRowMapper<T> rowMapper){
        this.rowMapper = rowMapper;
    }

    private T readNextRow() throws Exception {
        rowNumber++;
        Row row = this.rowIterator.next();
        if(rowNumber <= this.linesToSkip){
            return doRead();
        } else {
            return this.rowMapper.mapRow(row, rowNumber);
        }
    }
    private T readNextSheet() throws Exception {
        this.sheetIndex++;
        rowNumber = 0;
        boolean shouldRead = this.sheetsToRead == null || this.sheetsToRead.contains(this.sheetIndex);
        boolean canRead = this.sheetIndex < this.workbook.getNumberOfSheets();
        if(shouldRead && canRead){
            this.sheet = this.workbook.getSheetAt(this.sheetIndex);
            this.rowIterator = this.sheet.rowIterator();
            log.debug("Processing sheet " + this.sheet.getSheetName() + " at index " + this.sheetIndex);
            return doRead();

        } else if(canRead){
            log.debug("Skipping sheet at index " + this.sheetIndex);
            return readNextSheet();

        } else {
            log.debug("No more sheets to process");
            return null;

        }
    }

}

