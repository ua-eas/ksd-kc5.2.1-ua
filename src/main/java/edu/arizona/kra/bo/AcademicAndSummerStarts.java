package edu.arizona.kra.bo;

import java.util.LinkedHashMap;
import java.sql.Date;

import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

/**
 * Represents the Academic year start and Summer year start for a particular
 * School year. School year is the primary key and is expected to be unique
 * in the table. This object/table is used in Budget->Personnel tab to 
 * provide correct Start Date and End date for budget personnel that have 
 * Academic (9 months) or Summer (3 months) appointments
 * 
 * @author vdixit
 * 
 */
public class AcademicAndSummerStarts extends KraPersistableBusinessObjectBase {
    
    private Integer schoolYear;
    private Date academicPeriodStart;
    private Date summerPeriodStart;

   
    public AcademicAndSummerStarts() {
        super();
    }
    

    public Integer getSchoolYear() {
		return schoolYear;
	}


	public void setSchoolYear(Integer schoolYear) {
		this.schoolYear = schoolYear;
	}


	public Date getAcademicPeriodStart() {
		return academicPeriodStart;
	}


	public void setAcademicPeriodStart(Date academicPeriodStart) {
		this.academicPeriodStart = academicPeriodStart;
	}


	public Date getSummerPeriodStart() {
		return summerPeriodStart;
	}


	public void setSummerPeriodStart(Date summerPeriodStart) {
		this.summerPeriodStart = summerPeriodStart;
	}


    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("School Year", this.schoolYear);
        map.put("Academic Period Start", this.academicPeriodStart);
        map.put("Summer Period Start", this.summerPeriodStart);
        map.put("update user", this.getUpdateUser());
        map.put("update date", this.getUpdateTimestamp());
        map.put("objectid", this.getObjectId());
        map.put("versionNumber", this.getVersionNumber());
        return map;
    }
}