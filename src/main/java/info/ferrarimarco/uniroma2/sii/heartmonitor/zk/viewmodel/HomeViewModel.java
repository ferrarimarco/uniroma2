package info.ferrarimarco.uniroma2.sii.heartmonitor.zk.viewmodel;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.CurrentHeartbeatSession;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.SessionManagerService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.CurrentHeartbeatSessionPerisistenceService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.HeartbeatSessionPersistenceService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class HomeViewModel {
	
	private static Logger logger = LoggerFactory.getLogger(HomeViewModel.class);
	
	@WireVariable
	private HeartbeatSessionPersistenceService heartbeatSessionPersistenceService;
	
	@WireVariable
	private CurrentHeartbeatSessionPerisistenceService currentHeartbeatSessionPerisistenceService;
	
	@WireVariable
	private SessionManagerService sessionManagerService;
	
	private User currentUser;
	private String currentStatus;
	
	private HeartbeatSession selectedHeartbeatSession;
	
	private ListModelList<HeartbeatSession> storedSessions;
	
	private String currentBPM;
	private String currentIBI;
	
	public HomeViewModel() {
		setCurrentStatus("Select a session or start a new session");
	}
	
	@AfterCompose
	public void afterCompose() {
		currentUser = sessionManagerService.getAuthenticatedUser();
		
		refreshHeartbeatSessionList();
	}
	
	public HeartbeatSession getSelectedHeartbeatSession() {
		return selectedHeartbeatSession;
	}

	
	public void setSelectedHeartbeatSession(HeartbeatSession selectedHeartbeatSession) {
		this.selectedHeartbeatSession = selectedHeartbeatSession;
	}

	
	public ListModelList<HeartbeatSession> getStoredSessions() {
		return storedSessions;
	}

	
	public void setStoredSessions(ListModelList<HeartbeatSession> storedSessions) {
		this.storedSessions = storedSessions;
	}

	private void refreshHeartbeatSessionList() {
		List<HeartbeatSession> sessionsForCurrentUser = heartbeatSessionPersistenceService.readHeartbeatSessions(currentUser.getUserName());
		
		storedSessions = new ListModelList<>(sessionsForCurrentUser);
	}
	
	@Command
	public void replaySelectedSession() {
		
	}
	
	@Command
	public void startNewSession() {
		HeartbeatSession session = new HeartbeatSession(currentUser.getUserName());
		
		session = heartbeatSessionPersistenceService.storeHeartbeatSession(session);
		
		currentHeartbeatSessionPerisistenceService.storeCurrentHeartbeatSession(new CurrentHeartbeatSession(currentUser.getUserName(), session.getId()));
	}
	
	@Command
	public void stopCurrentSession() {
		
		CurrentHeartbeatSession currentHeartbeatSession = currentHeartbeatSessionPerisistenceService.readCurrentHeartbeatSession(currentUser.getUserName());
		
		if(currentHeartbeatSession == null) { // the interface is replaying a session
			
		}else { // the interface is currently recording a new session
			HeartbeatSession session = heartbeatSessionPersistenceService.readHeartbeatSession(currentHeartbeatSession.getCurrentSessionId());
			session.setClosed(true);
			
			heartbeatSessionPersistenceService.storeHeartbeatSession(session);
			currentHeartbeatSessionPerisistenceService.deleteCurrentHeartbeatSession(currentHeartbeatSession);
		}
	}
	
	@Command
	public void startHeartbeatValuesFlowDisplay() {
		
//		HeartbeatSessionValue latestValue = sessionManagerService.getLatestHeartbeatSessionValue();
//		
//		while(latestValue == null) {
//			latestValue = sessionManagerService.getLatestHeartbeatSessionValue();
//			
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				logger.error("Error: {}. Stacktrace: {}",e.getMessage(), ExceptionUtils.getStackTrace(e));
//			}
//		}
		
		
	}
	
	@Command
	public void stopHeartbeatValuesFlowDisplay() {
		
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
}
