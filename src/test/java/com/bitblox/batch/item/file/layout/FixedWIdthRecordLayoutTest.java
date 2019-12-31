package com.bitblox.batch.item.file.layout;

import org.junit.Test;
import org.springframework.batch.item.file.transform.Range;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class FixedWIdthRecordLayoutTest {

    @Test
    public void givenNonConsecutiveFieldsWhenFormatThenFiller(){
        FixedWidthRecordLayout record = new FixedWidthRecordLayout();
        record.setFieldNames(Arrays.asList("fieldOne", "fieldTwo"));
        record.setFieldRanges(Arrays.asList(new Range(1,10), new Range(21, 30)));

        String format = record.getFormat();
        assertEquals("%-10.10s          %-10.10s", format);

        String lineA = String.format(format, "1234567890", "1234567890");
        assertEquals(30, lineA.length());
        assertEquals("1234567890          1234567890", lineA);

        String lineB = String.format(format, "1", "21");
        assertEquals(30, lineB.length());
        assertEquals("1                   21        ", lineB);
    }

    @Test
    public void givenConsecutiveFieldsWhenFormatThenCorrect(){
        FixedWidthRecordLayout record = new FixedWidthRecordLayout();
        record.setFieldNames(Arrays.asList("fieldOne", "fieldTwo"));
        record.setFieldRanges(Arrays.asList(new Range(1,10), new Range(11, 20)));
        String format = record.getFormat();
        assertEquals("%-10.10s%-10.10s", format);
    }
}
