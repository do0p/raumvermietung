package at.brandl.rb.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import at.brandl.rb.client.Event;

public class InMemoryEventDao implements EventDao {
	
	private static int count = 0;  
	private final List<Event> events = new ArrayList<Event>();
	
	/* (non-Javadoc)
	 * @see at.brandl.rb.server.EventDao#getEvents()
	 */
	public List<Event> getEvents(Date pStartDate, Date pEndDate) {
		return events;
	}

	/* (non-Javadoc)
	 * @see at.brandl.rb.server.EventDao#storeEvent(at.brandl.rb.client.Event)
	 */
	public void storeEvent(Event pEvent) {
		pEvent.setId(Integer.toString(count++));
		events.add(pEvent);
		Collections.sort(events);
	}

	/* (non-Javadoc)
	 * @see at.brandl.rb.server.EventDao#removeEvent(at.brandl.rb.client.Event)
	 */
	public void removeEvent(Event pEvent) {
		events.remove(pEvent);
	}

	/* (non-Javadoc)
	 * @see at.brandl.rb.server.EventDao#updateEvent(at.brandl.rb.client.Event)
	 */
	public void updateEvent(Event pEvent) {
		events.remove(pEvent);
		events.add(pEvent);
		Collections.sort(events);
	}

}
