package com.github.sourcegroove.batch.item.file.reader.excel;

import com.github.sourcegroove.batch.item.file.reader.excel.ExcelItemReader;
import com.github.sourcegroove.batch.item.file.reader.excel.ExcelRowMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SimpleExcelItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements ExcelItemReader<T> {
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
    private FormulaEvaluator formulaEvaluator;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;

    public SimpleExcelItemReader() {
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
            this.formulaEvaluator = this.workbook.getCreationHelper().createFormulaEvaluator();
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
            List<String> values = getValues(row);
            return this.rowMapper.mapRow(values, rowNumber);
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
    private String format(Object object){
        if(object instanceof Date){
            Date date = (Date)object;
            LocalDateTime dt = new Timestamp(date.getTime()).toLocalDateTime();
            return DateTimeFormatter.BASIC_ISO_DATE.format(dt);
        }
        return object.toString();
    }

    private List<String> getValues(Row row){
        List<Object> values = new ArrayList<>();
        Iterator<Cell> cellIterator = row.cellIterator();
        while(cellIterator.hasNext()){
            Cell cell = cellIterator.next();
            values.add(getValue(cell));
        }
        return values.stream()
                .map(obj -> format(obj))
                .collect(Collectors.toList());
    }
    private Object getValue(Cell cell){
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.BOOLEAN){
            return cell.getBooleanCellValue();
        } else if (cell.getCellType() == CellType.FORMULA){
            return getFormulaValue(cell);
        } else {
            return cell.getStringCellValue();
        }
    }
    private Object getFormulaValue(Cell cell){
        CellValue v = formulaEvaluator.evaluate(cell);
        if(v.getCellType() == CellType.NUMERIC){
            return v.getNumberValue();
        } else if (v.getCellType() == CellType.BOOLEAN){
            return v.getBooleanValue();
        } else {
            return v.getStringValue();
        }
    }

}

