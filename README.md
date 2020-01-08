# Spring Batch File Layouts

## Overview
Spring Batch File Layouts aim to simplify the construction of FlatFileItemReader/Writers for fixed width & delimited flat files.

This library sits on top of existing SpringBatch components and allows the developer to focus on defining a single file 
layout that can be used to both read and write flat files in a simplified manner. 

If you are familiar with SpringBatch FlatFileItemReader/Writer, then this should be super-obvious. It's really just a builder/facade abstraction
on top of Spring's out of the box components (standing on the shoulder's of giants).

## Rationale
The need for this library arose from building systems that rely heavily on importing and exporting large, structured 
data sets represented as flat files. The systems needed to be able to import and export data according to a ton of 
different file specifications that:   
- Often consisted of more than 100+ fields
- Usually contained multiple record types (that themselves had varying layouts - i.e. header, detail01, detail02, supplemental01, supplemental02, footer, trailer01, trailer02, etc..)
- Changed periodically
- Needed to be versioned over an effective date (i.e files generated on or after 1/1/2020, user file spec 1.2, everything before uses 1.1)

Using this library, we were able to create two simple SpringBatch jobs - one for import and one for export - we just supply the layout and 
a physical file at runtime.   

# Usage

## Maven Dependency
```
<dependency>
  <groupId>com.github.sourcegroove</groupId>
  <artifactId>spring-batch-file-layout</artifactId>
  <version>1.0.7</version>
</dependency>
```

## Gradle Dependency
```
implementation 'com.github.sourcegroove:spring-batch-file-layout:1.0.7'
```
# Declarative file layouts are used to create ItemReaders and ItemWriters
All you need to do is define your delimited or fixed width file layout and pass it to the FileLayoutItemReader or FileLayoutItemWriter.  By default, the
FlatFileItemX implementations uses Spring's BeanWrapperFieldSetMapper & BeanWrapperFieldExtractor to map your POJO's properties to either column 'Ranges' 
in a fixed width file or column order in a delimited file.  

You can, of course, override this by changing the FieldSetMapper/FieldExtractor on the ItemReader/Writers if you need to.

```java
FileLayout layout = new FixedWidthFileLayout()
    .record(MockUserRecord.class)
        .column("recordType", 1, 4)
        .column("username", 5, 10)
        .column("firstName", 11, 20)
        .column("lastName", 21, 30)
        .column("dateOfBirth", 31, 38)
    .build();

FileLayoutItemReader<MockUserRecord> reader = new FileLayoutItemReader<>();
reader.setFileLayout(layout);

FileLayoutItemWriter<MockUserRecord> writer = new FileLayoutItemWriter<>();
writer.setFileLayout(layout);
```

# File Layouts
There are two implementations of the FileLayout interface - FixedWidthFileLayout & DelimitedFileLayout.  Each of these
contains a collection of 'record layouts' that define the records in the file.

Defining layouts in Java is simple, but they can also be dynamically defined at runtime using persisted data

## Fixed width file layout simple
```java
FileLayout layout = new FixedWidthFileLayout()
    .record(MockUserRecord.class)
        .column("recordType", 1, 4)
        .column("username", 5, 10)
        .column("firstName", 11, 20)
        .column("lastName", 21, 30)
        .column("dateOfBirth", 31, 38)
    .build();
```
## Fixed width file layout with custom property editors and multiple record types
```java
FileLayout layout = new FixedWidthFileLayout()
    .linesToSkip(1)
    .record(MockUserRecord.class)
        .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
        .prefix("USER*")
        .column("recordType", 1, 4)
        .column("username", 5, 10)
        .column("firstName", 11, 20)
        .column("lastName", 21, 30)
        .column("dateOfBirth", 31, 38)
    .and()
    .record(MockRoleRecord.class)
        .prefix("ROLE*")
        .column("recordType", 1, 4)
        .column("roleKey", 5, 8)
        .column("role", 9, 20)
    .build();
```

## Delimited file layouts
```java
FileLayout layout = new DelimitedFileLayout()
        .linesToSkip(1)
        .record(MockUserRecord.class)
            .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
            .column("username")
            .column("firstName")
            .column("lastName")
            .column("dateOfBirth")
        .build();

FileLayoutItemReader<MockUserRecord> reader = new FileLayoutItemReader<>();
reader.setFileLayout(layout);

FileLayoutItemWriter<MockUserRecord> writer = new FileLayoutItemWriter<>();
writer.setFileLayout(layout);
```
