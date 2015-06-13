package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.LocationMe;
import org.jivesoftware.smack.packet.LocationMe.Type;
import org.jivesoftware.smack.packet.Packet;

public class LocationMeTypeFilter implements PacketFilter{	
	
    private final LocationMe.Type type;

    /*public LocationMeTypeFilter(Message.Type type) {
        this.type = type;
    }*/
    public LocationMeTypeFilter(Type type) {
		// TODO Auto-generated constructor stub
    	this.type = type;
	}
	public boolean accept(Packet packet) {
        if (!(packet instanceof LocationMe)) {
            return false;
        }
        else {
            return ((LocationMe) packet).getType().equals(this.type);
        }
    }
}
