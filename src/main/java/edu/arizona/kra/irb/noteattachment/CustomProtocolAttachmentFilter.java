/*
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.arizona.kra.irb.noteattachment;

import java.util.Comparator;

import org.kuali.kra.protocol.noteattachment.ProtocolAttachmentProtocolBase;

import edu.arizona.kra.irb.ProtocolUtils;

public class CustomProtocolAttachmentFilter extends org.kuali.kra.irb.noteattachment.ProtocolAttachmentFilter {

    private static final long serialVersionUID = -8420432117877485410L;

    /**
     * This method returns a comparator used to sort protocol attachments
     * @return a comparator used to sort protocol attachments
     */
    @Override
    public Comparator<ProtocolAttachmentProtocolBase> getProtocolAttachmentComparator() {
        if ("ARNO".equalsIgnoreCase(getSortBy())) { 
            return new ProtocolAmendRenewNumberComparator();
        }  else if ("LAUP".equalsIgnoreCase(getSortBy())) {
            return new ProtocolAttachmentLastUpdatedComparator();
        }
        else
            return super.getProtocolAttachmentComparator();
    }   
    
    
    /**
     *  Implements the ARNO - Amendment/Renewal comparator for the default ARNO sorting.
     *  Primary sort by Amend/Renewal Number, secondary sort by Date/Timestamp, tertiary sort by Attachment Type.
     */
    private class ProtocolAmendRenewNumberComparator implements Comparator<ProtocolAttachmentProtocolBase>
    {
        @Override
        public int compare(ProtocolAttachmentProtocolBase o1, ProtocolAttachmentProtocolBase o2) {
            if ( o1 instanceof CustomProtocolAttachmentProtocol && o2 instanceof CustomProtocolAttachmentProtocol){
                String sparn0 = ((CustomProtocolAttachmentProtocol) o1).getSourceProtocolAmendRenewalNumber();
                String sparn1 = ((CustomProtocolAttachmentProtocol) o2).getSourceProtocolAmendRenewalNumber();
                
                if( sparn0.equalsIgnoreCase(sparn1) ){
                    return ProtocolUtils.compareTimestamps(o1.getUpdateTimestamp(), o2.getUpdateTimestamp());
                }                   
                else {
                    return sparn0.compareTo(sparn1);
                }
            }
            else { 
                return ProtocolUtils.compareTimestamps(o1.getUpdateTimestamp(), o2.getUpdateTimestamp());
            }

        }

    }
    
    
    private class ProtocolAttachmentLastUpdatedComparator implements Comparator<ProtocolAttachmentProtocolBase>
    {
        @Override
        public int compare(ProtocolAttachmentProtocolBase o1, ProtocolAttachmentProtocolBase o2) {
            return ProtocolUtils.compareTimestamps(o1.getUpdateTimestamp(), o2.getUpdateTimestamp());
                
        }
        
    }
   
}
