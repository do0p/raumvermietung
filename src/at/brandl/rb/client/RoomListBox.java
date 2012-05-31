package at.brandl.rb.client;

import com.google.gwt.user.client.ui.ListBox;

public class RoomListBox extends ListBox {
	public RoomListBox() {
		for (Room room : Room.values()) {
			addItem(room.toString());
		}
	}

	public void setSelectedRoom(Room pRoom) {
		setSelectedIndex(getIndex(pRoom));
	}
	
	private int getIndex(Room pRoom) {
		for(int i = 0; i < Room.values().length; i++)
		{
			if(Room.values()[i].equals(pRoom))
			{
				return i;
			}
		}
		throw new IllegalArgumentException("room must be an element of enum");
	}

	public Room getSelectedRoom() {
		return Room.values()[getSelectedIndex()];
	}
}
