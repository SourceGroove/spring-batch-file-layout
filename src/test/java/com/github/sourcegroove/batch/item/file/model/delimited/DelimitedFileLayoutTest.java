package com.github.sourcegroove.batch.item.file.model.delimited;

import com.github.sourcegroove.batch.item.file.model.delimited.DelimitedFileLayout;
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
