package com.github.sourcegroove.batch.item.file.reader.excel.streaming;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamingSheetContentsHandler implements XSSFSheetXMLHandler.SheetContentsHandler {
    private final Log log = LogFactory.getLog(getClass());
    private Map<Integer, List<Object>> rows = new HashMap<>();
    private int currentRow = 0;

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
        List<Object> columns = this.rows.get(currentRow);
        if(columns == null){
            columns = new ArrayList<>();
        }
        columns.add(formattedValue);
        log.trace("Adding column data to row " + currentRow);
        this.rows.put(currentRow, columns);
    }

}
