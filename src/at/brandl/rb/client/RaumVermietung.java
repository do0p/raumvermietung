package at.brandl.rb.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class RaumVermietung implements EntryPoint {

	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	private final EventServiceAsync eventService = GWT
			.create(EventService.class);
	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);
	private final Button addButton = new Button("neu");
	private final Anchor loginLink = new Anchor("login");
	private final Anchor csvLink = new Anchor("csv");
	private final MonthSelector monthSelector = new MonthSelector(new Date());
	private final EventsTable eventsTable = new EventsTable();

	private boolean forAdmin;

	public void onModuleLoad() {

		// create widgets
		addButton.setEnabled(false);
		addButton.setVisible(false);

		loginLink.setStyleName("fltrt");

		final EventForm eventForm = EventForm
				.createEventForm(createRaumVermietungsService());

		addButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addButton.setEnabled(false);
				eventForm.setForAdmin(forAdmin);
				eventForm.setupForNew();
				eventForm.center();
			}
		});

		eventsTable.setEventForm(eventForm);
		eventsTable.setAddButton(addButton);

		monthSelector.setRaumVermietungService(createRaumVermietungsService());

		// add widgets to panel
		RootPanel.get("buttons").add(addButton);
		RootPanel.get("buttons").add(monthSelector);
		RootPanel.get("link").add(csvLink);
		RootPanel.get("login").add(loginLink);
		RootPanel.get("termine").add(eventsTable);

		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
						updateEvents();
					}

					public void onSuccess(LoginInfo result) {
						final LoginInfo loginInfo = result;
						if (loginInfo.isLoggedIn()) {
							createLogoutLink(loginInfo);
						} else {
							createLoginLink(loginInfo);
						}
						forAdmin = loginInfo.isAdmin();
						addButton.setEnabled(loginInfo.isAdmin());
						addButton.setVisible(loginInfo.isAdmin());
						eventsTable.setForAdmin(loginInfo.isAdmin());
						updateEvents();
					}

				});

	}

	private RaumVermietungService createRaumVermietungsService() {
		return new RaumVermietungService() {
			public void enableAddButton() {
				if (forAdmin) {
					addButton.setEnabled(true);
				}
			}

			public EventServiceAsync getEventService() {
				return eventService;
			}

			public void updateEventsTable() {
				updateEvents();
			}
		};
	}

	private void createLogoutLink(LoginInfo pLoginInfo) {
		loginLink.setText("Logout");
		loginLink.setHref(pLoginInfo.getLogoutUrl());
	}

	private void createLoginLink(LoginInfo pLoginInfo) {
		loginLink.setText("Login");
		loginLink.setHref(pLoginInfo.getLoginUrl());
	}

	private void updateEvents() {
		eventService.getEvents(monthSelector.getStartDate(), monthSelector
				.getEndDate(), new AsyncCallback<List<Event>>() {
			public void onFailure(Throwable pCaught) {
			}

			public void onSuccess(List<Event> pEvents) {
				eventsTable.clear();
				for (int i = 0; i < pEvents.size(); i++) {
					eventsTable.insert(pEvents.get(i), i);
				}
			}
		});
		csvLink.setHref("/csv?m=" + monthSelector.getSelectedMonth() + "&y="
				+ monthSelector.getSelectedYear());
	}
}
