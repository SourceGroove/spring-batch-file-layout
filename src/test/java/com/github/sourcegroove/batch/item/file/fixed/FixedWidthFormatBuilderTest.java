package com.github.sourcegroove.batch.item.file.fixed;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class FixedWidthFormatBuilderTest {

    @Test
    public void givenStringBasedZDFormatWhenFormatThenCorrectFormat(){
        String format = new FixedWidthFormatBuilder()
                .append(1, 10, Format.ZD)
                .build();

        assertEquals("%-10.10s", format);
        String line = String.format(format, 6);
        assertEquals(10, line.length());
        assertEquals("6         ", line);
    }

    @Test
    public void givenStringBasedDecimalFormatWhenFormatThenCorrectFormat(){
        String format = new FixedWidthFormatBuilder()
                .append(1, 10, Format.DECIMAL)
                .build();

        assertEquals("%10.10s", format);
        String line = String.format(format, 6);
        assertEquals(10, line.length());
        assertEquals("         6", line);
    }
    
    @Test
    public void givenZDFormatWhenFormatThenCorrectFormat(){
        String format = new FixedWidthFormatBuilder(false)
                .append(1, 10, Format.ZD)
                .build();

        assertEquals("%-10.10s", format);
        String line = String.format(format, 6);
        assertEquals(10, line.length());
        assertEquals("6         ", line);
    }

    @Test
    public void givenDecimalFormatWhenFormatThenCorrectFormat(){
        String format = new FixedWidthFormatBuilder(false)
                .append(1, 10, Format.DECIMAL)
                .build();

        assertEquals("%010.2f", format);
        String line = String.format(format, 6.0);
        assertEquals(10, line.length());
        assertEquals("0000006.00", line);
    }
    @Test
    public void givenIntegerFormatWhenFormatThenCorrectFormat(){
        String format = new FixedWidthFormatBuilder(false)
                .append(1, 10, Format.INTEGER)
                .build();
        assertEquals("%010d", format);
        String line = String.format(format, 6);
        assertEquals(10, line.length());
        assertEquals("0000000006", line);
    }

    @Test
    public void givenYYYYFormatWhenFormatThenCorrectFormat(){
        String format = new FixedWidthFormatBuilder(false)
                .append(1, 10, Format.YYYY)
                .build();

        assertEquals("%tY", format);
        String line = String.format(format, LocalDate.of(2020,1,1));
        assertEquals(4, line.length());
        assertEquals("2020", line);
    }
    @Test
    public void givenYYYYMMDDFormatWhenFormatThenCorrectFormat(){
        String format = new FixedWidthFormatBuilder(false)
                .append(1, 10, Format.YYYYMMDD)
                .build();

        assertEquals("%tY%<tm%<td", format);
        String line = String.format(format, LocalDate.of(2020,1,1));
        assertEquals(8, line.length());
        assertEquals("20200101", line);
    }
    @Test
    public void givenMMYYYYFormatWhenFormatThenCorrectFormat(){
        String format = new FixedWidthFormatBuilder(false)
                .append(1, 10, Format.MMYYYY)
                .build();

        assertEquals("%tm%<tY", format);
        String line = String.format(format, LocalDate.of(2020,1,1));
        assertEquals(6, line.length());
        assertEquals("012020", line);
    }
    @Test
    public void givenYYYMMFormatWhenFormatThenCorrectFormat(){
        String format = new FixedWidthFormatBuilder(false)
                .append(1, 10, Format.YYYYMM)
                .build();

        assertEquals("%tY%<tm", format);
        String line = String.format(format, LocalDate.of(2020,1,1));
        assertEquals(6, line.length());
        assertEquals("202001", line);
    }

    @Test
    public void givenNonConsecutiveFieldsWhenFormatThenFiller(){
        String format = new FixedWidthFormatBuilder(false)
                .append(1, 10)
                .append(21, 30)
                .build();

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
        String format = new FixedWidthFormatBuilder(false)
                .append(1, 10)
                .append(11, 20)
                .build();
        assertEquals("%-10.10s%-10.10s", format);
    }
}
