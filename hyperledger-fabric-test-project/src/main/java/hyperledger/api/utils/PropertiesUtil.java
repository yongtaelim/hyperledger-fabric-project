package hyperledger.api.utils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:properties/${dtso}/system.properties")
public class PropertiesUtil {
	private static PropertiesConfiguration config;

	public static PropertiesConfiguration getConfig() {
		return config;
	}
	
	public void setConfig(PropertiesConfiguration config) {
		PropertiesUtil.config = config;
		config.setAutoSave(true);
		
		FileChangedReloadingStrategy reload = new FileChangedReloadingStrategy();
		reload.setRefreshDelay(2000);
		config.setReloadingStrategy(reload);
	}
	
	public Object getProperteis(String key) { 
		return config.getProperty(key);
	}
	
	public static void setProperties(String key, String value) {
		config.setProperty(key, value);
	}
	
	public static void clearProperties(String key) {
		config.clearProperty(key);
	}
	
	public static int getInt(String key) {
		return config.getInt(key);
	}
	
	public static String getString(String key) {
		return config.getString(key);
	}
	
	public static String getString(String key, String defaultValue) {
		return config.getString(key, defaultValue);
	}
}
