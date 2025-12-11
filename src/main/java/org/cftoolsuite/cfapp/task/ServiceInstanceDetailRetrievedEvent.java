package org.cftoolsuite.cfapp.task;

import java.io.Serial;
import java.util.List;

import org.cftoolsuite.cfapp.domain.ServiceInstanceDetail;
import org.springframework.context.ApplicationEvent;

public class ServiceInstanceDetailRetrievedEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<ServiceInstanceDetail> detail;

    public ServiceInstanceDetailRetrievedEvent(Object source) {
        super(source);
    }

    public ServiceInstanceDetailRetrievedEvent detail(List<ServiceInstanceDetail> detail) {
        this.detail = detail;
        return this;
    }

    public List<ServiceInstanceDetail> getDetail() {
        return detail;
    }


}
