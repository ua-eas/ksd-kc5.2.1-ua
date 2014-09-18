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
package edu.arizona.kra.irb;

import java.sql.Timestamp;

/**
 * Class containing static utility methods used throughout IRB
 */
public final class ProtocolUtils {
    
    /**
     * Utility method to compare timestamps that avoid NPEs when one of the timestamps is null
     * 
     * @param ts1 java.sql.Timestamp
     * @param ts2 java.sql.Timestamp
     * @return If both timestamps are not null, it returns the normal compare result, otherwise 
     * it returns 1 if the first one is not null, or -1 if the second one is not null and 0 if they are both null.
     */
    public static final int compareTimestamps(Timestamp timestamp1, Timestamp timestamp2){
            if (timestamp1 != null){
                if (timestamp2 != null){
                    return timestamp1.compareTo(timestamp2);
                }
                else {
                    return 1; 
                }
            } else {
                if (timestamp2 != null){
                    return -1;
                }
            }
            return 0; //both timestamps are null
    }

}
