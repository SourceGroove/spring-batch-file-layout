package com.github.sourcegroove.batch.item.file.layout.delimited;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class DelimitedFileLayoutTest {

    @Test
    public void givenInvalidLayoutWhenIsValidThenFalse(){
        assertFalse(
                new DelimitedFileLayout()
                        .record(null)
                            .column("colOne")
                            .column("colTwo")
                        .and().isValid());
    }
}
