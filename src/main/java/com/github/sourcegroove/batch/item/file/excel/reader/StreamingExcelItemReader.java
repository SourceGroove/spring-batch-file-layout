package com.github.sourcegroove.batch.item.file.excel.reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.Styles;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StreamingExcelItemReader<T>  extends AbstractItemCountingItemStreamItemReader<T> implements ExcelItemReader<T> {
    protected final Log log = LogFactory.getLog(getClass());
    protected Resource resource;
    protected XSSFReader.SheetIterator sheetIterator;
    private Iterator<List<String>> rowIterator;
    private ExcelRowMapper<T> rowMapper;
    private int linesToSkip = 0;
    private Set<Integer> sheetsToRead;
    private int sheetIndex = -1;
    private int rowNumber = -1;
    private ReadOnlySharedStringsTable strings;
    private Styles styles;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;

    public StreamingExcelItemReader() {
        super();
        this.setName(ClassUtils.getShortName(this.getClass()));
    }

    @Override
    protected T doRead() throws Exception {
        if (this.rowIterator != null && this.rowIterator.hasNext()) {
            rowNumber++;
            log.debug("Processing row " + rowNumber);
            List<String> row = this.rowIterator.next();
            return this.rowNumber <= this.linesToSkip ? doRead() : this.rowMapper.mapRow(row, rowNumber);
        } else {
            return nextSheet();
        }
    }
    @Override
    protected void doOpen() throws Exception {
        log.info("doOpen was just called");
        Assert.isTrue(this.resource.exists(), "resource does not exist");
        Assert.isTrue(this.resource.isReadable(), "resource is not readable");
        try {
            OPCPackage pkg = OPCPackage.open(this.resource.getInputStream());
            XSSFReader xssfReader = new XSSFReader(pkg);
            this.strings = new ReadOnlySharedStringsTable(pkg);
            this.styles = xssfReader.getStylesTable();
            this.sheetIterator = (XSSFReader.SheetIterator)xssfReader.getSheetsData();
            log.debug("Loaded file " + this.resource.getFilename());
        } catch (Throwable e) {
            throw new RuntimeException("Error opening workbook", e);
        }
    }

    @Override
    protected void doClose() throws Exception {}

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
        log.debug("Getting next sheet");
        this.sheetIndex++;
        this.rowNumber = 0;
        boolean shouldRead = this.sheetsToRead == null || this.sheetsToRead.contains(this.sheetIndex);
        boolean canRead = this.sheetIterator.hasNext();

        if (shouldRead && canRead) {
            log.debug("Processing sheet " + this.sheetIndex);
            this.rowIterator = getNextSheet();
            return doRead();

        } else if (canRead) {
            log.debug("Skipping sheet at index " + this.sheetIndex);
            this.sheetIterator.next();
            return nextSheet();

        } else {
            log.debug("No more sheets to process");
            return null;
        }
    }

    protected Iterator<List<String>> getNextSheet(){
        try (InputStream stream = this.sheetIterator.next()) {
            DataFormatter formatter = new ExcelDataFormatter();
            StreamingSheetContentsHandler sheetHandler = new StreamingSheetContentsHandler();
            ContentHandler handler = new XSSFSheetXMLHandler(this.styles, this.strings, sheetHandler, formatter, false);
            try {
                XMLReader sheetParser = SAXHelper.newXMLReader();
                sheetParser.setContentHandler(handler);
                sheetParser.parse(new InputSource(stream));
                return sheetHandler.getRowIterator();
            } catch (SAXException | ParserConfigurationException | IOException e) {
                throw new RuntimeException("Error processing sheet", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading sheet", e);
        }
    }


    public class StreamingSheetContentsHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
        private Map<Integer, List<String>> rows = new HashMap<>();
        private int currentRow = 0;
        private int currentColumn = 0;

        public Iterator<List<String>> getRowIterator(){
            return this.rows.values().iterator();
        }
        @Override
        public void startRow(int rowNum) {
            this.currentRow = rowNum;
        }
        @Override
        public void endRow(int rowNum) {
            this.currentColumn = 0;
        }

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            List<String> columns = this.rows.get(currentRow);
            if(columns == null){
                columns = new ArrayList<>();
            }
            if(cellReference == null) {
                cellReference = new CellAddress(currentRow, currentColumn).formatAsString();
            }
            int thisCol = (new CellReference(cellReference)).getCol();
            int missingColumns = thisCol - currentColumn - 1;
            for (int i = 0; i< missingColumns; i++) {
                columns.add("");
            }
            currentColumn = thisCol;
            columns.add(formattedValue);
            this.rows.put(currentRow, columns);
        }
    }
    private class ExcelDataFormatter extends DataFormatter {
        @Override
        public String formatRawCellContents(double value, int formatIndex, String formatString, boolean use1904Windowing) {
            if (DateUtil.isADateFormat(formatIndex, formatString) && DateUtil.isValidExcelDate(value)) {
                Date date = DateUtil.getJavaDate(value, use1904Windowing);
                LocalDateTime dt = new Timestamp(date.getTime()).toLocalDateTime();
                return dateFormatter.format(dt);
            }
            return String.valueOf(value);
        }
    }


}
