package io.pivotal.cfapp.report;

import io.pivotal.cfapp.domain.AppRelationship;
import io.pivotal.cfapp.task.AppRelationshipRetrievedEvent;

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