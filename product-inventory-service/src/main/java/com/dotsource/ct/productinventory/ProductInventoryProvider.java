/**
 * 
 */
package com.dotsource.ct.productinventory;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author mgatz
 *
 */
public class ProductInventoryProvider {
	@Autowired
	private SystemConfiguration config;
	
	public long calculateNewQuantity() {
		if("fixed".equals(config.getInventoryMode())) {
			return config.getInventoryFixedAmount();
		}
		// else random
		return new Random().nextInt(config.getInventoryRandomMax() + 1) + config.getInventoryRandomMin();
	}
}
