package at.brandl.rb.server;

import java.util.Date;
import java.util.List;

import at.brandl.rb.client.Event;

public interface EventDao {

	public abstract List<Event> getEvents(Date pStartDate, Date pEndDate) throws EventServiceException;

	public abstract void storeEvent(Event pEvent) throws EventServiceException;

	public abstract void removeEvent(Event pEvent) throws EventServiceException;

	public abstract void updateEvent(Event pEvent) throws EventServiceException;

}