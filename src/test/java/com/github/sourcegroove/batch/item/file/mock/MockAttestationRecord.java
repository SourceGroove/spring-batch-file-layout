package com.github.sourcegroove.batch.item.file.mock;

import java.time.LocalDate;

public class MockAttestationRecord {
    private Long id;
    private String hicn;
    private LocalDate reportMonth;
    private String contractNumber;
    private String memberName;
    private String workStatus;
    private String category;
    private String discrepancyType;
    private Double discrepancyAge;
    private LocalDate assignedOn;
    private String assignedTo;
    private String notes;
    private String helperColumn;

    public String getHelperColumn() {
        return helperColumn;
    }

    public void setHelperColumn(String helperColumn) {
        this.helperColumn = helperColumn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHicn() {
        return hicn;
    }

    public void setHicn(String hicn) {
        this.hicn = hicn;
    }

    public LocalDate getReportMonth() {
        return reportMonth;
    }

    public void setReportMonth(LocalDate reportMonth) {
        this.reportMonth = reportMonth;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDiscrepancyType() {
        return discrepancyType;
    }

    public void setDiscrepancyType(String discrepancyType) {
        this.discrepancyType = discrepancyType;
    }

    public Double getDiscrepancyAge() {
        return discrepancyAge;
    }

    public void setDiscrepancyAge(Double discrepancyAge) {
        this.discrepancyAge = discrepancyAge;
    }

    public LocalDate getAssignedOn() {
        return assignedOn;
    }

    public void setAssignedOn(LocalDate assignedOn) {
        this.assignedOn = assignedOn;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
