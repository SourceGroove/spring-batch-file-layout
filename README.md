# Spring Batch File Layouts

* Note - don't use anything prior to v 1.3.0
 
## Overview
Spring Batch File Layouts aim to simplify the construction of File ItemReader/Writers for fixed width, delimited and Excel files.

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


# Key Classes
* **FixedWidthFileLayout** - used to construct a fixed width file layout that can return configured FileLayoutItemReader/Writers 

* **DelimitedFileLayout** - used to construct a delimited file layout that can return configured FileLayoutItemReader/Writers 

* **ExcelFileLayout** - used to construct an Excel file layout that can return configured FileLayoutItemReader/Writers

* **FileLayoutItemReader** - an interface that extends ResourceAwareItemReaderItemStream<T> and InitializingBean - used to enforce 
                        FileLayout implementations to return appropriate reader implementations. 

* **FileLayoutItemWriter** - an interface that extends ResourceAwareItemReaderWriterStream<T> and InitializingBean - used to enforce 
                        FileLayout implementations to return appropriate writer implementations.
 
# Notes
- For fixed width files, use start/end (ranges) over width  where possible (widths are sketchy when you start incorporating filler and custom formats.. )
- The Excel item writer isn't implemented yet
- PropertyEditors with Format.xxx values - be careful mixing the two because you can run into issues.  Consider the below layout, and using the 
    layout.getItemWriter().  The editor will convert the dateOfBirth to a string as 'YYYYMMDD' and then the Format.YYYYMMDD will  try and treat
    the dateOfBirth value as a date and do 'String.print("%tY%<tm%<td",  dateOfBirthValue)'.  This will throw an exception because the dateOfBirthValue 
    is a String not a date object.
```
FileLayout layout = new FixedWidthFileLayout()
    .record(MockUserRecord.class)
        .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
        .column("recordType", 1, 4)
        .column("username", 5, 10)
        .column("firstName", 11, 20)
        .column("lastName", 21, 30)
        .column("dateOfBirth", 31, 38, Format.YYYYMMDD)
    .build();
```
Because of this, the library will allow you to add editors in different ways:
 - Globally to all record types in the layout
 - Globally to all readers for all record types in the layout
 - Globally to all writers for all record types in the layout
 - At the record level for all readers and writers
 - At the record level for readers only
 - At the record level for writers only

Any editor added at the record level for the same object type defined at the global level will be overwritten for that record.  i.e:
```java
FileLayout layout = new FixedWidthFileLayout()
    .readEditor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
    .record(MockUserRecord.class, "USER")
        .readEditor(LocalDate.class, new LocalDateEditor("MM/dd/yyyy"))
        .column("dateOfBirth", 1, 7)
    .record(MockRoleRecord.class, "Role")
        .column("effectiveDate", 1, 7)
    .build();
``` 
In  this case, the MockUserRecord#dateOfBirth will use 'MM/dd/yyyy' and the MockRoleRecord#effectiveDate will use yyyyMMdd for reading only  

# Usage

## Maven Dependency
```
<dependency>
  <groupId>com.github.sourcegroove</groupId>
  <artifactId>spring-batch-file-layout</artifactId>
  <version>1.3.10</version>
</dependency>
```

## Gradle Dependency
```
implementation 'com.github.sourcegroove:spring-batch-file-layout:1.3.2'
```
# Declarative file layouts are used to create ItemReaders and ItemWriters
All you need to do is define your delimited, fixed width or excel file layout and ask it for an item reader or wrier.  By default, the
implementations uses Spring's BeanWrapperFieldSetMapper & BeanWrapperFieldExtractor to map your POJO's properties to either column 'Ranges' 
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

LayoutItemReader<MockUserRecord> reader = layout.getItemReader();
LayoutItemWriter<MockUserRecord> writer = layout.getItemWriter();
```

# File Layouts
There are three implementations of the FileLayout interface - FixedWidthFileLayout, DelimitedFileLayout & ExcelFileLayout.  
Each of these contains a collection of 'record (or sheet) layouts' that define the records in the file.

Defining layouts in Java is simple, but they can also be dynamically defined at runtime using persisted data

## Fixed Width Layouts
Fixed width file layouts define columns by the position they appear in the file.  If the positions defined in the
layout leave a 'gap' in between defined columns, filler (empty spaces) will be added to the line to fill the gap.

You can customize the way columns are serialized to text when being written by specifying a FixedWidthFormatBuilder.Format 
value in your column definition. 

i.e.
```java
    ...
    .column("dateOfBirth", 31, 38, FixedWidthFormatBuilder.Format.YYYYMM) // will format the date to YYYYMM
    ...
