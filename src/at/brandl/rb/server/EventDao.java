package at.brandl.rb.server;

import java.util.Date;
import java.util.List;

import at.brandl.rb.client.Event;

public interface EventDao {

	List<Event> getEvents(Date pStartDate, Date pEndDate) throws EventServiceException;

	void storeEvent(Event pEvent) throws EventServiceException;

	void removeEvent(Event pEvent) throws EventServiceException;

	void updateEvent(Event pEvent) throws EventServiceException;

}