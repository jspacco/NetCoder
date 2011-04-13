package edu.ycp.cs.netcoder.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.ycp.cs.netcoder.shared.affect.AffectEvent;

@RemoteServiceRelativePath("affectEvent")
public interface AffectEventService extends RemoteService {
	public void recordAffectEvent(AffectEvent affectEvent);
}
