package com.github.sourcegroove.batch.item.file.fixed;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.transform.Range;

public class FixedWidthFormatBuilder {

    private static final Format DEFAULT_FORMAT = Format.STRING;
    private final Log log = LogFactory.getLog(getClass());
    private int length = 0;
    private StringBuilder format = new StringBuilder();

    //start / end  overloads
    public FixedWidthFormatBuilder append(int start, int end) {
        return append(start, end, DEFAULT_FORMAT);
    }
    public FixedWidthFormatBuilder append(int start, int end, String value) {
        return append(new Range(start, end), value);
    }
    public FixedWidthFormatBuilder append(int start, int end, Format format) {
        return this.append(new Range(start, end), format);
    }

    // range overloads
    public FixedWidthFormatBuilder append(Range range) {
        return this.append(range, DEFAULT_FORMAT);
    }
    public FixedWidthFormatBuilder append(Range range, String value) {
        return append(range, Format.STRING, value);
    }
    public FixedWidthFormatBuilder append(Range range, Format format) {
        return append(range, format, null);
    }
    public FixedWidthFormatBuilder append(Range range, Format format, String value) {
        int gap = range.getMin() - 1 - this.length;
        if (gap > 0) {
            //gap from previous range, so pad it...
            this.append(gap, Format.CONSTANT);
        }
        int width = range.getMax() - range.getMin() + 1;
        return this.append(width, format, value);
    }

    //width overloads
    public FixedWidthFormatBuilder append(int width) {
        return this.append(width, DEFAULT_FORMAT);
    }
    public FixedWidthFormatBuilder append(int width, Format format) {
        return append(width, format, null);
    }
    public FixedWidthFormatBuilder append(int width, Format format, String value) {
        String str = getFormat(format, width);
        if(value != null){
            str = String.format(str, StringUtils.leftPad(StringUtils.left(value, width), width));
        }
        this.length += width;
        this.format.append(str);
        log.trace("Current line format: length=" + this.length + ", format=" + this.format.toString());
        return this;
    }

    public String build() {
        return this.toString();
    }

    public String toString() {
        return this.format.toString();
    }

    private String getFormat(Format type, int width) {
        String fmt = null;
        if (type == Format.CONSTANT) {
            fmt = StringUtils.rightPad(" ", width);

        } else if (type == Format.DECIMAL) {
            fmt = "%0" + width + ".2f";

        } else if (type == Format.INTEGER) {
            fmt = "%0" + width + "d";

        } else if (type == Format.ZD) {
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
