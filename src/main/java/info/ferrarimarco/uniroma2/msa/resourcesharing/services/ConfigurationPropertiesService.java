package info.ferrarimarco.uniroma2.msa.resourcesharing.services;

import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Service;

@Service
public class ConfigurationPropertiesService {
	
	private String propetiesFilePath;
	private Properties properties;
	
	public ConfigurationPropertiesService() throws IOException {
		propetiesFilePath = "MsaResourceSharingCommon.properties";
		
		properties = new Properties();
		properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propetiesFilePath));
	}
	
	public String getHashingAlgorithm() {
		return properties.getProperty("security.hashing.algorithm");
	}
	
	public String getCryptographyProvider() {
		return properties.getProperty("security.provider");
	}

	public String getPropetiesFilePath() {
		return propetiesFilePath;
	}
	
	public String getGcmSender() {
		return properties.getProperty("info.ferrarimarco.msa.resourcesharing.gcm.sender");
	}
	
	public String getGcmServerKey() {
		return properties.getProperty("info.ferrarimarco.msa.resourcesharing.gcm.serverkey");
	}
	
	public String getGcmMaxTtl() {
		return properties.getProperty("info.ferrarimarco.msa.resourcesharing.gcm.maxttl");
	}
}
