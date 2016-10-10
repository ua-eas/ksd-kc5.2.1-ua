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
package org.kuali.kra.subaward.lookup;

import org.kuali.kra.lookup.KraLookupableHelperServiceImpl;
import org.kuali.kra.subaward.bo.SubAward;
import org.kuali.kra.subaward.dao.SubAwardLookupDao;
import org.kuali.kra.subaward.document.SubAwardDocument;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;



public class SubAwardLookupableHelperServiceImpl extends KraLookupableHelperServiceImpl {

    private static final long serialVersionUID = -985414963870962388L;

    private SubAwardLookupDao subAwardLookupDao;


    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        super.setBackLocationDocFormKey(fieldValues);

        try {
            return (List<SubAward>)subAwardLookupDao.findSubAwards(fieldValues);
        } catch (Exception e){
            LOG.error(e);
            throw new RuntimeException(e);
        }

    }


    /**
     * .
     * this method for getCustomActionUrls
     *
     * @param businessObject
     * @param pkNames
     */
    @Override
    public List<HtmlData> getCustomActionUrls(
            BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList =
                super.getCustomActionUrls(businessObject, pkNames);
        htmlDataList.add(getOpenLink((SubAward) businessObject, false));
        htmlDataList.add(getMedusaLink((SubAward) businessObject, false));
        return htmlDataList;
    }

    /**
     * .
     * This method is for getOpenLink
     *
     * @param subaward
     * @param viewOnly
     * @return htmlData
     */
    protected AnchorHtmlData getOpenLink(SubAward subAward, Boolean viewOnly) {
        SubAwardDocument subAwardDocument = subAward.getSubAwardDocument();
        AnchorHtmlData htmlData = new AnchorHtmlData();
        htmlData.setDisplayText("open");
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER,
                KRADConstants.DOC_HANDLER_METHOD);
        parameters.put(KRADConstants.PARAMETER_COMMAND,
                KewApiConstants.DOCSEARCH_COMMAND);
        parameters.put(KRADConstants.DOCUMENT_TYPE_NAME, getDocumentTypeName());
        parameters.put("viewDocument", viewOnly.toString());
        parameters.put("docOpenedFromAwardSearch", "true");
        parameters.put("docId", subAwardDocument.getDocumentNumber());
        parameters.put("placeHolderAwardId",
                subAward.getSubAwardId().toString());
        String href = UrlFactory.parameterizeUrl(
                "../" + getHtmlAction(), parameters);
        htmlData.setHref(href);
        return htmlData;
    }

    /**
     * .
     * This Method is for getMedusaLink
     *
     * @param subaward
     * @param readOnly
     * @return htmlData
     */
    protected AnchorHtmlData getMedusaLink(
            SubAward subAward, Boolean readOnly) {
        AnchorHtmlData htmlData = new AnchorHtmlData();
        htmlData.setDisplayText(MEDUSA);
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "medusa");
        parameters.put(KRADConstants.PARAMETER_COMMAND,
                KewApiConstants.DOCSEARCH_COMMAND);
        parameters.put(KRADConstants.DOCUMENT_TYPE_NAME, getDocumentTypeName());
        parameters.put("viewDocument", readOnly.toString());
        parameters.put("docId", subAward.
                getSubAwardDocument().getDocumentNumber());
        parameters.put("docOpenedFromAwardSearch", "true");
        parameters.put("placeHolderAwardId",
                subAward.getSubAwardId().toString());
        String href = UrlFactory.parameterizeUrl(
                "../" + getHtmlAction(), parameters);
        htmlData.setHref(href);
        return htmlData;
    }

    /**
     * .
     * This override is reset field definitions
     *
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getRows()
     */
    @Override
    public List<Row> getRows() {
        List<Row> rows = super.getRows();
        for (Row row : rows) {
            for (Field field : row.getFields()) {

                if (field.getPropertyName().equals("startDate") || field.getPropertyName().equals("endDate")
                        || field.getPropertyName().equals("closeoutDate")) {
                    field.setDatePicker(true);
                }
                if (field.getPropertyName().equals("requisitionerUserName")) {
                    field.setFieldConversions("principalName:requisitionerUserName,principalId:requisitionerId");
                }
            }
        }
        return rows;
    }

    @Override
    protected void addEditHtmlData(List<HtmlData> htmlDataList, BusinessObject businessObject) {
        //no-op
    }


    @Override
    protected String getHtmlAction() {
        return "subAwardHome.do";
    }

    @Override
    protected String getDocumentTypeName() {
        return "SubAwardDocument";
    }

    @Override
    protected String getKeyFieldName() {
        return "SubAwardId";
    }


    public void setSubAwardLookupDao( SubAwardLookupDao subAwardLookupDao){
        this.subAwardLookupDao = subAwardLookupDao;
    }

}
