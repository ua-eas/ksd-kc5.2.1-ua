package edu.arizona.kra.budget.web.struts.action;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.BusinessObjectBase;
/**
 * 
 */

@SuppressWarnings("serial")
public class SpecialDateRange extends BusinessObjectBase {
    
    private Date start;
    private Date end;
    public enum Appointment {ACADEMIC, SUMMER }
    private Appointment appointment;
    
    public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

	public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
    
    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String,Object>();
        map.put("start", getStart());
        map.put("end", getEnd());
        map.put("appointment", getAppointment());
        return map;
    }

    public void refresh() {
        // do nothing
    }
}
