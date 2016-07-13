/**
 * 
 */
package com.dotsource.ct.productinventory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author mgatz
 *
 */
@Component
public class SystemConfiguration {
	
	@Value("${ctp.credentials.clientid}")
	private String clientID;
	@Value("${ctp.credentials.clientsecret}")
	private String clientSecret;
	@Value("${ctp.credentials.projectkey}")
	private String projectKey;
	
	@Value("${inventory.mode}")
	private String inventoryMode;
	@Value("${inventory.mode.fixed.amount}")
	private Integer inventoryFixedAmount;
	@Value("${inventory.mode.random.min}")
	private Integer inventoryRandomMin;
	@Value("${inventory.mode.random.max}")
	private Integer inventoryRandomMax;
	
	/**
	 * @return the clientID
	 */
	public String getClientID() {
		return clientID;
	}
	/**
	 * @return the clientSecret
	 */
	public String getClientSecret() {
		return clientSecret;
	}
	/**
	 * @return the projectKey
	 */
	public String getProjectKey() {
		return projectKey;
	}
	/**
	 * @return the inventoryMode
	 */
	public String getInventoryMode() {
		return inventoryMode;
	}
	/**
	 * @return the inventoryFixedAmount
	 */
	public Integer getInventoryFixedAmount() {
		return inventoryFixedAmount;
	}
	/**
	 * @return the inventoryRandomMin
	 */
	public Integer getInventoryRandomMin() {
		return inventoryRandomMin;
	}
	/**
	 * @return the inventoryRandomMax
	 */
	public Integer getInventoryRandomMax() {
		return inventoryRandomMax;
	}
	
	
}
