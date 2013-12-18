package info.ferrarimarco.uniroma2.sii.heartmonitor.zk.viewmodel;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.CurrentHeartbeatSession;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSessionValue;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.SessionManagerService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.CurrentHeartbeatSessionPerisistenceService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.HeartbeatSessionPersistenceService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.HeartbeatSessionValuePersistenceService;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Timer;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class HomeViewModel {
	
	private static Logger logger = LoggerFactory.getLogger(HomeViewModel.class);
	
	@WireVariable
	private HeartbeatSessionPersistenceService heartbeatSessionPersistenceService;
	
	@WireVariable
	private CurrentHeartbeatSessionPerisistenceService currentHeartbeatSessionPerisistenceService;
	
	@WireVariable
	private HeartbeatSessionValuePersistenceService heartbeatSessionValuePersistenceService;
	
	@WireVariable
	private SessionManagerService sessionManagerService;
	
	@Wire
	private Timer timer;
	
	private User currentUser;
	private String currentStatus;
	
	private HeartbeatSession selectedHeartbeatSession;
	
	private ListModelList<HeartbeatSession> storedSessions;
	
	private String currentSessionId;
	private int sequenceNumber;
	private int currentBPM;
	private int currentIBI;
	
	private static final String idleStatusLabel = "Select a session to replay or start a new session";
	
	public HomeViewModel() {
		currentStatus = idleStatusLabel;
	}
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		
		Selectors.wireComponents(view, this, false);
		
		timer.stop();
		
		currentUser = sessionManagerService.getAuthenticatedUser();
		
		refreshHeartbeatSessionList();
		clearHeartbeatSession();
	}
	
	private void clearHeartbeatSession() {
		currentSessionId = null;
		sequenceNumber = 0;
		currentBPM = 0;
		currentIBI = 0;
		
		currentHeartbeatSessionPerisistenceService.deleteAll();
		heartbeatSessionValuePersistenceService.deleteAll();
	}
	
	private void updateHeartbeatSessionValues(HeartbeatSessionValue heartbeatSessionValue) {
		
		currentBPM = heartbeatSessionValue.getBpm();
		currentIBI = heartbeatSessionValue.getIbi();
		
		// Increase sequence number to read the next value
		sequenceNumber = heartbeatSessionValue.getSequenceNumber() + 1;
	}

	private void refreshHeartbeatSessionList() {
		List<HeartbeatSession> sessionsForCurrentUser = heartbeatSessionPersistenceService.readHeartbeatSessions(currentUser.getUserName());
		
		storedSessions = new ListModelList<>(sessionsForCurrentUser);
	}
	
	@Command
	public void replaySelectedSession() {
		
	}
	
	@Command
	@NotifyChange({"currentBPM","currentIBI","currentStatus","isSessionStarted"})
	public void startNewSession() {
		
		clearHeartbeatSession();
		
		HeartbeatSession session = new HeartbeatSession(currentUser.getUserName());
		
		session = heartbeatSessionPersistenceService.storeHeartbeatSession(session);
		
		currentSessionId = session.getId();
		
		currentHeartbeatSessionPerisistenceService.storeCurrentHeartbeatSession(new CurrentHeartbeatSession(currentUser.getUserName(), currentSessionId));
		
		currentStatus = "Recording a new session. ID: " + currentSessionId;
		
		timer.start();
	}
	
	@Command
	@NotifyChange({"currentBPM","currentIBI","currentStatus","isSessionStarted"})
	public void stopCurrentSession() {
		
		CurrentHeartbeatSession currentHeartbeatSession = currentHeartbeatSessionPerisistenceService.readCurrentHeartbeatSession(currentUser.getUserName());
		
		if(currentHeartbeatSession == null) { // the interface is replaying a session
			
		}else { // the interface is currently recording a new session
			HeartbeatSession session = heartbeatSessionPersistenceService.readHeartbeatSession(currentHeartbeatSession.getCurrentSessionId());
			session.setClosed(true);
			
			heartbeatSessionPersistenceService.storeHeartbeatSession(session);
			currentHeartbeatSessionPerisistenceService.deleteCurrentHeartbeatSession(currentHeartbeatSession);
			heartbeatSessionValuePersistenceService.deleteAll();
			
			clearHeartbeatSession();
		}
		
		currentStatus = idleStatusLabel;
		timer.stop();
	}
	
	@Command
	@NotifyChange({"currentBPM","currentIBI"})
	public void heartbeatValuesFlowDisplay() {
		
		HeartbeatSessionValue latestValue = null;
		while (latestValue == null) {
			latestValue = heartbeatSessionValuePersistenceService.readHeartbeatSessionValue(sequenceNumber);

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				logger.error("Error: {}. Stacktrace: {}", e.getMessage(), ExceptionUtils.getStackTrace(e));
			}
		}
		heartbeatSessionValuePersistenceService.deleteHeartbeatSessionValue(sequenceNumber);
		updateHeartbeatSessionValues(latestValue);
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	
	public int getCurrentBPM() {
		return currentBPM;
	}

	
	public void setCurrentBPM(int currentBPM) {
		this.currentBPM = currentBPM;
	}

	
	public int getCurrentIBI() {
		return currentIBI;
	}

	
	public void setCurrentIBI(int currentIBI) {
		this.currentIBI = currentIBI;
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
}
