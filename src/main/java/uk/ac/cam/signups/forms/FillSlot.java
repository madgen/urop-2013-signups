package uk.ac.cam.signups.forms;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import uk.ac.cam.signups.models.Row;
import uk.ac.cam.signups.models.Slot;
import uk.ac.cam.signups.models.User;
import uk.ac.cam.signups.models.Type;
import uk.ac.cam.signups.util.HibernateUtil;
import uk.ac.cam.signups.util.Util;

import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;

public class FillSlot {
	@PathParam("id") int eventId;
	@FormParam("slot_crsids[]") String[] crsids;
	@FormParam("slot_ids[]") int[] slotIds;
	@FormParam("types[]") int[] typeIds;
	
	Logger log = LoggerFactory.getLogger(FillSlot.class);
	
	public void handle(int id) {
		Session session = HibernateUtil.getTransactionSession();
		
		Set<Integer> ids = new HashSet<Integer>();
		for(int slotId: slotIds) {
			ids.add(slotId);
		}
		
		@SuppressWarnings("unchecked")
    List<Slot> slots = (List<Slot>) session.createQuery("from Slot as slot where slot.row.event.id = :id").setParameter("id", id).list();
		
		
		int columnsSize = slots.get(0).getRow().getSlots().size();
		
		log.error("IDS I received: ");
		for(int idxx: ids)
			log.error("Received id: " + idxx);
		
		log.error("IDS in the database: ");
		for(int idxx: Util.getIds(slots))
			log.error("Slot id: " + idxx);
		
		
		if (ids.equals(Util.getIds(slots))) {
			log.error("I passed equal ids test");
			Slot slot;
			Row row;
			Type type;
			for(int i = 0; i < slotIds.length; i++) {
				User owner = User.registerUser(crsids[i]);
				if (owner == null) continue;
				log.error("I passed owner test");
				slot = Util.findById(slots, slotIds[i]);
				slot.setOwner(owner);
				int typeId;
				session.update(slot);
				if ((slot.getRow().getEvent().getTypes().size() != 1) && ((typeId = typeIds[i/columnsSize]) != 0)) {
					type = (Type) session.createQuery("from Type as type where type.id = :type_id AND type.event.id = :id").setParameter("type_id",typeId).setParameter("id", id).uniqueResult();
					row = slot.getRow();
					row.setType(type);
					session.update(row);
				}
			}
		} else {
			// TODO unauthorized access
		}
	}
}