package at.brandl.rb.server;

import java.util.Date;
import java.util.List;

import at.brandl.rb.client.Event;
import at.brandl.rb.client.EventService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class EventServiceImpl extends RemoteServiceServlet implements
		EventService {

	private static final long serialVersionUID = 2355372289476417861L;
	private EventDao eventDao = new CalendarEventDao();

	public List<Event> getEvents(Date pStartDate, Date pEndDate) {
		return eventDao.getEvents(pStartDate, pEndDate);
	}

	public void storeEvent(Event pEvent) {
		eventDao.storeEvent(pEvent);

	}

	public void deleteEvent(Event pEvent) {
		eventDao.removeEvent(pEvent);
	}

	public void updateEvent(Event pEvent) {
		eventDao.updateEvent(pEvent);
	}

}
