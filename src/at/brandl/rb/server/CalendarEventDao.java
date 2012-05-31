package at.brandl.rb.server;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import at.brandl.rb.client.Event;
import at.brandl.rb.client.Room;
import at.brandl.rb.client.State;
import at.brandl.rb.client.Visibility;

import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.BaseEventEntry;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.util.AuthenticationException;

public class CalendarEventDao implements EventDao {
	private static final String PLANNING = "In Planung";
	private static final String CALENDAR_URL = "http://www.google.com/calendar/feeds/raummiete.lws@gmail.com/private/full";
	private CalendarService service;
	private Map<String, String> editUrls = new HashMap<String, String>();

	public CalendarEventDao() {
		service = new CalendarService("Raumvermietung");
		try {
			service.setUserCredentials("raummiete.lws@gmail.com", "auggracd");
			service.setProtocolVersion(CalendarService.Versions.V1);
		} catch (AuthenticationException e) {
			throw new IllegalStateException(
					"could not initialize google calendar service", e);
		}
	}

	public List<Event> getEvents(Date pStartDate, Date pEndDate) throws EventServiceException {
		final List<Event> events = new ArrayList<Event>();
		
		try {
			final CalendarQuery query = new CalendarQuery(new URL(CALENDAR_URL));
			query.setMinimumStartTime(createDateTime(pStartDate));
			query.setMaximumStartTime(createDateTime(pEndDate));
			CalendarEventFeed resultFeed = service.query(query,	CalendarEventFeed.class);
			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
				final CalendarEventEntry entry = resultFeed.getEntries().get(i);
				final Event event = createEvent(entry);
				events.add(event);

			}
		} catch (Exception e) {
			throw new EventServiceException("could not query events", e);
		}
		Collections.sort(events);
		return events;
	}

	public void removeEvent(Event pEvent) throws EventServiceException {
		try {
			final URL editUrl = new URL(editUrls.get(pEvent.getId()));
			service.delete(editUrl);
			editUrls.remove(pEvent.getId());
		} catch (Exception e) {
			throw new EventServiceException("could not delete event " + pEvent,
					e);
		}
	}

	public void storeEvent(Event pEvent) throws EventServiceException {
		final CalendarEventEntry entry = createEntry(pEvent);

		try {
			service.insert(new URL(CALENDAR_URL), entry);
		} catch (Exception e) {
			throw new EventServiceException("could not quer events", e);
		}
	}

	public void updateEvent(Event pEvent) throws EventServiceException {
		CalendarEventEntry myEntry = createEntry(pEvent);

		try {
			final URL editUrl = new URL(editUrls.get(pEvent.getId()));
			service.update(editUrl, myEntry);
		} catch (Exception e) {
			throw new EventServiceException("could not quer events", e);
		}

	}

	private Event createEvent(CalendarEventEntry pEntry) {
		final Event event = new Event();
		event.setId(pEntry.getId());
		event.setTitle(pEntry.getTitle().getPlainText());
		final When times = pEntry.getTimes().get(0);
		event.setStartDate(new Date(times.getStartTime().getValue()));
		event.setEndDate(new Date(times.getEndTime().getValue()));
		event.setRoom(createRoom(pEntry.getLocations().get(0)));
		final String description = pEntry.getContent() == null ? ""
				: ((TextContent) pEntry.getContent()).getContent()
						.getPlainText();
		event.setDescription(createDescription(description));
		event.setState(createState(description));
		event.setVisibility(createVisibilty(pEntry.getVisibility()));
		editUrls.put(event.getId(), pEntry.getEditLink().getHref());
		return event;
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

	private CalendarEventEntry createEntry(Event pEvent) {
		final CalendarEventEntry entry = new CalendarEventEntry();

		entry.setId(pEvent.getId());
		entry.setTitle(new PlainTextConstruct(pEvent.getTitle()));

		final Where location = new Where();
		location.setValueString(pEvent.getRoom().toString());
		entry.addLocation(location);

		final When eventTimes = new When();
		eventTimes.setStartTime(createDateTime(pEvent.getStartDate()));
		eventTimes.setEndTime(createDateTime(pEvent.getEndDate()));
		entry.addTime(eventTimes);

		entry.setContent(new PlainTextConstruct(createDescription(pEvent)));
		entry.setVisibility(createVisibilty(pEvent.getVisibilty()));

		return entry;
	}

	private String createDescription(Event pEvent) {
		return (State.Planning.equals(pEvent.getState()) ? PLANNING + "\n\n"
				: "")
				+ pEvent.getDescription();
	}

	private com.google.gdata.data.extensions.BaseEventEntry.Visibility createVisibilty(
			Visibility pVisibilty) {
		switch (pVisibilty) {
		case Private:
			return BaseEventEntry.Visibility.PRIVATE;
		case Public:
			return BaseEventEntry.Visibility.PUBLIC;
		}
		throw new IllegalArgumentException("unknown visibility " + pVisibilty);
	}

	private DateTime createDateTime(Date pDate) {
		final DateTime endTime = new DateTime(pDate.getTime());
		endTime.setTzShift(calcShift(pDate));
		return endTime;
	}

	private Integer calcShift(Date pDate) {
		final GregorianCalendar calendar = new GregorianCalendar(TimeZone
				.getTimeZone("Europe/Vienna"));
		calendar.setTime(pDate);
		return (calendar.get(Calendar.ZONE_OFFSET) + calendar
				.get(Calendar.DST_OFFSET))
				/ (60 * 1000);
	}

	private Room createRoom(Where pLocation) {
		return Room.valueOf(pLocation.getValueString());
	}

	private Visibility createVisibilty(
			com.google.gdata.data.extensions.BaseEventEntry.Visibility pValue) {
		if (BaseEventEntry.Visibility.PRIVATE.equals(pValue)) {
			return Visibility.Private;
		}
		if (BaseEventEntry.Visibility.PUBLIC.equals(pValue)) {
			return Visibility.Public;
		}
		throw new IllegalArgumentException("unknown visibility " + pValue);
	}
}
