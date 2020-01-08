package com.github.sourcegroove.batch.item.file.layout.fixed.builder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.file.transform.Range;

public class StringFormatBuilder {
    public enum Format {
        STRING,
        INTEGER,
        ZD, // left aligned number
        DECIMAL,
        YYYYMMDD, //date
        YYYYMM, //date
        YYYY, //date
        MMYYYY, //date
        CONSTANT;
    }

    private static final Format DEFAULT_FORMAT = Format.STRING;
    private int length = 0;
    private StringBuilder format = new StringBuilder();

    public StringFormatBuilder append(int start, int end){
        return append(start,end, DEFAULT_FORMAT);
    }
    public StringFormatBuilder append(int start, int end, Format format){
        return this.append(new Range(start, end), format);
    }
    public StringFormatBuilder append(Range range){
        return this.append(range, DEFAULT_FORMAT);
    }
    public StringFormatBuilder append(Range range, Format format){
        int gap = range.getMin() - 1 - this.length;
        if(gap > 0){
            //gap from previous range, so pad it...
            this.append(gap, Format.CONSTANT);
        }
        int width = range.getMax() - range.getMin() + 1;
        return this.append(width, format);
    }
    public StringFormatBuilder append(int width){
        return this.append(width, DEFAULT_FORMAT);
    }
    public StringFormatBuilder append(int width, Format format){
        this.length += width;
        this.format.append(getFormat(format, width));
        return this;
    }
    public String build(){
        return this.toString();
    }
    public String toString(){
        return this.format.toString();
    }


    private String getFormat(Format type, int width){
        String fmt = null;
        if (type == Format.CONSTANT) {
            fmt = StringUtils.rightPad(" ", width);

        } else if (type == Format.DECIMAL) {
            fmt = "%0" + width + ".2f";

        } else if (type == Format.INTEGER) {
            fmt = "%0" + width + "d";

        } else if (type == Format.ZD){
            fmt = "%-" + width + "." + width + "s";

        } else if (type == Format.YYYYMMDD) {
            fmt = "%tY%<tm%<td";

        } else if (type == Format.YYYYMM) {
            fmt = "%tY%<tm";

        } else if (type == Format.MMYYYY) {
            fmt = "%tm%<tY";

        } else if (type == Format.YYYY) {
            fmt = "%tY";

        } else {
            String flags = "-";
            fmt = "%" + flags + width + "." + width + "s";
        }
        return fmt;
    }
}
