package info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

@Service
public class SecurityProviderManagerService {
	
	public SecurityProviderManagerService(){
		Security.addProvider(new BouncyCastleProvider());
	}	
}
