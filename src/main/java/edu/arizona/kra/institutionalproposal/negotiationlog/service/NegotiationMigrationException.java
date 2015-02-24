package edu.arizona.kra.institutionalproposal.negotiationlog.service;

import org.kuali.rice.core.api.exception.KualiException;

public class NegotiationMigrationException extends  KualiException {

    private static final long serialVersionUID = 963934453347063551L;

    public NegotiationMigrationException(String message) {
        super(message);
    }
}
