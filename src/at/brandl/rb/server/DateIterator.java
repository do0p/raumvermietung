package at.brandl.rb.server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DateIterator implements Iterator<Date> {

	private GregorianCalendar calendar;
	private boolean allSeen;
	private Calendar endTime;

	public DateIterator(Date pStartDate, Date pEnddate) {
		calendar = new GregorianCalendar();
		calendar.setTime(pStartDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		endTime = new GregorianCalendar();
		endTime.setTime(pEnddate);
		endTime.set(Calendar.HOUR_OF_DAY, 0);
		endTime.set(Calendar.MINUTE, 0);
		endTime.set(Calendar.SECOND, 0);
	}

	public boolean hasNext() {
		if (endTime.before(calendar)) {
			return false;
		}
		return true;
	}

	public Date next() {
		if (allSeen) {
			throw new NoSuchElementException();
		}
		final Date returnDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		allSeen = !hasNext();
		return returnDate;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
