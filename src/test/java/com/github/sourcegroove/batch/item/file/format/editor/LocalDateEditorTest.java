package com.github.sourcegroove.batch.item.file.format.editor;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateEditorTest {
    
    @Test
    public void testFormat(){
        String str = "4/11/2019";
        DateTimeFormatter.ofPattern("M/d/yyyy").parse(str);
        LocalDateEditor editor = new LocalDateEditor("M/dd/yyyy");
        editor.setAsText(str);
        LocalDate d = (LocalDate)editor.getValue();
    }
}
