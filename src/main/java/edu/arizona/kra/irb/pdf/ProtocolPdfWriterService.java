package edu.arizona.kra.irb.pdf;

import org.kuali.rice.krad.UserSession;

public interface ProtocolPdfWriterService {
    boolean generateActiveProtocolPdfsToDisk(UserSession userSession);
}
