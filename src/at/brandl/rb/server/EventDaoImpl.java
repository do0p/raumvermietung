package at.brandl.rb.server;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import at.brandl.rb.client.Event;
import at.brandl.rb.client.Room;
import at.brandl.rb.client.State;
import at.brandl.rb.client.Visibility;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

public class EventDaoImpl implements EventDao {
	
	private static final String PLANNING = "In Planung";
	private static final String RAUMMIETE_CAL_ID = "raummiete.lws@gmail.com";
	private static final String TIMEZONE_VIENNA = "Europe/Vienna";
	private static final String FIELDS = "items(id, summary, description, start, end,  location, visibility)";
	private final Calendar calendar;

	public EventDaoImpl(HttpRequestInitializer requestInitializer) {
		this.calendar = new Calendar(new NetHttpTransport(),
				new JacksonFactory(), requestInitializer);
	}

	@Override
	public List<Event> getEvents(Date pStartDate,
			Date pEndDate) throws EventServiceException {
		try {
			return map(calendar.events().list(RAUMMIETE_CAL_ID).setFields(FIELDS)
					.setTimeMin(convertToDateTime(pStartDate))
					.setTimeMax(convertToDateTime(pEndDate)).execute());

		} catch (IOException e) {
			throw new EventServiceException(
					"could not get Events with startdate " + pStartDate
							+ " and enddate " + pEndDate, e);
		}
	}

	@Override
	public void storeEvent(Event event)
			throws EventServiceException {
		try {
			calendar.events().insert(RAUMMIETE_CAL_ID, map(event, null)).execute()
					.getId();
		} catch (IOException e) {
			throw new EventServiceException("could not store event " + event
					+ " in calendar " + RAUMMIETE_CAL_ID, e);
		}
	}

	@Override
	public void removeEvent(Event event)
			throws EventServiceException {
		try {
			calendar.events().delete(RAUMMIETE_CAL_ID, event.getId()).execute();
		} catch (IOException e) {
			throw new EventServiceException("could not delete eventId "
					+ event.getId() + " from calendar " + RAUMMIETE_CAL_ID, e);
		}
	}

	@Override
	public void updateEvent(Event pEvent)
			throws EventServiceException {
		try {
			Integer sequence = calendar.events().get(RAUMMIETE_CAL_ID, pEvent.getId()).execute().getSequence();
			calendar.events().patch(RAUMMIETE_CAL_ID, pEvent.getId(), map(pEvent, sequence)).execute();
		} catch (IOException e) {
			throw new EventServiceException("could not update event " + pEvent
					+ " in calendar " + RAUMMIETE_CAL_ID, e);
		}

	}

	public String createCalendar(String name) throws IOException {
		com.google.api.services.calendar.model.Calendar content = new com.google.api.services.calendar.model.Calendar();
		content.setSummary(name);
		content.setTimeZone(TIMEZONE_VIENNA);
		return calendar.calendars().insert(content).execute().getId();
	}

	public void deleteCalendar(String id) throws IOException {
		calendar.calendars().delete(id).execute();
	}

	public List<String> listVisibleCalendarIds(String summary)
			throws IOException {
		CalendarList calendars = calendar.calendarList().list().execute();
		final List<String> ids = new ArrayList<String>();
		for (CalendarListEntry cal : calendars.getItems()) {
			if (cal.getSummary().equals(summary)) {
				ids.add(cal.getId());
			}
		}
		return ids;
	}

	private DateTime convertToDateTime(Date date) {
		return new DateTime(date, TimeZone.getTimeZone(TIMEZONE_VIENNA));
	}

	private EventDateTime convertToEventDateTime(Date date) {
		final EventDateTime eventDateTime = new EventDateTime();
		eventDateTime.setDateTime(convertToDateTime(date));
		eventDateTime.setTimeZone(TIMEZONE_VIENNA);
		return eventDateTime;
	}

	private List<Event> map(Events events) {
		final List<Event> result = new ArrayList<Event>();
		final List<com.google.api.services.calendar.model.Event> items = events
				.getItems();
		if (items != null && !items.isEmpty()) {
			for (com.google.api.services.calendar.model.Event jsonEvent : items) {
				result.add(map(jsonEvent));
			}
		}
		Collections.sort(result);
		return result;
	}

	private com.google.api.services.calendar.model.Event map(Event event, Integer sequence) {
		final com.google.api.services.calendar.model.Event jsonEvent = new com.google.api.services.calendar.model.Event();
		jsonEvent.setSequence(sequence);
		jsonEvent.setSummary(event.getTitle());
		jsonEvent.setDescription(createDescription(event));
		jsonEvent.setStart(convertToEventDateTime(event.getStartDate()));
		jsonEvent.setEnd(convertToEventDateTime(event.getEndDate()));
		jsonEvent.setLocation(event.getRoom().toString());
		jsonEvent.setVisibility(createVisibilty(event.getVisibilty()));
		return jsonEvent;
	}


	private String createVisibilty(
			Visibility pVisibilty) {
		switch (pVisibilty) {
		case Private:
			return "private";
		case Public:
			return "public";
		}
		throw new IllegalArgumentException("unknown visibility " + pVisibilty);
	}
	
	private String createDescription(Event pEvent) {
		return (State.Planning.equals(pEvent.getState()) ? PLANNING + "\n\n"
				: "")
				+ pEvent.getDescription();
	}
	
	private Event map(com.google.api.services.calendar.model.Event jsonEvent) {
		final Event event = new Event();
		event.setId(jsonEvent.getId());
		event.setTitle(jsonEvent.getSummary());
		final String description = jsonEvent.getDescription() == null ? ""
				: jsonEvent.getDescription() ;
		event.setDescription(createDescription(description));
		event.setState(createState(description));
		event.setVisibility(createVisibilty(jsonEvent.getVisibility()));
		event.setStartDate(convert(jsonEvent.getStart()));
		event.setEndDate(convert(jsonEvent.getEnd()));
		event.setRoom(createRoom(jsonEvent.getLocation()));
		return event;
	}
	


	private Visibility createVisibilty(
			String pValue) {
		if ("private".equals(pValue)) {
			return Visibility.Private;
		}
		if ("public".equals(pValue)) {
			return Visibility.Public;
		}
		throw new IllegalArgumentException("unknown visibility " + pValue);
	}
	
	private Room createRoom(String pLocation) {
		return Room.valueOf(pLocation);
	}
	
	private String createDescription(String pDescription) {
		if (pDescription.startsWith(PLANNING)) {
			return pDescription.substring(PLANNING.length()).trim();
		}
		return pDescription;
	}

	private State createState(String pDescription) {
		return pDescription.toLowerCase().indexOf(PLANNING.toLowerCase()) != -1 ? State.Planning
				: State.Confirmed;
	}
	
	private Date convert(EventDateTime date) {
		return new Date(getDateOrDateTime(date).getValue());
	}

	public DateTime getDateOrDateTime(EventDateTime date) {
		return date.getDateTime() != null ? date.getDateTime() : date.getDate();
	}

	public static GoogleCredential createCredentialForServiceAccount(
			String serviceAccountId, File p12File,
			String... serviceAccountScopes) throws GeneralSecurityException,
			IOException {
		return new GoogleCredential.Builder()
				.setTransport(new NetHttpTransport())
				.setJsonFactory(new JacksonFactory())
				.setServiceAccountId(serviceAccountId)
				.setServiceAccountScopes(Arrays.asList(serviceAccountScopes))
				.setServiceAccountPrivateKeyFromP12File(p12File).build();
	}

}
