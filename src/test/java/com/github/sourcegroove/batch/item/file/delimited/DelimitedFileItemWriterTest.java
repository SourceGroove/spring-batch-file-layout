package com.github.sourcegroove.batch.item.file.delimited;

import com.github.sourcegroove.batch.item.file.mock.MockFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

public class DelimitedFileItemWriterTest {
    private static final Log log = LogFactory.getLog(DelimitedFileItemWriterTest.class);
    @Test
    public void givenWriterWhenWriteThenCorrectDateFormats() throws Exception {

        DelimitedLayout layout = new DelimitedLayout()
                .linesToSkip(0)
                .column("recordType")
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .layout();
        
        DelimitedFileItemWriter writer = layout.getItemWriter();
        
        String expected = "USER,0001,Neo,Anderson,19780930";
        String actual = writer.getLineAggregator().aggregate(MockFactory.getNeo());
        actual = writer.getLineAggregator().aggregate(MockFactory.getNeo());
        
        Assert.assertEquals(expected, actual);
        
    }

}
