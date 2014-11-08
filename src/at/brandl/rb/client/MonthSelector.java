package at.brandl.rb.client;

import java.util.Date;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

public class MonthSelector extends HorizontalPanel {

	private static final int FIRST_YEAR = 2009;
	private ListBox month;
	private ListBox year;

	public MonthSelector() {
		month = new ListBox();
		month.addItem("Alle", "0");
		month.addItem("Januar", "1");
		month.addItem("Februar", "2");
		month.addItem("Maerz", "3");
		month.addItem("April", "4");
		month.addItem("Mai", "5");
		month.addItem("Juni", "6");
		month.addItem("Juli", "7");
		month.addItem("August", "8");
		month.addItem("September", "9");
		month.addItem("Oktober", "10");
		month.addItem("November", "11");
		month.addItem("Dezember", "12");

		year = new ListBox();

		
		add(month);
		add(year);
	}

	public MonthSelector(Date pDate) {
		this();
		setCurrentDate(pDate);
	}

	public void setRaumVermietungService(final RaumVermietungService pRaumVermietungService)
	{
		year.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				pRaumVermietungService.updateEventsTable();
			}});
		
		month.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				pRaumVermietungService.updateEventsTable();
			}});
		
	}
	
	public Date getStartDate() {
		return DateUtil.getStartDate(getSelectedMonth(), getSelectedYear());
	}

	public Date getEndDate() {
		return DateUtil.getEndDate(getSelectedMonth(), getSelectedYear());
	}

	public void setCurrentDate(Date pDate) {
		setupYearSelection(pDate.getYear() + 1900);
		month.setSelectedIndex(pDate.getMonth() + 1);
	}

	private void setupYearSelection(int pCurrentYear) {
		year.clear();
		for (int yr = FIRST_YEAR; yr <= pCurrentYear + 2; yr++) {
			year.addItem(Integer.toString(yr));
		}
		year.setSelectedIndex(pCurrentYear - FIRST_YEAR);
	}

	public int getSelectedMonth() {
		return Integer.parseInt(month.getValue(month.getSelectedIndex()));
	}
	
	public int getSelectedYear() {
		return Integer.parseInt(year.getValue(year.getSelectedIndex()));
	}
}
