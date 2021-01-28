package edu.arizona.kra.irb.pdf;

import org.kuali.rice.krad.UserSession;

public interface ProtocolPdfWriterService {
    ProtocolPdfJobInfo generateActiveProtocolPdfsToDisk(UserSession userSession);
}
