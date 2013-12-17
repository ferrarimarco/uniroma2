package info.ferrarimarco.uniroma2.sii.heartmonitor.zk.viewmodel;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSessionValue;
import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.SessionManagerService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.persistence.HeartbeatSessionPersistenceService;

import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class HomeViewModel {
	
	private static Logger logger = LoggerFactory.getLogger(HomeViewModel.class);;
	
	@WireVariable
	private HeartbeatSessionPersistenceService heartbeatSessionPersistenceService;
	
	@WireVariable
	private SessionManagerService sessionManagerService;
	
	private User currentUser;
	
	private HeartbeatSession selectedHeartbeatSession;
	
	private List<HeartbeatSession> storedSessions;
	
	@AfterCompose
	public void afterCompose() {
		currentUser = sessionManagerService.getAuthenticatedUser();
		
		refreshHeartbeatSessionList();
	}
	
	private void refreshHeartbeatSessionList() {
		storedSessions = heartbeatSessionPersistenceService.readHeartbeatSessions(currentUser.getUserName());
	}
	
	@Command
	public void replaySelectedSession() {
		
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
}
