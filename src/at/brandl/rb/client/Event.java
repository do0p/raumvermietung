package at.brandl.rb.client;

import java.io.Serializable;
import java.util.Date;


public class Event implements Serializable, Comparable<Event> {

	private static final long serialVersionUID = 4912333683847788038L;
	private String id;
	
	private String title;
	private Room room;
	private Date startDate;
	private Date endDate;
	private String description;
	private Visibility visibilty = Visibility.Private;
	private State state = State.Planning;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date pStartDate) {
		startDate = pStartDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String pTitle) {
		title = pTitle;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room pRoom) {
		room = pRoom;
	}

	public int compareTo(Event o) {
		return startDate.compareTo(o.getStartDate());
	}
	
	public String getId() {
		return id;
	}

	public void setId(String pId) {
		id = pId;
	}
	
	@Override
	public boolean equals(Object pObj) {
		if(!(pObj instanceof Event))
		{
			return false;
		}
		final Event other = (Event) pObj;
		return id == null ? other == null : id.equals(other.id);
	}
	
	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}
	
	@Override
	public String toString() {
		return title + " " + startDate.toString() + " " + room;
	}

	public void setEndDate(Date pEndDate) {
		endDate = pEndDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setDescription(String pDescription) {
		description = pDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setVisibility(Visibility pVisibilty) {
		visibilty = pVisibilty;
	}

	public Visibility getVisibilty() {
		return visibilty;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
}
