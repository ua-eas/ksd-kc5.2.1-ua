package edu.colostate.kc.hierarchyrouting;

import static org.kuali.kra.infrastructure.KraServiceLocator.getService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.kuali.kra.bo.Unit;
import org.kuali.kra.budget.core.Budget;
import org.kuali.kra.budget.distributionincome.BudgetCostShare;
import org.kuali.kra.budget.versions.BudgetDocumentVersion;
import org.kuali.kra.hierarchyrouting.UnitStop;
import org.kuali.kra.proposaldevelopment.document.ProposalDevelopmentDocument;
import org.kuali.kra.service.UnitService;
import org.kuali.rice.core.api.util.xml.XmlHelper;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.NodeState;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeConfigParam;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.engine.node.hierarchyrouting.HierarchyProvider;
import org.kuali.rice.kew.rule.NamedRuleSelector;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.krad.service.BusinessObjectService;

public class UnitHierarchyProvider implements HierarchyProvider {

	   private static final Log LOG = LogFactory.getLog(UnitHierarchyProvider.class);
	   private static final String UNIT_NUMBER_ELEMENT_NAME = "unitNumber";
	   private static final String DOC_NUMBER_ELEMENT_NAME = "documentNumber";
	   private static final String DOC_NUMBER_DB_COLUMN_NAME = "document_number";
	   private static final String PROPOSAL_PERSON_UNIT_CLASS_NAME = "org.kuali.kra.proposaldevelopment.bo.ProposalPersonUnit";
	    

	    private UnitService unitService;
	    private List<String> unitNumbers;
	    
	    /*KCI-1122 starts */
	    private String leadUnit;
	    /*KCI-1122 ends */
	    /**
	     * The root stop
	     */
	    private UnitStop root;
	    /**
	     * Map of Stop id-to-Stop instance
	     */
	    private Map<String, UnitStop> stops = new HashMap<String, UnitStop>();

	    public void configureRequestNode(RouteNodeInstance hiearchyNodeInstance, RouteNode node) {
	        Map<String, String> cfgMap = Utilities.getKeyValueCollectionAsMap(node.getConfigParams());
	        // propagate rule selector and name from hierarchy node
	        if (!cfgMap.containsKey(RouteNode.RULE_SELECTOR_CFG_KEY)) {
	            Map<String, String> hierarchyCfgMap = Utilities.getKeyValueCollectionAsMap(hiearchyNodeInstance.getRouteNode()
	                    .getConfigParams());
	            node.getConfigParams().add(
	                    new RouteNodeConfigParam(node, RouteNode.RULE_SELECTOR_CFG_KEY, hierarchyCfgMap
	                            .get(RouteNode.RULE_SELECTOR_CFG_KEY)));
	        }
	        if (!cfgMap.containsKey(NamedRuleSelector.RULE_NAME_CFG_KEY)) {
	            Map<String, String> hierarchyCfgMap = Utilities.getKeyValueCollectionAsMap(hiearchyNodeInstance.getRouteNode()
	                    .getConfigParams());
	            node.getConfigParams().add(
	                    new RouteNodeConfigParam(node, NamedRuleSelector.RULE_NAME_CFG_KEY, hierarchyCfgMap
	                            .get(NamedRuleSelector.RULE_NAME_CFG_KEY)));
	        }
	    }


	    public boolean equals(Stop a, Stop b) {
	        return ObjectUtils.equals(a, b);
	    }

	    public List<Stop> getLeafStops(RouteContext context) {
	        List<Stop> leafStops = new ArrayList<Stop>();
	        Stop leadUnitStop = null;
	        for (UnitStop stop : stops.values()) {
	            if (stop.children.size() == 0) {
	            	/*KCI-1122 starts */
            		if(stop.id.equals(leadUnit))
            		{
            			leadUnitStop = stop;
            			continue;
            		}
            		/*KCI-1122 ends */
	                leafStops.add(stop);
	            }
	        }
	        /*KCI-1122 starts */
            if(leadUnitStop != null) {
                leafStops.add(leadUnitStop);
            }
	        /*KCI-1122 ends */
	        return leafStops;
	    }

	    public Stop getParent(Stop stop) {
	        return ((UnitStop) stop).parent;
	    }

	    public boolean isRoot(Stop stop) {
	        return equals(stop, root);
	    }

	    public Stop getStop(RouteNodeInstance nodeInstance) {
	        NodeState state = nodeInstance.getNodeState("id");
	        if (state == null) {
	            // return null;
	            throw new RuntimeException();
	        }
	        else {
	        	if(LOG.isDebugEnabled()) {
	        		LOG.debug("id Node state on nodeinstance " + nodeInstance + ": " + state);
	        	}
	            return stops.get(state.getValue());
	        }
	    }

	    public Stop getStopByIdentifier(String stopId) {
	        return stops.get(stopId);
	    }

	    public String getStopIdentifier(Stop stop) {
	        return ((UnitStop) stop).id;
	    }

	    public boolean hasStop(RouteNodeInstance nodeInstance) {
	        return nodeInstance.getNodeState("id") != null;
	    }
	    
