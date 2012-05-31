package at.brandl.rb.client;

import java.util.Date;
import java.util.List;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EventServiceAsync {

	void storeEvent(Event pEvent, AsyncCallback<Void> callback);

	void updateEvent(Event event, AsyncCallback<Void> asyncCallback);

	void deleteEvent(Event event, AsyncCallback<Void> asyncCallback);

	void getEvents(Date pStart, Date pEnd, AsyncCallback<List<Event>> callback);

}
