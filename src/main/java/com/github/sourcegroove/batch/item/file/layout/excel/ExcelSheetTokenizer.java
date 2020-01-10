package com.github.sourcegroove.batch.item.file.layout.excel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.Styles;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.springframework.batch.item.file.transform.DefaultFieldSetFactory;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.FieldSetFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelSheetTokenizer {
    private FieldSetFactory fieldSetFactory = new DefaultFieldSetFactory();
    private DataFormatter formatter = new ExcelDataFormatter();
    private int linesToSkip = 0;
    private String[] names;

    public void setFormatter(DataFormatter formatter){
        this.formatter = formatter;
    }
    public void setLinesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
    }
    public void setNames(String[] names){
        this.names = names;
    }

    public void afterPropertiesSet(){
        Assert.notNull(this.formatter, "'formatter' cannot be null");
        Assert.notNull(this.names, "'names' cannot be null");
    }

    public Iterator<FieldSet> tokenize(InputStream sheet, Styles styles, ReadOnlySharedStringsTable strings){
        SheetContentHandler sheetHandler = new SheetContentHandler();
        sheetHandler.setLinesToSkip(this.linesToSkip);
        ContentHandler handler = new XSSFSheetXMLHandler(styles, strings, sheetHandler, formatter, false);
        try {
            XMLReader sheetParser = SAXHelper.newXMLReader();
            sheetParser.setContentHandler(handler);
            sheetParser.parse(new InputSource(sheet));
            return tokenize(sheetHandler.getRows());
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException("Error processing sheet", e);
        }

    }

    private Iterator<FieldSet> tokenize(Map<Integer, List<Object>> rows){
        if(CollectionUtils.isEmpty(rows)){
            return new ArrayList<FieldSet>().iterator();
        }
        return rows
                .values()
                .stream()
                .map(r -> tokenize(r))
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
                .iterator();
    }
    private FieldSet tokenize(List<Object> row){
        if(CollectionUtils.isEmpty(row)){
            return null;
        }
        String[] values = row.toArray(new String[row.size()]);
        return this.fieldSetFactory.create(values, this.names);
    }


    private class SheetContentHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
        private final Log log = LogFactory.getLog(getClass());
        private Map<Integer, List<Object>> rows = new HashMap<>();
        private int currentRow = 0;
        private int linesToSkip;

        public void setLinesToSkip(int linesToSkip){
            this.linesToSkip = linesToSkip;
        }
        public Map<Integer, List<Object>> getRows(){
            return this.rows;
        }


        @Override
        public void startRow(int rowNum) {
            log.trace("Starting row" + rowNum);
            this.currentRow = rowNum;
        }
        @Override
        public void endRow(int rowNum) {}

        @Override
        public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            if(currentRow < this.linesToSkip){
                log.trace("Skipping row " + currentRow);
                return;
            }
            List<Object> columns = this.rows.get(currentRow);
            if(columns == null){
                columns = new ArrayList<>();
            }
            columns.add(formattedValue);
            log.trace("Adding column data to row " + currentRow);
            this.rows.put(currentRow, columns);
        }

    }
}
