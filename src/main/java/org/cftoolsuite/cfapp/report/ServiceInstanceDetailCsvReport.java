package org.cftoolsuite.cfapp.report;

import org.cftoolsuite.cfapp.domain.ServiceInstanceDetail;
import org.cftoolsuite.cfapp.task.ServiceInstanceDetailRetrievedEvent;

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
