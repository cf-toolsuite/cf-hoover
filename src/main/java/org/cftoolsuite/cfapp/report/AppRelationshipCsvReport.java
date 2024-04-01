package org.cftoolsuite.cfapp.report;

import org.cftoolsuite.cfapp.domain.AppRelationship;
import org.cftoolsuite.cfapp.task.AppRelationshipRetrievedEvent;

public class AppRelationshipCsvReport  {

    public String generateDetail(AppRelationshipRetrievedEvent event) {
        StringBuffer details = new StringBuffer();
        details.append("\n");
        details.append(AppRelationship.headers());
        details.append("\n");
        event.getRelations()
                .forEach(a -> {
                    details.append(a.toCsv());
                    details.append("\n");
                });
        return details.toString();
    }
}