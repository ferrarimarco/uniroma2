package info.ferrarimarco.uniroma2.sii.heartmonitor.zk.initiators;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.SessionManagerService;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Initiator;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class AuthenticationInitiator implements Initiator {
	
	@WireVariable
	private SessionManagerService sessionManagerService;
	
	@Override
	public void doInit(Page page, Map<String, Object> args) throws Exception {
		
		Selectors.wireVariables(page, this, Selectors.newVariableResolvers(getClass(), null));
		
        User currentUser = sessionManagerService.getAuthenticatedUser();
        
        if(currentUser == null){
            Executions.sendRedirect("/index.zul");
        }
	}
	
}
