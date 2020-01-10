package com.github.sourcegroove.batch.item.file.layout.excel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelDataFormatter extends DataFormatter {
    private final Log log = LogFactory.getLog(getClass());
    private String datePattern = "yyyyMMdd";

    public void setDatePattern(String datePattern){
        this.datePattern = datePattern;
    }

    @Override
    public String formatRawCellContents(double value, int formatIndex, String formatString, boolean use1904Windowing) {
        if (DateUtil.isADateFormat(formatIndex, formatString)) {
            if (DateUtil.isValidExcelDate(value)) {
                Date d = DateUtil.getJavaDate(value, use1904Windowing);
                try {
                    return new SimpleDateFormat(datePattern).format(d);
                } catch (Exception e) {
                    log.error("Error formatting date " + d + " using " + datePattern);
                }
            }
        }
        return new DecimalFormat("##0.#####").format(value);
    }
}
