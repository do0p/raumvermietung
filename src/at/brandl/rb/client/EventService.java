package at.brandl.rb.client;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("event")
public interface EventService extends RemoteService
{
	List<Event> getEvents(Date pStart, Date pEnd);
	
	void storeEvent(Event pEvent);

	void updateEvent(Event pEvent);

	void deleteEvent(Event pEvent);
}
