package edu.arizona.kra.subaward.batch.bo;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

import java.sql.Date;

/**
 * Created by nataliac on 11/26/18.
 */
public class UASubawardInvoiceFeedSummary extends KraPersistableBusinessObjectBase {
    static final long serialVersionUID = -24983129882357439L;

    private Long executionId;

    private Date jobStartTime;
    private Date jobEndTime;
    private long jobExecutionTime;
    private String jobStatus;

    private Date importIntervalStartDate;
    private Date importIntervalEndDate;
    private int importIntervalNbrOfDays;

    private int glEntriesImportCount;
    private int invoiceDataCount;
    private int subawardInvoicesCount;
    private int errorCount;



    public Long getExecutionId() {
        return executionId;
    }

    public UASubawardInvoiceFeedSummary setExecutionId(Long id) {
        this.executionId = id;
        return this;
    }

    public Date getJobStartTime() {
        return jobStartTime;
    }

    public UASubawardInvoiceFeedSummary setJobStartTime(Date jobStartTime) {
        this.jobStartTime = jobStartTime;
        return this;
    }

    public Date getJobEndTime() {
        return jobEndTime;
    }

    public UASubawardInvoiceFeedSummary setJobEndTime(Date jobEndTime) {
        this.jobEndTime = jobEndTime;
        return this;
    }

    public long getJobExecutionTime() {
        return jobExecutionTime;
    }

    public UASubawardInvoiceFeedSummary setJobExecutionTime(long jobExecutionTime) {
        this.jobExecutionTime = jobExecutionTime;
        return this;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public UASubawardInvoiceFeedSummary setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }

    public Date getImportIntervalStartDate() {
        return importIntervalStartDate;
    }

    public UASubawardInvoiceFeedSummary setImportIntervalStartDate(Date importIntervalStartDate) {
        this.importIntervalStartDate = importIntervalStartDate;
        return this;
    }

    public Date getImportIntervalEndDate() {
        return importIntervalEndDate;
    }

    public UASubawardInvoiceFeedSummary setImportIntervalEndDate(Date importIntervalEndDate) {
        this.importIntervalEndDate = importIntervalEndDate;
        return this;
    }

    public int getImportIntervalNbrOfDays() {
        return importIntervalNbrOfDays;
    }

    public UASubawardInvoiceFeedSummary setImportIntervalNbrOfDays(int importIntervalNbrOfDays) {
        this.importIntervalNbrOfDays = importIntervalNbrOfDays;
        return this;
    }

    public int getGlEntriesImportCount() {
        return glEntriesImportCount;
    }

    public UASubawardInvoiceFeedSummary setGlEntriesImportCount(int glEntriesImportCount) {
        this.glEntriesImportCount = glEntriesImportCount;
        return this;
    }

    public int getInvoiceDataCount() {
        return invoiceDataCount;
    }

    public UASubawardInvoiceFeedSummary setInvoiceDataCount(int invoiceDataCount) {
        this.invoiceDataCount = invoiceDataCount;
        return this;
    }

    public int getSubawardInvoicesCount() {
        return subawardInvoicesCount;
    }

    public UASubawardInvoiceFeedSummary setSubawardInvoicesCount(int subawardInvoicesCount) {
        this.subawardInvoicesCount = subawardInvoicesCount;
        return this;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public UASubawardInvoiceFeedSummary setErrorCount(int errorCount) {
        this.errorCount = errorCount;
        return this;
    }
}
