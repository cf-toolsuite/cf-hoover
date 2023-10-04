package io.pivotal.cfapp.report;

import io.pivotal.cfapp.domain.ServiceInstanceDetail;
import io.pivotal.cfapp.task.ServiceInstanceDetailRetrievedEvent;

public class ServiceInstanceDetailCsvReport {

    public String generateDetail(ServiceInstanceDetailRetrievedEvent event) {
    	StringBuffer detail = new StringBuffer();
        detail.append("\n");
        detail.append(ServiceInstanceDetail.headers());
        detail.append("\n");
        event.getDetail()
                .forEach(a -> {
                    detail.append(a.toCsv());
                    detail.append("\n");
                });
        return detail.toString();
    }

}
