package com.github.sourcegroove.batch.item.file.fixed;

import org.apache.commons.lang3.StringUtils;

public enum Format {
    STRING,
    STRING_LFET, // Left aligned string - same as STRING
    STRING_RIGHT, //Right aligned string
    INTEGER,
    ZD, // left aligned number
    DECIMAL("#.##"),
    YYYYMMDD("yyyyMMdd"), //date
    YYYYMM("yyyyMM"), //date
    YYYY("yyyy"), //date
    MMYYYY("MMyyyy"), //date
    FILLER;

    private String pattern;

    Format() {
    }

    Format(String pattern) {
        this.pattern = pattern;
    }

    public boolean hasPattern(){
        return StringUtils.isNotBlank(this.pattern);
    }
    public String getPattern() {
        return this.pattern;
    }
    
    public boolean isRightAligned() {
        return this == Format.DECIMAL
                || this == Format.INTEGER
                || this == Format.STRING_RIGHT;
    }

    public boolean isDateFormat() {
        return this == Format.YYYYMMDD
                || this == Format.YYYYMM
                || this == Format.YYYY
                || this == Format.MMYYYY;
    }


}
