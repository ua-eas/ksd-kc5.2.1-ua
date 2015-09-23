package org.kuali.kra.subaward.web;

import java.io.Serializable;

import org.kuali.kra.award.home.Award;
import org.kuali.kra.subaward.bo.SubAward;

public class SubAwardFundingSourceBean implements Serializable {
    private static final long serialVersionUID = -8203046483314128290L;
    
    private String subAwardFundingSourceId;
    private SubAward subaward;
    private Award award;
    private boolean deleted = false;

    public SubAwardFundingSourceBean() {
        super();
    }
    
    public SubAwardFundingSourceBean(String subAwardFundingSourceId, SubAward subaward, Award award) {
        super();
        this.subAwardFundingSourceId = subAwardFundingSourceId;
        this.subaward = subaward;
        this.award = award;
    }
    
    public String getSubAwardFundingSourceId() {
        return subAwardFundingSourceId;
    }
    public void setSubAwardFundingSourceId(String subAwardFundingSourceId) {
        this.subAwardFundingSourceId = subAwardFundingSourceId;
    }
    public SubAward getSubaward() {
        return subaward;
    }
    public void setSubaward(SubAward subaward) {
        this.subaward = subaward;
    }
    public Award getAward() {
        return award;
    }
    public void setAward(Award award) {
        this.award = award;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
