package fede.geotagger;

import java.util.Date;

public class PositionForRange extends Position {
	private Long from;
	private Long to;
	public Long getFrom() {
		return from;
	}
	public void setFrom(Long from) {
		this.from = from;
	}
	public Long getTo() {
		return to;
	}
	public void setTo(Long to) {
		this.to = to;
	}
	public PositionForRange() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PositionForRange(Long f, Long t, String name, String latitude, String longitude,
			String altitude, Date d) {
		super(name, latitude, longitude, altitude, d);
		from = f;
		to = t;
	}
	
}
