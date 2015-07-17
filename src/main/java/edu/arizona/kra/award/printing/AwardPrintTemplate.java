package edu.arizona.kra.award.printing;

import org.apache.struts.upload.FormFile;
import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

public abstract class AwardPrintTemplate extends KraPersistableBusinessObjectBase {

	private static final long serialVersionUID = 1L;

    private Integer awarPrintTemplId;

    private String awardPrintTypeCode;

    private String fileName;

    private byte[] printTemplate;

    private transient FormFile templateFile;

    public AwardPrintTemplate() {
        super();
    }

    public Integer getAwardPrintTemplId() {
        return awarPrintTemplId;
    }

    public void setAwardPrintTemplId(Integer awarPrintTemplId) {
        this.awarPrintTemplId = awarPrintTemplId;
    }

    public String getAwardPrintypeCode() {
        return awardPrintTypeCode;
    }

    public void setAwardPrintTypeCode(String awardPrintTypeCode) {
        this.awardPrintTypeCode = awardPrintTypeCode;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getPrintTemplate() {
        return printTemplate;
    }

    public void setPrintTemplate(byte[] printTemplate) {
        this.printTemplate = printTemplate;
    }

    public void setTemplateFile(FormFile templateFile) {
        this.templateFile = templateFile;
    }

    public FormFile getTemplateFile() {
        return templateFile;
    }
}