```

Current format options:

Enum Value  | Example | Description
------------- | ------------- | -------------
STRING  | "TEXT______" | left aligned text 
INTEGER | "0000000123" | left padded with 0's
ZD | "123_______" | left aligned number
DECIMAL | "000000123.5" | right aligned decimal with 2 digit decimal
YYYYMMDD | "20190930" | date formatted to YYYYMMDD
YYYYMM | "201909" | date formatted to YYYYMM
YYYY | "2019" | date formatted to YYYY
CONSTANT | "__________" | filler space (mostly used internally to fill gaps between columns

### Fixed width file layout simple
```java
FileLayout layout = new FixedWidthFileLayout()
    .record(MockUserRecord.class)
        .column("recordType", 1, 4)
        .column("username", 5, 10)
        .column("firstName", 11, 20)
        .column("lastName", 21, 30)
        .column("dateOfBirth", 31, 38)
    .build();

LayoutItemReader<MockUserRecord> reader = layout.getItemReader();
LayoutItemWriter<MockUserRecord> writer = layout.getItemWriter();
```

### Fixed width file layout with custom property editors, multiple record types and custom column formats
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
        .column("dateOfBirth", 31, 38, FixedWidthFormatBuilder.Format.YYYYMM)
    .record(MockRoleRecord.class)
        .prefix("ROLE*")
        .column("recordType", 1, 4)
        .column("roleKey", 5, 8)
        .column("role", 9, 20)
    .build();

LayoutItemReader reader = layout.getItemReader();
LayoutItemWriter writer = layout.getItemWriter();
```

## Delimited Layouts
Delimited layouts need to define the columns in the order they appear in the file. 
The delimiter and qualifier can be defined.

### Delimited file layout
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

LayoutItemReader<MockUserRecord> reader = layout.getItemReader();
LayoutItemWriter<MockUserRecord> writer = layout.getItemWriter();
```

### Delimited file layout with custom qualifier and delimiter
```java
FileLayout layout = new DelimitedFileLayout()
        .linesToSkip(1)
        .qualifier('~')
        .delimiter("|")
        .record(MockUserRecord.class)
            .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
            .column("username")
            .column("firstName")
            .column("lastName")
            .column("dateOfBirth")
        .build();

LayoutItemReader<MockUserRecord> reader = layout.getItemReader();
LayoutItemWriter<MockUserRecord> writer = layout.getItemWriter();
```

## Excel Layouts
By default, the excel reader will read all sheets in the workbook and use the StreamingExcelItemReader implementation.  There is 
a SimpleExcelItemReader which loads the entire workbook into memory, but I can't think of a valid reason to use this over the 
streaming implementation at this point.

### Excel file layout simple
```java
Layout layout = new ExcelLayout()
                .linesToSkip(1)
                .sheet(MockUserRecord.class)
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .editor(LocalDate.class, new LocalDateEditor())
                .layout();

LayoutItemReader<MockUserRecord> reader = layout.getItemReader();
LayoutItemWriter<MockUserRecord> writer = layout.getItemWriter();
```
### Excel file layout one sheet
```java
Layout layout = new ExcelLayout()
                .linesToSkip(1)
                .sheet(MockUserRecord.class)
                .sheetIndex(1) // reads the second sheet in the workbook
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .editor(LocalDate.class, new LocalDateEditor())
                .layout();

LayoutItemReader<MockUserRecord> reader = layout.getItemReader();
LayoutItemWriter<MockUserRecord> writer = layout.getItemWriter();
```

### Excel file layout one sheet
```java
Layout layout = new ExcelLayout()
                .linesToSkip(1)
                .sheet(MockUserRecord.class)
                .sheetIndex(1) // reads the second sheet in the workbook
                .column("username")
                .column("firstName")
                .column("lastName")
                .column("dateOfBirth")
                .editor(LocalDate.class, new LocalDateEditor())
                .layout();

LayoutItemReader<MockUserRecord> reader = layout.getItemReader();
LayoutItemWriter<MockUserRecord> writer = layout.getItemWriter();
```
