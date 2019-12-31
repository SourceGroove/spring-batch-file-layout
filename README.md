# spring-batch-file-layout

Layout driven item readers and writers

# Declarative file layouts can be used to create ItemReaders and ItemWriters

## Fixed width files
```java
 FileLayout layout = new FixedWidthFileLayout()
        .linesToSkip(1)
        .record(MockUserRecord.class)
            .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
            .prefix("user*")
            .column("username", 1, 10)
            .column("firstName", 11, 20)
            .column("lastName", 21, 30)
            .column("dateOfBirth", 31, 38)
        .record(MockRoleRecord.class)
            .prefix("role*")
            .column("roleKey", 1, 8)
            .column("role", 9, 20);

FileLayoutItemReader<MockUserRecord> reader = new FileLayoutItemReader();
reader.setFileLayout(layout);

FileLayoutItemWriter<MockUserRecord> writer = new FileLayoutItemWriter<>();
writer.setFileLayout(layout);

```
## Delimited files
```java
FileLayout layout = new DelimitedFileLayout()
        .linesToSkip(1)
        .record(MockUserRecord.class)
            .editor(LocalDate.class, new LocalDateEditor("yyyyMMdd"))
            .column("username")
            .column("firstName")
            .column("lastName")
            .column("dateOfBirth");

FileLayoutItemReader<MockUserRecord> reader = new FileLayoutItemReader();
reader.setFileLayout(layout);

FileLayoutItemWriter<MockUserRecord> writer = new FileLayoutItemWriter<>();
writer.setFileLayout(layout);
```
