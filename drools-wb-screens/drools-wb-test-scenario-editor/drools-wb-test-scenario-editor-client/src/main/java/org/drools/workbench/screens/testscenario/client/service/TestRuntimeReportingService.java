package org.drools.workbench.screens.testscenario.client.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.drools.workbench.screens.testscenario.model.Success;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.PlaceManager;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

@ApplicationScoped
public class TestRuntimeReportingService {

    private PlaceManager placeManager;

    private Event<Success> successEvent;

    protected User identity;

    private ListDataProvider<Failure> dataProvider = new ListDataProvider<Failure>();

    public TestRuntimeReportingService() {
    }

    @Inject
    public TestRuntimeReportingService(PlaceManager placeManager,
                                       Event<Success> successEvent,
                                       User identity) {
        this.placeManager = placeManager;
        this.successEvent = successEvent;
        this.identity = identity;
    }

    public void addBuildMessages(final @Observes TestResultMessage message) {
        if (message.getIdentifier().equals(identity.getIdentifier())) {
            dataProvider.getList().clear();

            if (message.wasSuccessful()) {
                successEvent.fire(new Success(message.getRunCount()));
            } else {
                dataProvider.getList().addAll(message.getFailures());
                dataProvider.flush();
            }

            placeManager.goTo("org.kie.guvnor.TestResults");
        }
    }

    public void addDataDisplay(HasData<Failure> failures) {
        dataProvider.addDataDisplay(failures);
    }
}
