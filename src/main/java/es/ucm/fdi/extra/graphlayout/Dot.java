package es.ucm.fdi.extra.graphlayout;

public class Dot {
	private String _id;
	private int _location;
	private boolean _faulty;
	
	public Dot(String id, int location, boolean faulty) {
		_id = id;
		_location = location;
		_faulty = faulty;
	}
	
	public String getId() {
		return _id;
	}
	
	public int getLocation() {
		return _location;
	}

	public boolean getFaulty() {
		return _faulty;
	}
	
}
