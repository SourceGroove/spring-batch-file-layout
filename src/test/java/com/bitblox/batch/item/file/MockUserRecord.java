package com.bitblox.batch.item.file;

import lombok.Data;

import java.time.LocalDate;


@Data
public class MockUserRecord {
    private String username;
    private String lastName;
    private String firstName;
    private LocalDate dateOfBirth;
}