	    private List<String> retrieveProposalUnitNumbers(RouteContext context) {
            Document document = XmlHelper.buildJDocument(context.getDocumentContent().getDocument());
            List<String> proposalUnits = new ArrayList<String>();
            Collection<Element> proposalPersonUnitElements = XmlHelper.findElements(document.getRootElement(), PROPOSAL_PERSON_UNIT_CLASS_NAME);
            if (proposalPersonUnitElements.size() > 0) {
                for (Element proposalPersonUnitElement : proposalPersonUnitElements) {
                    if (proposalPersonUnitElement != null) {
                        String unitNumber = proposalPersonUnitElement.getChildText(UNIT_NUMBER_ELEMENT_NAME);
                        if (!proposalUnits.contains(unitNumber)) {
                            proposalUnits.add(unitNumber);
                        }
                    }
                }
            }
            
	    	/*KCI-703 starts */
            //Adding Cost share units to workflow
	    	String documentNumber = context.getDocument().getDocumentId().toString();
	        Map<String, Object> fieldValues = new HashMap<String, Object>();
	        fieldValues.put(DOC_NUMBER_ELEMENT_NAME, documentNumber);
	        BusinessObjectService businessObjectService = getService(BusinessObjectService.class);
	        List<ProposalDevelopmentDocument> proposalDocuments = (List<ProposalDevelopmentDocument>) businessObjectService.findMatching(ProposalDevelopmentDocument.class, fieldValues);

	        if (proposalDocuments.size() > 1) {
	            throw new RuntimeException("More than one proposal retieved for document number: " + documentNumber);
	        }
	        ProposalDevelopmentDocument proposalDocument = proposalDocuments.get(0);
	    	List<BudgetDocumentVersion> budgetDocuments = proposalDocument.getBudgetDocumentVersions();
	        if (budgetDocuments != null && budgetDocuments.size() > 0) {            
	            for(BudgetDocumentVersion budgetDocument : budgetDocuments) {
	            	fieldValues.clear();
	                fieldValues.put(DOC_NUMBER_DB_COLUMN_NAME, budgetDocument.getDocumentNumber());
	                List<Budget> budgets = (List<Budget>) businessObjectService.findMatching(Budget.class, fieldValues);
	                Budget budget = budgets.get(0);
	                
	                // only collect units for final version
	                if (budget.getFinalVersionFlag()) {
	                    List<BudgetCostShare> costShares = budget.getBudgetCostShares();
	                    if (costShares != null && costShares.size() > 0) {
	                        for (BudgetCostShare costShare : costShares) {
	                        	//If there is a third party cost share unit, it's unit administrators need to be added
                                proposalUnits.add(costShare.getSourceUnit());
	                        }
	                    }
	                    break;
	                }
	            }                         
	        }       
	    	/*KCI-703 ends */
	        
	        /*KCI-1122 starts */
	        leadUnit = proposalDocument.getDevelopmentProposal().getOwnedByUnitNumber();
	        /*KCI-1122 ends */
	        return proposalUnits;
	    }
	    

	public void init(RouteNodeInstance nodeInstance, RouteContext context) {
		if (unitNumbers == null || unitNumbers.isEmpty()) {
			unitNumbers = retrieveProposalUnitNumbers(context);
		}

		Unit ownedByUnit = null;

		for (String unitNumber : unitNumbers) {

			if (StringUtils.isNotEmpty(unitNumber)) {
				ownedByUnit = getUnitService().getUnit(unitNumber);
				if (ownedByUnit != null) {

					Unit unit = ownedByUnit;

					while (unit.getParentUnit() != null) {
						UnitStop simpleStop = (UnitStop) getStopByIdentifier(unit
								.getUnitNumber());
						if (simpleStop == null) {
							simpleStop = new UnitStop();
							simpleStop.id = unit.getUnitNumber();
							stops.put(simpleStop.id, simpleStop);
						}
						if (StringUtils.isNotBlank(unit.getParentUnitNumber())) {
							UnitStop parent = (UnitStop) getStopByIdentifier(unit
									.getParentUnitNumber());
							if (parent == null) {
								parent = new UnitStop();
								parent.id = unit.getParentUnitNumber();
								parent.children = new ArrayList<UnitStop>();
								stops.put(parent.id, parent);
							}
							parent.children.add(simpleStop);
							simpleStop.parent = parent;
						} else {
							root = simpleStop;
							simpleStop.parent = null;
						}
						unit = unit.getParentUnit();
					}

					if (root == null) {
						root = (UnitStop) getStopByIdentifier(getUnitService()
								.getTopUnit().getUnitNumber());
					}
				}
			}
		}
	}

	    public void setStop(RouteNodeInstance requestNodeInstance, Stop stop) {
	        // TODO : not sure about this one yet ?
	    	if(stop == null) {
	    		return;
	    	}
	        requestNodeInstance.addNodeState(new NodeState("id", getStopIdentifier(stop)));
	    }

	    private UnitService getUnitService() {
	    	if (unitService == null) {
	    		unitService = getService(UnitService.class);
	    	}
	    	return unitService;
	    }
}
