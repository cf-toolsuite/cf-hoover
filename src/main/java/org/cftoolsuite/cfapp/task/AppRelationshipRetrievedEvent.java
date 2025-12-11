package org.cftoolsuite.cfapp.task;

import java.io.Serial;
import java.util.List;

import org.cftoolsuite.cfapp.domain.AppRelationship;
import org.springframework.context.ApplicationEvent;

public class AppRelationshipRetrievedEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<AppRelationship> relations;

    public AppRelationshipRetrievedEvent(Object source) {
        super(source);
    }

    public AppRelationshipRetrievedEvent relations(List<AppRelationship> relations) {
        this.relations = relations;
        return this;
    }

    public List<AppRelationship> getRelations() {
        return relations;
    }

}
