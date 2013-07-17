package uk.ac.cam.signups.models;

import com.google.common.collect.ImmutableMap;

import uk.ac.cam.signups.util.Util;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name="ROWS")
public class Row implements Mappable {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private int id;

	@Temporal(TemporalType.DATE)
	@Column(name="ROW_DATE")
	private Calendar calendar;

	@ManyToOne
	@JoinColumn(name = "DEADLINE_ID")
	private Deadline deadline;

	@ManyToOne
	@JoinColumn(name = "EVENT_ID")
	private Event event;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "row")
	private Set<Slot> slots = new HashSet<Slot>(0);

	@ManyToOne
	@JoinColumn(name = "TYPE_ID")
	private Type type;

	public Row() {}
	
	public Row(Calendar calendar) {
		this.calendar = calendar;
	}
	
	public Row(int id, 
						Calendar calendar, 
						Deadline deadline, 
						Set<Slot> slots, 
						Event event, 
						Type type) {
		this.id = id;
		this.calendar = calendar;
		this.deadline = deadline;
		this.slots = slots;
		this.event = event;
		this.type = type;
	}
	
	public int getId() { return this.id; }
	public void setId(int id) { this.id = id; }
	
	public Calendar getCalendar() { return this.calendar; }
	public void setCalendar(Calendar calendar) { this.calendar = calendar; }
	
	public Set<Slot> getSlots() { return this.slots; }
	public void setSlots(Set<Slot> slots) { this.slots = slots; }
	
	public Event getEvent() { return this.event; }
	public void setEvent(Event event) { this.event = event; }
	
	public Type getType() { return this.type; }
	public void setType(Type type) { this.type = type; }
	
	public Map<String, ?> toMap() {
		ImmutableMap.Builder<String, Object> builder = new ImmutableMap.Builder<String, Object>();
		builder = builder.put("date", ImmutableMap.of("day", calendar.get(Calendar.DAY_OF_MONTH),
																									"month", calendar.get(Calendar.MONTH),
																									"year", calendar.get(Calendar.YEAR),
																									"minute", calendar.get(Calendar.MINUTE),
																									"second", calendar.get(Calendar.SECOND)));
		builder = builder.put("slots", Util.getImmutableCollection(slots));
		builder = builder.put("type", type.toMap());
		return ImmutableMap.of("slots", slots, "type", type);
	}
}