package com.github.sourcegroove.batch.item.file.layout.excel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.Styles;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ExcelItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements ResourceAwareItemReaderItemStream<T>, InitializingBean {

    protected final Log log = LogFactory.getLog(getClass());
    private Resource resource;
    private FieldSetMapper<T> fieldSetMapper;
    private ExcelSheetTokenizer sheetTokenizer;
    private XSSFReader.SheetIterator sheetIterator;
    private Iterator<FieldSet> rowIterator;
    private ReadOnlySharedStringsTable strings;
    private Styles styles;

    public ExcelItemReader() {
        this.setName(ClassUtils.getShortName(this.getClass()));
    }
    public void setSheetTokenizer(ExcelSheetTokenizer sheetTokenizer){
        this.sheetTokenizer = sheetTokenizer ;
    }
    public void setFieldSetMapper(FieldSetMapper<T> fieldSetMapper){
        this.fieldSetMapper = fieldSetMapper;
    }
    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.resource, "'resource' is required");
        Assert.notNull(this.sheetTokenizer, "'sheetProcessor' is required");
        Assert.notNull(this.fieldSetMapper, "'fieldSetMapper' is required");
    }

    @Override
    protected T doRead() throws Exception {

        if(this.rowIterator.hasNext()){
            return this.fieldSetMapper.mapFieldSet(this.rowIterator.next());

        } else if (this.sheetIterator.hasNext()){
            this.processNextSheet();
            return this.doRead();

        } else {
            log.debug("Nothing left to read... done");
            return null;
        }
    }

    @Override
    protected void doOpen() throws Exception {
        Assert.notNull(this.resource, "'resource' is required");
        if (!this.resource.exists()) {
            log.warn("Input resource does not exist '" + this.resource.getDescription() + "'.");
            return;
        }
        if (!this.resource.isReadable()) {
            log.warn("Input resource is not readable '" + this.resource.getDescription() + "'.");
            return;
        }
        this.openWorkbook();
        this.processNextSheet();
    }

    @Override
    protected void doClose() throws Exception { }


    private void openWorkbook() {
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.open(this.resource.getInputStream());
            XSSFReader xssfReader = new XSSFReader(pkg);
            this.strings = new ReadOnlySharedStringsTable(pkg);
            this.styles = xssfReader.getStylesTable();
            this.sheetIterator = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        } catch (Throwable e) {
            throw new RuntimeException("Error opening workbook", e);
        }
    }

    private void processNextSheet(){
        if(!sheetIterator.hasNext()){
            log.info("No more worksheets");
            return;
        }

        try (InputStream stream = this.sheetIterator.next()) {
            log.info("Processing sheet " + this.sheetIterator.getSheetName());
            this.rowIterator = this.sheetTokenizer.tokenize(stream, this.styles, this.strings);
        } catch (IOException e) {
            throw new RuntimeException("Error reading sheet", e);
        }
    }



}
