package at.brandl.rb.client;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EventsTable extends FlexTable {

	private final DateTimeFormat dateFormat;
	private int numHeaderRows = 1;
	private EventForm eventForm;
	private Button addButton;
	private boolean forAdmin;

	public EventsTable() {
		setStyleName("events");
		insertRow(0);
		fillRow(0, "Beginn", "Ende", "Veranstaltung", "Raum");
		dateFormat = DateTimeFormat.getFormat("dd.MM.yy HH:mm");
		getRowFormatter().setStyleName(0, "head");
		getColumnFormatter().addStyleName(0, "smallColumn");
		getColumnFormatter().addStyleName(1, "smallColumn");
		getColumnFormatter().addStyleName(3, "smallColumn");
	}

	public void setForAdmin(boolean pForAdmin) {
		forAdmin = pForAdmin;
	}

	/**
	 * insert event at given row
	 * 
	 * @param pEvent
	 * @param pRow
	 *            - 0 based index
	 */
	public void insert(Event pEvent, int pRow) {
		final int currentRow = pRow + numHeaderRows;
		while (getRowCount() <= currentRow) {
			insertRow(getRowCount());
		}
		fillRow(currentRow, pEvent);
	}

	private void fillRow(int pRow, Event pEvent) {
		fillRow(pRow, createLabel(formatDate(pEvent.getStartDate()),
				createClickHandler(pEvent)), createLabel(formatDate(pEvent
				.getEndDate()), createClickHandler(pEvent)), createLabel(pEvent
				.getTitle(), createClickHandler(pEvent)), createLabel(pEvent
				.getRoom().toString(), createClickHandler(pEvent)));
		getRowFormatter().setStyleName(pRow, pEvent.getState().toString());
	}

	private String formatDate(Date pDate) {

		return dateFormat.format(pDate);
	}

	private ClickHandler createClickHandler(final Event pEvent) {
		return new ClickHandler() {

			public void onClick(ClickEvent event) {
				addButton.setEnabled(false);
				eventForm.setEvent(pEvent);
				eventForm.setForAdmin(forAdmin);
				if (forAdmin) {
					eventForm.setupForEdit();
				} else {
					eventForm.setupForShow();
				}
				eventForm.center();
			}
		};
	}

	private Label createLabel(String pText, ClickHandler pClickHandler) {
		final Label label = new Label(pText);
		label.addClickHandler(pClickHandler);
		return label;
	}

	private void fillRow(int pRow, String... pHtml) {
		int cellCount = 0;
		for (String html : pHtml) {
			addCell(pRow);
			setHTML(pRow, cellCount++, html);
		}
	}

	private void fillRow(int pRow, Widget... pWidgets) {
		int cellCount = 0;
		for (Widget text : pWidgets) {
			addCell(pRow);
			setWidget(pRow, cellCount++, text);
		}
	}

	@Override
	public void clear() {
		int rowCount = getRowCount();
		while (rowCount > numHeaderRows) {
			removeRow(--rowCount);
		}
	}

	public void setEventForm(EventForm pDialogBox) {
		eventForm = pDialogBox;
	}

	public void setAddButton(Button pAddButton) {
		addButton = pAddButton;
	}
}
