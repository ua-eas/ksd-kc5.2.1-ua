/*
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.kra.irb.noteattachment;

import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.ProtocolDao;
import org.kuali.kra.irb.personnel.ProtocolPerson;
import org.kuali.kra.protocol.ProtocolBase;
import org.kuali.kra.protocol.noteattachment.*;
import org.kuali.kra.protocol.personnel.ProtocolPersonBase;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kra.protocol.dao.ProtocolAttachmentDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


/** Implementation of {@link ProtocolAttachmentService ProtocolNoteAndAttachmentService}. */
public class ProtocolAttachmentServiceImpl extends ProtocolAttachmentServiceImplBase implements ProtocolAttachmentService {

    private ProtocolAttachmentDao protocolAttachmentDao;

    public ProtocolAttachmentServiceImpl(BusinessObjectService boService, ProtocolDao protocolDao) {
        super(boService, protocolDao);
    }

    /** {@inheritDoc} */
    public Collection<ProtocolAttachmentTypeBase> getTypesForGroup(String code) {
        if (code == null) {
            throw new IllegalArgumentException("the code is null");
        }

        final Collection<ProtocolAttachmentTypeGroup> typeGroups
        = this.boService.findMatching(ProtocolAttachmentTypeGroup.class, Collections.singletonMap("groupCode", code));
        if (typeGroups == null) {
            return new ArrayList<ProtocolAttachmentTypeBase>();
        }

        final Collection<ProtocolAttachmentTypeBase> types = new ArrayList<ProtocolAttachmentTypeBase>();
        for (final ProtocolAttachmentTypeGroup typeGroup : typeGroups) {
            types.add(typeGroup.getType());
        }

        return types;
    }

    @Override
    public Class<? extends ProtocolBase> getProtocolClassHook() {
        return Protocol.class;
    }

    @Override
    public Class<? extends ProtocolAttachmentStatusBase> getProtocolAttachmentStatusClassHook() {
        return ProtocolAttachmentStatus.class;
    }

    @Override
    public Class<? extends ProtocolPersonBase> getProtocolPersonClassHook() {
        return ProtocolPerson.class;
    }

    @Override
    public Class<? extends ProtocolAttachmentTypeBase> getProtocolAttachmentTypeClassHook() {
        return ProtocolAttachmentType.class;
    }

    @Override
    public Class<? extends ProtocolAttachmentTypeGroupBase> getProtocolAttachmentTypeGroupClassHook() {
        return ProtocolAttachmentTypeGroup.class;
    }

    @Override
    public Class<? extends ProtocolAttachmentProtocolBase> getProtocolAttachmentProtocolClassHook() {
        return ProtocolAttachmentProtocol.class;
    }

    @Override
    public Class<? extends ProtocolAttachmentPersonnelBase> getProtocolAttachmentPersonnelClassHook() {
        return ProtocolAttachmentPersonnel.class;
    }

    public void setProtocolAttachmentDao(ProtocolAttachmentDao protocolAttachmentDao) {
        this.protocolAttachmentDao = protocolAttachmentDao;
    }

    protected ProtocolAttachmentDao getProtocolAttachmentDao() {
        return this.protocolAttachmentDao;
    }


    @Override
    public String getSourceProtocolNumber(ProtocolAttachmentBase attachment){
        try {
            return getProtocolAttachmentDao().getSourceProtocolNumber(attachment);
        } catch (Exception e){
            LOG.error(e);
        }
        return null;
    }

    @Override
    public  byte[] getFileContents(Long fileId){
        try {
            return getProtocolAttachmentDao().getAttachmentData(fileId);
        } catch (Exception e){
            LOG.error(e);
        }
        return null;
    }

    @Override
    public  void saveFileContents(Long fileId, byte[] attachmentData){
        //Safeguard against overwriting valid files with empty data
        if (attachmentData==null || attachmentData.length == 0){
            throw new IllegalArgumentException("Cannot save file with empty content!");
        }
        try {
            getProtocolAttachmentDao().saveAttachmentData(fileId, attachmentData);
        } catch (Exception e){
            LOG.error(e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public ArrayList<Long> checkForNullFileData(Long protocolId){
        try{
            return getProtocolAttachmentDao().checkForNullFileData(protocolId);
        } catch (Exception e){
            LOG.error(e);
        }
        return null;

    }
}
