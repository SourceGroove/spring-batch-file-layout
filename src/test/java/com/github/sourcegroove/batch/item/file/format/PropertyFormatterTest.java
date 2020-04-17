package com.github.sourcegroove.batch.item.file.format;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

public class PropertyFormatterTest {
    @Test
    public void givenIntegerWhenReformatThenDateString(){
        PropertyFormatter formatter = new PropertyFormatter();
        formatter.names(Arrays.asList("age"));
        formatter.formats(Arrays.asList(Format.INTEGER));
        assertEquals(0, formatter.formatForWrite("age", null));
    }
    
    @Test
    public void givenYYYYWhenReformatThenDateString(){
        PropertyFormatter formatter = new PropertyFormatter();
        formatter.names(Arrays.asList("dateOfBirth"));
        formatter.formats(Arrays.asList(Format.YYYY));
        assertEquals("20190101", formatter.formatForWrite("dateOfBirth", "2019"));
    }

    @Test
    public void givenYYYYMMWhenReformatThenDateString(){
        PropertyFormatter formatter = new PropertyFormatter();
        formatter.names(Arrays.asList("dateOfBirth"));
        formatter.formats(Arrays.asList(Format.YYYYMM));
        assertEquals("20190901", formatter.formatForWrite("dateOfBirth", "201909"));
    }

    @Test
    public void givenYYYYMMDDWhenReformatThenDateString(){
        PropertyFormatter formatter = new PropertyFormatter();
        formatter.names(Arrays.asList("dateOfBirth"));
        formatter.formats(Arrays.asList(Format.YYYYMMDD));
        assertEquals("20190930", formatter.formatForWrite("dateOfBirth", "20190930"));
    }

    @Test
    public void givenMMYYYYDDWhenReformatThenDateString(){
        PropertyFormatter formatter = new PropertyFormatter();
        formatter.names(Arrays.asList("dateOfBirth"));
        formatter.formats(Arrays.asList(Format.MMYYYY));
        assertEquals("09201901", formatter.formatForWrite("dateOfBirth", "092019"));
    }
    
    
}
