package info.ferrarimarco.uniroma2.sii.heartmonitor.zk.viewmodel;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.User;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.SessionManagerService;
import info.ferrarimarco.uniroma2.sii.heartmonitor.services.authentication.UserAuthenticationService;

import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class IndexViewModel {

	private String userName;
	private String password;
	
	@WireVariable
	private UserAuthenticationService userAuthenticationService;
	
	@WireVariable
	private SessionManagerService sessionManagerService;
	
	public IndexViewModel() {
		userName = "";
		password = "";
	}
	
	@Command
	public void login() {
		
		User authenticatedUser = userAuthenticationService.login(userName, password, true);
		
		if(authenticatedUser != null) {
			Executions.sendRedirect("/secure/home.zul");
		}else {
			Messagebox.show("Login failed: wrong username/password combination", "Login Failed error", 0, Messagebox.ERROR);
		}
	}
}
