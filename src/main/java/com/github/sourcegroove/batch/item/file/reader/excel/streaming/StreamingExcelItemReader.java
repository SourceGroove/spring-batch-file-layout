package com.github.sourcegroove.batch.item.file.reader.excel.streaming;

import com.github.sourcegroove.batch.item.file.reader.excel.AbstractExcelItemReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.Styles;
import org.springframework.util.ClassUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

public class StreamingExcelItemReader<T>  extends AbstractExcelItemReader<T> {

    protected final Log log = LogFactory.getLog(getClass());
    private ReadOnlySharedStringsTable strings;
    private Styles styles;

    public StreamingExcelItemReader() {
        super();
        this.setName(ClassUtils.getShortName(this.getClass()));
    }


    @Override
    protected Iterator loadSheets() {
        try {
            OPCPackage pkg = OPCPackage.open(this.resource.getInputStream());
            XSSFReader xssfReader = new XSSFReader(pkg);
            this.strings = new ReadOnlySharedStringsTable(pkg);
            this.styles = xssfReader.getStylesTable();
            return xssfReader.getSheetsData();
            //return (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        } catch (Throwable e) {
            throw new RuntimeException("Error opening workbook", e);
        }
    }

    @Override
    protected void doClose() throws Exception {}


    protected Iterator<List<Object>> loadNextSheet(){
        try (InputStream stream = (InputStream)this.sheetIterator.next()) {
            DataFormatter formatter = new StreamingExcelDataFormatter();
            StreamingSheetContentsHandler sheetHandler = new StreamingSheetContentsHandler();
            ContentHandler handler = new XSSFSheetXMLHandler(this.styles, this.strings, sheetHandler, formatter, false);
            try {
                XMLReader sheetParser = SAXHelper.newXMLReader();
                sheetParser.setContentHandler(handler);
                sheetParser.parse(new InputSource(stream));
                return sheetHandler.getRows().values().iterator();
            } catch (SAXException | ParserConfigurationException | IOException e) {
                throw new RuntimeException("Error processing sheet", e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading sheet", e);
        }
    }





}
