package uk.ac.cam.signups.controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.resteasy.annotations.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.signups.models.Group;
import uk.ac.cam.signups.models.User;
import uk.ac.cam.signups.util.HibernateSessionRequestFilter;
import uk.ac.cam.signups.util.HibernateUtil;

import com.google.common.collect.ImmutableMap;
import com.googlecode.htmleasy.RedirectException;
import com.googlecode.htmleasy.ViewWith;

@Path("/groups")
public class GroupsController extends ApplicationController {
		// Create the logger
		private static Logger log = LoggerFactory.getLogger(GroupsController.class);
		
		// Get current user from raven session
		private User user;
		
		// Index
		@GET @Path("/") @ViewWith("/soy/groups.index")
		public Map indexGroups() {
			// Initialise user
			user = initialiseUser();
			
			return ImmutableMap.of("groups", user.getGroupsMap());
		}
		
		// Create
		@POST @Path("/") 
		public void createGroup(@Form Group group, @FormParam("users[]") String[] users) throws Exception {
			// Initialise user
			user = initialiseUser();
			
			if(user==null){System.out.println("No user..");}

			Set<User> groupMembers = new HashSet<User>();
			// Register or retrieve all group members as User objects and add to set
			for(int i=0; i<users.length; i++){
				groupMembers.add(User.registerUser(users[i]));
			}
			
			// Add group members to group
			group.setUsers(groupMembers);
			
			// Set group owner as current user
			group.setOwner(user);
			
			System.out.println("Created group:");
			System.out.println("Group name = " + group.getTitle());
			System.out.println("Owner = " + group.getOwner().getCrsid());
			for(User u: group.getUsers()){
				System.out.println(u.getCrsid());
			}

			
			// Save group to database
			log.info("Adding group to databse.");
			Session session = HibernateUtil.getTransaction();
			session.save(group);
			session.getTransaction().commit();
			
			throw new RedirectException("/groups");
		}
		
		// Edit
//		@GET @Path("/{id}/edit") @ViewWith("/soy/groups.edit")
//		public Map editGroup(@PathParam("id") int id) {
//			return ImmutableMap.of();
//		}
		
		// Update
//		@PUT @Path("/{id}")
//		public void updateGroup(@PathParam("id") int id,
//				@Form Group group) {
//			
//			throw new RedirectException("/");
//		}
		
		// Destroy 
//		@DELETE @Path("/{id}")
//		public void deleteGroup(@PathParam("id") int id) {
//			
//			throw new RedirectException("/");
//		}
		
}