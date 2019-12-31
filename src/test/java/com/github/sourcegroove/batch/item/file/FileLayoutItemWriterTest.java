package com.github.sourcegroove.batch.item.file;

import com.github.sourcegroove.batch.item.file.editors.LocalDateEditor;
import com.github.sourcegroove.batch.item.file.layout.FileLayout;
import com.github.sourcegroove.batch.item.file.layout.FixedWidthFileLayout;
import lombok.extern.java.Log;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Log
public class FileLayoutItemWriterTest {
    private static final String EXPORT_DIR = "./target/test-classes/files/";
    @Test
    public void givenRecordsWithFillerWhenWriteThenWritenWithFiller() throws Exception {
        FileLayout layout = new FixedWidthFileLayout()
                .record(MockUserRecord.class)
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .prefix("user*")
                .column("username", 1, 10)
                .column("firstName", 11, 20)
                .column("lastName", 31, 40)
                .column("dateOfBirth", 41, 48);

        Resource file = new FileSystemResource(EXPORT_DIR + "sample-file-output-filler.txt");
        
        FileLayoutItemWriter<MockUserRecord> writer = new FileLayoutItemWriter<>();
        writer.setFileLayout(layout);
        writer.setResource(file);
        writer.open(new ExecutionContext());
        writer.write(MockFactory.getUsers());

        //read it back in and validate....
        FileLayoutItemReader reader = new FileLayoutItemReader();
        reader.setFileLayout(layout);
        reader.setResource(file);
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

    @Test
    public void givenRecordsWithNoFillerWhenWriteThenWriten() throws Exception {
        FileLayout layout = new FixedWidthFileLayout()
                .record(MockUserRecord.class)
                .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
                .prefix("user*")
                .column("username", 1, 10)
                .column("firstName", 11, 20)
                .column("lastName", 21, 30)
                .column("dateOfBirth", 31, 38);

        Resource file = new FileSystemResource(EXPORT_DIR + "sample-file-output-no-filler.txt");
        FileLayoutItemWriter<MockUserRecord> writer = new FileLayoutItemWriter<>();
        writer.setFileLayout(layout);
        writer.setResource(file);
        writer.open(new ExecutionContext());
        writer.write(MockFactory.getUsers());

        //read it back in and validate....
        FileLayoutItemReader reader = new FileLayoutItemReader();
        reader.setFileLayout(layout);
        reader.setResource(file);
        reader.open(new ExecutionContext());
        MockFactory.assertNeo((MockUserRecord) reader.read());
        MockFactory.assertTrinity((MockUserRecord) reader.read());
    }

    

}
