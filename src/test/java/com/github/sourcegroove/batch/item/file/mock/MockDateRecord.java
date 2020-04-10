package com.github.sourcegroove.batch.item.file.mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class MockDateRecord {
    private String type;
    private LocalDate year;
    private LocalDate month;
    private LocalDate day;
    private Date dateField;
    private LocalDate localDateField;
    private LocalDateTime localDateTimeField;
    
    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type = type;
    }
    public LocalDate getYear() {
        return year;
    }

    public void setYear(LocalDate year) {
        this.year = year;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public LocalDate getDay() {
        return day;
    }

    public void setDay(LocalDate day) {
        this.day = day;
    }

    public Date getDateField() {
        return dateField;
    }

    public void setDateField(Date dateField) {
        this.dateField = dateField;
    }

    public LocalDate getLocalDateField() {
        return localDateField;
    }

    public void setLocalDateField(LocalDate localDateField) {
        this.localDateField = localDateField;
    }

    public LocalDateTime getLocalDateTimeField() {
        return localDateTimeField;
    }

    public void setLocalDateTimeField(LocalDateTime localDateTimeField) {
        this.localDateTimeField = localDateTimeField;
    }
}
