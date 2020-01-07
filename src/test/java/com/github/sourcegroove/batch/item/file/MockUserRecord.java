package com.github.sourcegroove.batch.item.file;

import lombok.Data;

import java.time.LocalDate;


@Data
public class MockUserRecord {
    private String recordType;
    private String username;
    private String lastName;
    private String firstName;
    private LocalDate dateOfBirth;
}
