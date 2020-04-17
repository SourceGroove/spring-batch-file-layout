package com.github.sourcegroove.batch.item.file.fixed;

import com.github.sourcegroove.batch.item.file.format.Format;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.transform.Range;

public class FixedWidthLineFormatBuilder {

    private static final Format DEFAULT_FORMAT = Format.STRING;
    private final Log log = LogFactory.getLog(getClass());
    private boolean acceptDateObjects;
    private int length = 0;
    private StringBuilder format = new StringBuilder();

    /*
     * This builder produces a printf string which will throw errors when trying to handle null objects like Date, Double, Integer.
     * When Date objects are null and applied to something like '%tY%<tm%<td', an exception is thrown.  
     * 
     * Therefore, the default behavior for this builder is to treat date objects as pre formatted strings.
     * 
     * The below formats are assumed to be provided as pre formatted strings (not dates as they would imply)
     * Format.YYYY
     * Format.YYYYMMDD
     * Format.YYYYMM
     * Format.YYYY
     * Format.MMYYYY
     * 
     * If you  want to have it create the pure printf, set the 'acceptDateObjects' parameter to 'true' and be sure not to pass any nulls!
     *
     * * param acceptDateObjects when true, creates a format that accepts Date objects, otherwise it expects a String
     */
    public FixedWidthLineFormatBuilder(boolean acceptDateObjects){
        this.acceptDateObjects = acceptDateObjects;
    }
    public FixedWidthLineFormatBuilder(){
        this.acceptDateObjects = false;
    }
    
    
    //start / end  overloads
    public FixedWidthLineFormatBuilder append(int start, int end) {
        return append(start, end, DEFAULT_FORMAT);
    }
    public FixedWidthLineFormatBuilder append(int start, int end, String value) {
        return append(new Range(start, end), value);
    }
    public FixedWidthLineFormatBuilder append(int start, int end, Format format) {
        return this.append(new Range(start, end), format);
    }

    // range overloads
    public FixedWidthLineFormatBuilder append(Range range) {
        return this.append(range, DEFAULT_FORMAT);
    }
    public FixedWidthLineFormatBuilder append(Range range, String value) {
        return append(range, Format.STRING, value);
    }
    public FixedWidthLineFormatBuilder append(Range range, Format format) {
        return append(range, format, null);
    }
    public FixedWidthLineFormatBuilder append(Range range, Format format, String value) {
        int gap = range.getMin() - 1 - this.length;
        if (gap > 0) {
            //gap from previous range, so pad it...
            this.append(gap, Format.FILLER);
        }
        int width = range.getMax() - range.getMin() + 1;
        return this.append(width, format, value);
    }

    //width overloads
    public FixedWidthLineFormatBuilder append(int width) {
        return this.append(width, DEFAULT_FORMAT);
    }
    public FixedWidthLineFormatBuilder append(int width, Format format) {
        return append(width, format, null);
    }
    public FixedWidthLineFormatBuilder append(int width, Format format, String value) {
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

    @Override
    public String toString() {
        return this.format.toString();
    }

    private String getFormat(Format type, int width) {
        return this.acceptDateObjects ?
                getTrueFormat(type, width) :
                getDatesAsStringFormat(type, width);
    }
    private String getDatesAsStringFormat(Format type, int width) {
        String fmt = null;
        
        if (type == Format.FILLER) {
            fmt = StringUtils.rightPad(" ", width);
        } else if (type == Format.DECIMAL) {
            fmt = "%0" + width + ".2f";

        } else if (type == Format.INTEGER) {
            fmt = "%0" + width + "d";

        } else if (type == Format.ZD) {
            fmt = "%-" + width + "." + width + "s";
            
        } else {
            String flags = !type.isRightAligned() ? "-" : "";
            fmt = "%" + flags + width + "." + width + "s";
        }
        
        return fmt;
    }
    
    private String getTrueFormat(Format type, int width) {
        String fmt = null;
        if (type == Format.FILLER) {
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
