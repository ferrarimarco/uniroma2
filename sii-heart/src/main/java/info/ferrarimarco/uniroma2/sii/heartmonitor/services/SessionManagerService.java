package info.ferrarimarco.uniroma2.sii.heartmonitor.services;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.SessionParameter;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;

import org.springframework.stereotype.Service;
import org.zkoss.zk.ui.Sessions;

@Service
public class SessionManagerService {
	
	public User getAuthenticatedUser() {
		return (User) Sessions.getCurrent().getAttribute(SessionParameter.CURRENT_USER.toString());
	}
	
	public void saveCurrentAuthenticatedUser(User user) {
		Sessions.getCurrent().setAttribute(SessionParameter.CURRENT_USER.toString(), user);
	}
	
	public void removeCurrentAuthenticatedUser() {
		Sessions.getCurrent().removeAttribute(SessionParameter.CURRENT_USER.toString());
	}
}
