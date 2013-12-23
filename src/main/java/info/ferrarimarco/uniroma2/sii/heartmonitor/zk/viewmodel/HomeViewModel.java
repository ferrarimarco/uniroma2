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
import org.zkoss.zul.Messagebox;
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
	
	private ListModelList<HeartbeatSession> storedSessions;
	private HeartbeatSession selectedSession;
	
	private String currentSessionId;
	private int sequenceNumber;
	private int currentBPM;
	private int currentIBI;
	
	private static final int sleepTimeIncrement = 100;
	private static final int maxSleepTime = 700;
	
	private boolean disableStartSessionButton;
	
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
		initInterfaceFlags();
	}
	
	private void initInterfaceFlags() {
		disableStartSessionButton = false;
	}
	
	private void clearHeartbeatSession() {
		currentSessionId = null;
		sequenceNumber = 0;
		currentBPM = 0;
		currentIBI = 0;
		
		currentHeartbeatSessionPerisistenceService.deleteCurrentHeartbeatSession(currentUser.getUserName());
		heartbeatSessionValuePersistenceService.deleteAllHeartbeatSessionValuesByReferringUserName(currentUser.getUserName());
	}
	
	private void updateHeartbeatSessionValues(HeartbeatSessionValue heartbeatSessionValue) {
		
		currentBPM = heartbeatSessionValue.getBpm();
		currentIBI = heartbeatSessionValue.getIbi();
		
		// Increase sequence number to read the next value
		sequenceNumber = heartbeatSessionValue.getSequenceNumber() + 1;
	}
	
	@Command
	@NotifyChange("storedSessions")
	public void refreshHeartbeatSessionList() {
		List<HeartbeatSession> sessionsForCurrentUser = heartbeatSessionPersistenceService.readClosedHeartbeatSessions(currentUser.getUserName());
		
		storedSessions = new ListModelList<>(sessionsForCurrentUser);
	}
	
	@Command
	@NotifyChange({"currentBPM","currentIBI","currentStatus","disableStartSessionButton","disableStopSessionButton","disableSessionGrid","disableReplaySessionButton","disableDeleteSessionButton"})
	public void replaySelectedSession() {
		
		initNewSession(selectedSession);
		
		// load session values from repo
		for(HeartbeatSessionValue value : selectedSession.getValues()) {
			heartbeatSessionValuePersistenceService.storeHeartbeatSessionValue(value);
		}
		
		currentStatus = "Replaying session. ID: " + currentSessionId;
	}
	
	@Command
	@NotifyChange({"currentBPM","currentIBI","currentStatus","disableStartSessionButton","disableStopSessionButton","disableSessionGrid","disableReplaySessionButton","disableDeleteSessionButton"})
	public void startNewSession() {
		
		HeartbeatSession session = new HeartbeatSession(currentUser.getUserName());
		session = heartbeatSessionPersistenceService.storeHeartbeatSession(session);
		
		initNewSession(session);
		
		currentHeartbeatSessionPerisistenceService.storeCurrentHeartbeatSession(new CurrentHeartbeatSession(currentUser.getUserName(), currentSessionId));
		
		currentStatus = "Recording a new session. ID: " + currentSessionId;
		
		initTimer();
	}
	
	private void initNewSession(HeartbeatSession session) {
		clearHeartbeatSession();
		
		disableStartSessionButton = true;
		
		currentSessionId = session.getId();
	}
	
	private void initTimer() {
		timer.setDelay(sleepTimeIncrement);
		timer.start();
	}
	
	@Command
	@NotifyChange({"currentBPM","currentIBI","currentStatus","storedSessions","disableStartSessionButton","disableStopSessionButton","disableSessionGrid","disableReplaySessionButton","disableDeleteSessionButton"})
	public void stopCurrentSession() {
		
		CurrentHeartbeatSession currentHeartbeatSession = currentHeartbeatSessionPerisistenceService.readCurrentHeartbeatSession(currentUser.getUserName());
		
		if(currentHeartbeatSession != null) { // the interface is currently recording a new session
			HeartbeatSession session = heartbeatSessionPersistenceService.readHeartbeatSession(currentHeartbeatSession.getCurrentSessionId());
			session.setClosed(true);
			session = heartbeatSessionPersistenceService.storeHeartbeatSession(session);

			logger.info("Closed session {}", session.getId());
			refreshHeartbeatSessionList();
		}
		
		clearHeartbeatSession();
		
		initInterfaceFlags();
		
		currentStatus = idleStatusLabel;
		timer.stop();
	}
	
	@Command
	@NotifyChange({"currentBPM","currentIBI","currentStatus","storedSessions","disableStartSessionButton","disableStopSessionButton","disableSessionGrid","disableReplaySessionButton","disableDeleteSessionButton"})
	public void heartbeatValuesFlowDisplay() {
		
		if (timer.isRunning()) {
			
			logger.info("Looking for value {} of session {}", sequenceNumber, currentSessionId);
			
			HeartbeatSessionValue latestValue = heartbeatSessionValuePersistenceService.readHeartbeatSessionValue(currentSessionId + sequenceNumber);
			
			if (latestValue != null) {
				timer.setDelay(latestValue.getIbi());
				heartbeatSessionValuePersistenceService.deleteHeartbeatSessionValue(currentSessionId + sequenceNumber);
				updateHeartbeatSessionValues(latestValue);
			} else {
				logger.info("Value {} of session {} not found", sequenceNumber, currentSessionId);
				
				timer.setDelay(timer.getDelay() + sleepTimeIncrement);

				if (timer.getDelay() > maxSleepTime) {
					timer.stop();
					logger.info("Stopped timer (delay too high)", sequenceNumber, currentSessionId);
				}
			}
			
			// this checks if the timer has been stopped because sleep time is too high
			if (!timer.isRunning() && timer.getDelay() > maxSleepTime) {
				stopCurrentSession();
				Messagebox.show("Current session has been stopped since no values have been received in " + maxSleepTime + " ms", "Session closed", 0, Messagebox.INFORMATION);
			}
		}
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
	
	public ListModelList<HeartbeatSession> getStoredSessions() {
		return storedSessions;
	}

	
	public void setStoredSessions(ListModelList<HeartbeatSession> storedSessions) {
		this.storedSessions = storedSessions;
	}

	
	public boolean isDisableStartSessionButton() {
		return disableStartSessionButton;
	}

	
	public void setDisableStartSessionButton(boolean disableStartSessionButton) {
		this.disableStartSessionButton = disableStartSessionButton;
	}

	
	public boolean isDisableStopSessionButton() {
		return !isDisableStartSessionButton();
	}

	
	public boolean isDisableReplaySessionButton() {
		return isDisableStartSessionButton();
	}
	
	public boolean isDisableDeleteSessionButton() {
		return isDisableStartSessionButton();
	}
	
	public boolean isDisableSessionGrid() {
		return isDisableStartSessionButton();
	}

	/**
	 * @return the selectedSession
	 */
	public HeartbeatSession getSelectedSession() {
		return selectedSession;
	}

	/**
	 * @param selectedSession the selectedSession to set
	 */
	public void setSelectedSession(HeartbeatSession selectedSession) {
		this.selectedSession = selectedSession;
	}
}
