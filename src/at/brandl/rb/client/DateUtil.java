package at.brandl.rb.client;

import java.util.Date;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class DateUtil {

	public static Date getEndDate(int selectedMonth, int selectedYear) {
		if (selectedMonth == 0 || selectedMonth == 12) {
			selectedMonth = 0;
			selectedYear++;
		}
		final Date date = DateUtil.createDate(selectedMonth, selectedYear);
		return new Date(date.getTime() - 1);
	}

	public static Date getStartDate(int selectedMonth, int selectedYear) {
		if (selectedMonth > 0) {
			selectedMonth--;
		}		
		return DateUtil.createDate(selectedMonth, selectedYear);
	}

	private static Date createDate(int selectedMonth, int selectedYear) {
		final Date date = new Date();
		CalendarUtil.setToFirstDayOfMonth(date);
		date.setMonth(selectedMonth);
		date.setYear(selectedYear - 1900);
		return date;
	}

}
