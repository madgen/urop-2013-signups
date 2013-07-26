package uk.ac.cam.signups.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Query;
import org.hibernate.Session;
//Import the following for logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.signups.models.Event;
import uk.ac.cam.signups.models.Row;
import uk.ac.cam.signups.models.Slot;
//Import models
import uk.ac.cam.signups.models.User;
import uk.ac.cam.signups.util.HibernateUtil;

import com.google.common.collect.ImmutableMap;
import com.googlecode.htmleasy.RedirectException;
import com.googlecode.htmleasy.ViewWith;

@Path("/")
public class HomePageController extends ApplicationController{
	
	// Create the logger
	private static Logger log = LoggerFactory.getLogger(HomePageController.class);
	
	private User user;
	
	@GET @Path("/")
	public void localhostRedirect() {
		throw new RedirectException("/app/#signapp/");
	}
	
	// Index
	@GET @Path("/signapp") 
	@Produces(MediaType.APPLICATION_JSON)
	public Map indexHomePage() {
		
		user = initialiseUser();
		
		// move most of this code to a better location later
		Session session = HibernateUtil.getTransactionSession();

		// get dates
		Calendar cal = Calendar.getInstance();
		ArrayList<ImmutableMap<String, ?>> weekdates = new ArrayList<ImmutableMap<String,?>>();
		SimpleDateFormat niceDateFormat = new SimpleDateFormat("EEEEE, dd MMMMM yyyy");
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEEE");
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		for(int i=0; i<7;i++){
			weekdates.add(ImmutableMap.of("day", dayFormat.format(cal.getTime()), "date", niceDateFormat.format(cal.getTime())));
			cal.add(Calendar.DAY_OF_YEAR, 1);
		}
		
		ArrayList<Slot> slots = new ArrayList<Slot>();
		Query getSlot = session.createQuery("from Slot where owner = :user").setParameter("user", user);
		slots = (ArrayList<Slot>) getSlot.list();
	  	Row row;
	  	Event event;
	  	List<ImmutableMap<String, ?>> events = new ArrayList<ImmutableMap<String,?>>();
		for(Slot s : slots){
			row = s.getRow();
			event = row.getEvent();
			events.add(ImmutableMap.of("date", row.getCalendar().getTime().toString(), "event", event.toMap()));
		}
		
		
		// Get user details
		log.debug("Index GET: Getting user details");
		
		ImmutableMap<String, ?> userMap = ulm.getAll();		
		
		return ImmutableMap.of("user", userMap, "deadlines", user.getUserDeadlinesMap(), "events", events, "weekdates", weekdates);
	}
	
	// DOS Index
	@GET @Path("signapp/DoS") @ViewWith("/soy/home_page.dos")
	public Map dosHomePage() {
		
		// Initialise user
		initialiseUser();
		
		// Does user have staff level access?
		if(!isStaff()){
			throw new RedirectException("/");
		}
		
		return ImmutableMap.of();
	}
	
	// Admin Index
	@GET @Path("signapp/admin") @ViewWith("/soy/home_page.admin")
	public Map adminHomePage() {
		return ImmutableMap.of();
	}
	
	// Authenticate staff
	public boolean isStaff() {
		try {
			return (ulm.getStatus().equals("staff"));
		} catch(NullPointerException e) {
			log.error("User initialisation failed");
			return false;
		}
	}
}