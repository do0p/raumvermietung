package at.brandl.rb.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import at.brandl.rb.client.DateUtil;
import at.brandl.rb.client.Event;

public class CsvService extends HttpServlet {

	private static final long serialVersionUID = 9205860285613044024L;
	public static final String YEAR = "y";
	public static final String MONTH = "m";
	public static final String DELIM = ";";
	public static final String NEWLINE = "\n";

	private EventServiceImpl eventService;
	private DateFormat dateTimeFormat;
	private DateFormat timeFormat;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		eventService = new EventServiceImpl();
		dateTimeFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN);
		TimeZone tz = TimeZone.getTimeZone("Europe/Vienna");
		dateTimeFormat.setTimeZone(tz);
		timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMAN);
		timeFormat.setTimeZone(tz);
	}

	@Override
	protected void doGet(HttpServletRequest pReq, HttpServletResponse pResp)
			throws ServletException, IOException {
		final int month;
		final int year;
		try {
			month = Integer.parseInt(pReq.getParameter(MONTH));
			year = Integer.parseInt(pReq.getParameter(YEAR));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("could not parse input params",
					e);
		}
		final Date startDate = DateUtil.getStartDate(month, year);
		final Date endDate = DateUtil.getEndDate(month, year);
		
		final HashMap<String, List<Event>> eventDates = getEvents(startDate,
				endDate);

		final String csv = createCsvText(startDate, endDate, eventDates);
		
		pResp.setContentType("text/comma-separated-values");
		pResp.setHeader("Content-Disposition", "attachment; filename=veranstaltungen_" + month + "-" + year + ".csv");
		final PrintWriter writer = pResp.getWriter();
		writer.write(csv);
		writer.close();
		writer.flush();

	}

	private String createCsvText(final Date startDate, final Date endDate,
			final HashMap<String, List<Event>> eventDates) {
		final StringBuilder csv = new StringBuilder();
		writeHeading(csv);
		final DateIterator dateIterator = new DateIterator(startDate, endDate);
		while (dateIterator.hasNext()) {
			final String date = dateTimeFormat.format(dateIterator.next());
			writeDate(csv, date, eventDates.get(date));
		}
		return csv.toString();
	}

	private HashMap<String, List<Event>> getEvents(Date pStartDate,
			Date pEndDate) {
		final List<Event> events = eventService.getEvents(pStartDate, pEndDate);
		final HashMap<String, List<Event>> eventDates = new HashMap<String, List<Event>>();

		for (Event event : events) {
			final DateIterator dateIterator = new DateIterator(event
					.getStartDate(), event.getEndDate());
			while (dateIterator.hasNext()) {
				final String dateStr = dateTimeFormat.format(dateIterator
						.next());
				List<Event> eventList = eventDates.get(dateStr);
				if (eventList == null) {
					eventList = new ArrayList<Event>();
					eventDates.put(dateStr, eventList);
				}
				eventList.add(event);
			}
		}
		return eventDates;
	}

	private void writeDate(StringBuilder pCsv, String pDate, List<Event> events) {

		String stock1 = createStockText(1, events);
		String stock2 = createStockText(2, events);
		String stock3 = createStockText(3, events);
		writeLine(pCsv, pDate, stock1, stock2, stock3, "");

	}

	private String createStockText(int pFloor, List<Event> pEvents) {
		final StringBuilder text = new StringBuilder();
		boolean first = true;
		for (Event event : getEventsForFloor(pFloor, pEvents)) {
			if (!first) {
				text.append(", ");
			} else {
				first = false;
			}
			text.append(formatStockText(event));

		}
		return text.toString();
	}

	private String formatStockText(Event event) {
		final StringBuilder text = new StringBuilder();
		text.append(event.getTitle());
		text.append(", (");
		text.append(timeFormat.format(event.getStartDate()));
		text.append(" - ");
		text.append(timeFormat.format(event.getEndDate()));
		text.append(")");
		return text.toString();
	}

	private List<Event> getEventsForFloor(int pFloor, List<Event> pEvents) {
		final List<Event> events = new ArrayList<Event>();
		if (pEvents != null) {
			for (Event event : pEvents) {
				if (event.getRoom().getFloor() == pFloor
						|| event.getRoom().getFloor() == -1) {
					events.add(event);
				}
			}
		}
		return events;
	}

	private void writeHeading(StringBuilder pCsv) {
		writeLine(pCsv, "Datum", "1. Stock", "2. Stock", "3. Stock",
				"Bemerkung");
	}

	private void writeLine(StringBuilder pCsv, String... pText) {
		for (String text : pText) {
			pCsv.append(text);
			pCsv.append(DELIM);
		}
		pCsv.append(NEWLINE);
	}

}
