package edu.arizona.kra.institutionalproposal.negotiationlog.service;

import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * Static methods used in Negotiation Log Migration
 */ 
public final class NegotiationMigrationUtils {
    private NegotiationMigrationUtils() {}
   
    /**
    * isNegotiationMigrationEnabled - Find if Negotiation Migration Parameter is set/enabled
    * @return Boolean
    */
   @SuppressWarnings("unchecked")
   public static java.lang.Boolean isNegotiationMigrationEnabled() {
       ParameterService paramService = CoreFrameworkServiceLocator.getParameterService();
       if ( paramService.parameterExists("KC-NEGOTIATION", "Negotiation", "NEGOTIATION_MIGRATION_ENABLED")){
           return CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean("KC-NEGOTIATION", "Negotiation", "NEGOTIATION_MIGRATION_ENABLED");
       }
       return false;
   }
   
}
