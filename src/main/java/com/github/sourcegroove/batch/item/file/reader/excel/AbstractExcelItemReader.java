package com.github.sourcegroove.batch.item.file.reader.excel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class AbstractExcelItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements ResourceAwareItemReaderItemStream<T>, InitializingBean {
    protected final Log log = LogFactory.getLog(getClass());
    protected Resource resource;
    protected Iterator sheetIterator;
    private Iterator<List<Object>> rowIterator;
    private ExcelRowMapper<T> rowMapper;
    private int linesToSkip = 0;
    private Set<Integer> sheetsToRead;
    private int sheetIndex = -1;
    private int rowNumber = -1;

    protected abstract Iterator loadSheets();
    protected abstract Iterator<List<Object>> loadNextSheet();

    @Override
    protected T doRead() throws Exception {
        if (this.rowIterator != null && this.rowIterator.hasNext()) {
            rowNumber++;
            List<Object> row = this.rowIterator.next();
            return this.rowNumber <= this.linesToSkip ? doRead() : this.rowMapper.mapRow(row, rowNumber);
        } else {
            return nextSheet();
        }
    }
    @Override
    protected void doOpen() throws Exception {
        Assert.isTrue(this.resource.exists(), "resource does not exist");
        Assert.isTrue(this.resource.isReadable(), "resource is not readable");
        this.sheetIterator = loadSheets();
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
    public void setLinesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
    }
    public void setSheetsToRead(Set<Integer> sheetsToRead) {
        this.sheetsToRead = sheetsToRead;
    }
    public void setRowMapper(ExcelRowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }
    private T nextSheet() throws Exception {
        this.sheetIndex++;
        this.rowNumber = 0;
        boolean shouldRead = this.sheetsToRead == null || this.sheetsToRead.contains(this.sheetIndex);
        boolean canRead = this.sheetIterator.hasNext();

        if (shouldRead && canRead) {
            log.info("Processing sheet " + this.sheetIndex);
            this.rowIterator = loadNextSheet();
            return doRead();

        } else if (canRead) {
            log.debug("Skipping sheet at index " + this.sheetIndex);
            return nextSheet();

        } else {
            log.debug("No more sheets to process");
            return null;
        }
    }



}
