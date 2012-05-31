package at.brandl.rb.client;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class EventForm extends DialogBox {
	private final DateTimeFormat dateTimeFormat;

	private TextBox title;
	private DateBox startDate;
	private DateBox endDate;
	private CheckBox publicVisibilty;
	private CheckBox planning;
	private TextArea description;
	private RoomListBox rooms;

	private Button closeButton;
	private Button newButton;
	private Button editButton;
	private Button deleteButton;

	private Panel panel;
	private Panel buttons;
	private RaumVermietungService raumVermietungsService;
	private String id;

	private boolean forAdmin;

	private EventForm() {
		dateTimeFormat = DateTimeFormat.getFormat("d.M.yy HH:mm");

		setText("Termin");
		setAnimationEnabled(true);

		// setup fields
		startDate = createDateBox();
		startDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			public void onValueChange(ValueChangeEvent<Date> event) {
				if (endDate.getValue() == null
						|| endDate.getValue().before(event.getValue())) {
					endDate.setValue(event.getValue());
				}
			}
		});
		endDate = createDateBox();
		endDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			public void onValueChange(ValueChangeEvent<Date> event) {
				if (startDate.getValue() == null
						|| startDate.getValue().after(event.getValue())) {
					startDate.setValue(event.getValue());
				}
			}
		});
		title = new TextBox();
		rooms = new RoomListBox();
		description = new TextArea();
		publicVisibilty = new CheckBox();
		planning = new CheckBox();

		setForAdmin(false);

		buttons = new HorizontalPanel();
		
		// create panel
		panel = createPanel();
		setWidget(panel);

		
		
		// create buttons
		createCloseButton();
		createNewButton();
		createEditButton();
		createDeleteButton();

	}

	private DateBox createDateBox() {
		final DateBox dateBox = new DateBox();
		dateBox.setFormat(new DateBox.DefaultFormat(dateTimeFormat));
		return dateBox;
	}

	public void setEvent(Event pEvent) {
		id = pEvent.getId();
		title.setValue(pEvent.getTitle());
		startDate.setValue(pEvent.getStartDate());
		endDate.setValue(pEvent.getEndDate());
		description.setValue(pEvent.getDescription());
		publicVisibilty.setValue(Visibility.Public
				.equals(pEvent.getVisibilty()));
		planning.setValue(State.Planning.equals(pEvent.getState()));
		rooms.setSelectedRoom(pEvent.getRoom());
	}

	public void clearEvent() {
		id = null;
		title.setValue(null);
		startDate.setValue(null);
		endDate.setValue(null);
		description.setValue(null);
		publicVisibilty.setValue(Boolean.FALSE);
		planning.setValue(Boolean.FALSE);
		rooms.setSelectedIndex(0);
	}

	public Event getEvent() {
		final Event event = new Event();
		event.setId(id);
		event.setStartDate(startDate.getValue());
		event.setEndDate(endDate.getValue());
		event.setDescription(description.getValue());
		event.setVisibility(publicVisibilty.getValue() ? Visibility.Public
				: Visibility.Private);
		event.setState(planning.getValue() ? State.Planning : State.Confirmed);
		event.setTitle(title.getText());
		event.setRoom(rooms.getSelectedRoom());
		return event;
	}

	public void setupForEdit() {
		setText("Termin editieren");
		buttons.add(editButton);
		buttons.add(deleteButton);
		buttons.add(closeButton);
	}

	public void setupForNew() {
		setText("Termin anlegen");
		buttons.add(newButton);
		buttons.add(closeButton);
	}

	public void setupForShow() {
		buttons.add(closeButton);
	}

	private void createNewButton() {
		newButton = new Button("Speichern");
		newButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent pClickEvent) {
				final Event event = getEvent();
				raumVermietungsService.getEventService().storeEvent(event,
						createAsyncSuccessCallback());
			}

		});
	}

	private void createDeleteButton() {
		deleteButton = new Button("L&ouml;schen");
		deleteButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent pClickEvent) {
				final Event event = getEvent();
				raumVermietungsService.getEventService().deleteEvent(event,
						createAsyncSuccessCallback());
			}
		});
	}

	private void createEditButton() {
		editButton = new Button("Editieren");
		editButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent pClickEvent) {
				final Event event = getEvent();
				raumVermietungsService.getEventService().updateEvent(event,
						createAsyncSuccessCallback());
			}
		});
	}

	private void createCloseButton() {
		closeButton = new Button("Abbrechen");
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent pClickEvent) {
				hide();
				raumVermietungsService.enableAddButton();
				clearEvent();
				removeAllButtons();
			}

		});
	}

	private AsyncCallback<Void> createAsyncSuccessCallback() {
		return new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {
			}

			public void onSuccess(Void result) {
				hide();
				if (forAdmin) {
					raumVermietungsService.enableAddButton();
				}
				raumVermietungsService.updateEventsTable();
				clearEvent();
				removeAllButtons();
			}
		};
	}

	private Panel createPanel() {
		final FlexTable eventForm = new FlexTable();
		eventForm.addStyleName("dialogVPanel");
		int row = 0;
		insertRow(row++, "Title", title, eventForm);
		insertRow(row++, "Beginn", startDate, eventForm);
		insertRow(row++, "Ende", endDate, eventForm);
		insertRow(row++, "Raum", rooms, eventForm);
		insertRow(row++, "Beschreibung", description, eventForm);
		insertRow(row++, "&ouml;ffentlich", publicVisibilty, eventForm);
		insertRow(row++, "in Planung", planning, eventForm);
		eventForm.insertRow(row);
		eventForm.addCell(row);
		eventForm.setWidget(row, 0, buttons);
		eventForm.getFlexCellFormatter().setColSpan(row, 0, 2);
		return eventForm;
	}

	private void insertRow(int pRow, String pLabel, Widget pField, FlexTable pEventForm) {
		int column = 0;
		pEventForm.insertRow(pRow);
		pEventForm.addCell(pRow);
		pEventForm.setHTML(pRow, column, pLabel);
		pEventForm.getFlexCellFormatter().setAlignment(pRow, column++, HasHorizontalAlignment.ALIGN_RIGHT, HasVerticalAlignment.ALIGN_TOP);
		pEventForm.addCell(pRow);
		pEventForm.setWidget(pRow, column, pField);
		pEventForm.getFlexCellFormatter().setAlignment(pRow, column, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
	}

	private void removeAllButtons() {
		buttons.remove(closeButton);
		buttons.remove(newButton);
		buttons.remove(editButton);
		buttons.remove(deleteButton);
	}

	public static EventForm createEventForm(
			RaumVermietungService pRaumVermietung) {

		final EventForm eventForm = new EventForm();
		eventForm.raumVermietungsService = pRaumVermietung;
		return eventForm;
	}

	public void setForAdmin(boolean pForAdmin) {
		forAdmin = pForAdmin;
		title.setEnabled(forAdmin);
		rooms.setEnabled(forAdmin);
		description.setEnabled(forAdmin);
		publicVisibilty.setEnabled(forAdmin);
		planning.setEnabled(forAdmin);
		startDate.setEnabled(forAdmin);
		endDate.setEnabled(forAdmin);
	}

}
