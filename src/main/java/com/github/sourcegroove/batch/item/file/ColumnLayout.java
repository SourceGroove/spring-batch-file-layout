package com.github.sourcegroove.batch.item.file;

import com.github.sourcegroove.batch.item.file.format.Format;

public class ColumnLayout {

    private String name;
    private Integer start;
    private Integer end;
    private Format format;

    public String getName() {
        return name;
    }

    public ColumnLayout setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getStart() {
        return start;
    }

    public ColumnLayout setStart(Integer start) {
        this.start = start;
        return this;
    }

    public Integer getEnd() {
        return end;
    }

    public ColumnLayout setEnd(Integer end) {
        this.end = end;
        return this;
    }

    public Format getFormat() {
        return format;
    }

    public ColumnLayout setFormat(Format format) {
        this.format = format;
        return this;
    }
}
