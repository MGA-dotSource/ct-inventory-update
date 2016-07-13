/**
 * 
 */
package com.dotsource.ct.productinventory.jobs.actions;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.commands.Command;
import io.sphere.sdk.inventory.InventoryEntry;

/**
 * @author mgatz
 *
 */
public class InventoryWriter implements ItemWriter<List<Command<InventoryEntry>>> {

	@Autowired
	private SphereClient client;
	
	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends List<Command<InventoryEntry>>> items) throws Exception {
		if(items.size() > 0) {
			items.get(0).forEach(i -> {
				client.execute(i);
			});			
		}
	}
}
