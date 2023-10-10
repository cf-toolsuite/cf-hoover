package io.pivotal.cfapp.report;

import io.pivotal.cfapp.domain.AppDetail;
import io.pivotal.cfapp.task.AppDetailRetrievedEvent;

public class AppDetailCsvReport  {

    public String generateDetail(AppDetailRetrievedEvent event) {
        StringBuffer details = new StringBuffer();
        details.append("\n");
        details.append(AppDetail.headers());
        details.append("\n");
        event.getDetail()
                .forEach(a -> {
                    details.append(a.toCsv());
                    details.append("\n");
                });
        return details.toString();
    }

}