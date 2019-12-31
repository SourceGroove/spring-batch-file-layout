package com.bitblox.batch.item.file.layout;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.transform.Range;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FixedWidthRecordLayout implements RecordLayout {

    private enum ColumnType {
        STRING,
        INTEGER,
        ZD, // left aligned number
        DECIMAL,
        DATE,
        MONTH,
        CONSTANT;
    }

    private Class targetType;
    private String prefix = "*";
    private List<String> fieldNames = new ArrayList<>();
    private List<Range> fieldRanges = new ArrayList<>();
    private Map<Class<?>, PropertyEditor> editors = new HashMap<>();


    public String[] getFieldNameArray(){
        return getFieldNames().toArray(new String[getFieldNames().size()]);
    }
    public Range[] getFieldRangeArray(){
        return getFieldRanges().toArray(new Range[getFieldNames().size()]);
    }
    
    public String getFormat(){
        StringBuilder format = new StringBuilder();
        Range previous = null;
        for(int i = 0; i < getFieldNames().size(); i++){
            Range range = getFieldRanges().get(i);

            int gap = previous != null ? range.getMin() - 1 - previous.getMax() : 0;
            if(gap > 0){
                format.append(getFormat(ColumnType.CONSTANT, gap));
            }

            int width = range.getMax() - range.getMin() + 1;
            format.append(getFormat(ColumnType.STRING, width));
            previous = range;
        }
        return format.toString();
    }

    private String getFormat(ColumnType type, int width){
        String fmt = null;
        if (type == ColumnType.CONSTANT) {
            fmt = StringUtils.rightPad(" ", width);

        } else if (type == ColumnType.DECIMAL) {
            fmt = "%0" + width + ".2f";

        } else if (type == ColumnType.INTEGER) {
            fmt = "%0" + width + "d";

        } else if (type == ColumnType.ZD){
            fmt = "%-" + width + "." + width + "s";

        } else if (type == ColumnType.DATE) {
            fmt = "%tY%<tm%<td";

        } else if (type == ColumnType.MONTH) {
            fmt = "%tm%<tY";

        } else {
            String flags = "-";
            fmt = "%" + flags + width + "." + width + "s";
        }
        return fmt;
    }
}
