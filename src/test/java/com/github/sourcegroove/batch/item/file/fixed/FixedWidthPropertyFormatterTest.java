package com.github.sourcegroove.batch.item.file.fixed;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

public class FixedWidthPropertyFormatterTest {
    
    @Test
    public void givenYYYYWhenReformatThenDateString(){
        FixedWidthPropertyFormatter formatter = new FixedWidthPropertyFormatter();
        formatter.names(Arrays.asList("dateOfBirth"));
        formatter.formats(Arrays.asList(Format.YYYY));
        assertEquals("20190101", formatter.formatForWrite("dateOfBirth", "2019"));
    }

    @Test
    public void givenYYYYMMWhenReformatThenDateString(){
        FixedWidthPropertyFormatter formatter = new FixedWidthPropertyFormatter();
        formatter.names(Arrays.asList("dateOfBirth"));
        formatter.formats(Arrays.asList(Format.YYYYMM));
        assertEquals("20190901", formatter.formatForWrite("dateOfBirth", "201909"));
    }

    @Test
    public void givenYYYYMMDDWhenReformatThenDateString(){
        FixedWidthPropertyFormatter formatter = new FixedWidthPropertyFormatter();
        formatter.names(Arrays.asList("dateOfBirth"));
        formatter.formats(Arrays.asList(Format.YYYYMMDD));
        assertEquals("20190930", formatter.formatForWrite("dateOfBirth", "20190930"));
    }

    @Test
    public void givenMMYYYYDDWhenReformatThenDateString(){
        FixedWidthPropertyFormatter formatter = new FixedWidthPropertyFormatter();
        formatter.names(Arrays.asList("dateOfBirth"));
        formatter.formats(Arrays.asList(Format.MMYYYY));
        assertEquals("09201901", formatter.formatForWrite("dateOfBirth", "092019"));
    }
    
    
}
