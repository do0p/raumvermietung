package at.brandl.rb.server;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import at.brandl.rb.client.Event;
import at.brandl.rb.client.EventService;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.calendar.CalendarScopes;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class EventServiceImpl extends RemoteServiceServlet implements
		EventService {

	private static final long serialVersionUID = 2355372289476417861L;
	private EventDao eventDao;

	private static final String CLIENT_ID = "418212430257@developer.gserviceaccount.com";
	private static final String KEY_FILE_REL_PATH = "/WEB-INF/key.p12";
	
	@Override
	public void init() throws ServletException {
			super.init();
			final File keyFile = new File(getServletContext().getRealPath(
					KEY_FILE_REL_PATH));
			try {
				final HttpRequestInitializer requestInitializer = EventDaoImpl
						.createCredentialForServiceAccount(CLIENT_ID, keyFile,
								CalendarScopes.CALENDAR);
				eventDao = new EventDaoImpl(requestInitializer);
			} catch (GeneralSecurityException e) {
				throw new ServletException("could not create event dao", e);
			} catch (IOException e) {
				throw new ServletException("could not create event dao", e);
			}
	}
	
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
