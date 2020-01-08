package com.github.sourcegroove.batch.item.file.layout.delimited;

import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import com.github.sourcegroove.batch.item.file.layout.delimited.builder.DelimitedFileLayoutBuilder;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class DelimitedFileLayoutTest {

    @Test
    public void givenInvalidLayoutWhenIsValidThenFalse(){
        FileLayout layout = new DelimitedFileLayoutBuilder()
                .record(null)
                    .column("colOne")
                    .column("colTwo")
                .and()
                .build();
        assertFalse(layout.isValid());
    }
}
