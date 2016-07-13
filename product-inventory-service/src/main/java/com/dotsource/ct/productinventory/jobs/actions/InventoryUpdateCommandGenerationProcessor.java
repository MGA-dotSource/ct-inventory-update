/**
 * 
 */
package com.dotsource.ct.productinventory.jobs.actions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.dotsource.ct.productinventory.ProductInventoryProvider;

import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.channels.queries.ChannelQuery;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.commands.Command;
import io.sphere.sdk.inventory.InventoryEntry;
import io.sphere.sdk.inventory.InventoryEntryDraft;
import io.sphere.sdk.inventory.commands.InventoryEntryCreateCommand;
import io.sphere.sdk.inventory.commands.InventoryEntryUpdateCommand;
import io.sphere.sdk.inventory.commands.updateactions.ChangeQuantity;
import io.sphere.sdk.inventory.queries.InventoryEntryQuery;
import io.sphere.sdk.inventory.queries.InventoryEntryQueryBuilder;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.queries.PagedQueryResult;

/**
 * @author mgatz
 *
 */
public class InventoryUpdateCommandGenerationProcessor 
		implements ItemProcessor<ProductProjection, List<Command<InventoryEntry>>> {

	@Autowired
	private SphereClient client;
	private BlockingSphereClient bClient;
	@Autowired
	private ProductInventoryProvider invProvider;
	
	private List<Channel> channels = new ArrayList<>();
	
	private final ChannelQuery channelQuery = ChannelQuery.of().withSort(cqm -> cqm.createdAt().sort().asc());
	
	@PostConstruct
	public void postConstruct() {
		bClient = BlockingSphereClient.of(client, Duration.ofMillis(2000));
		this.channels = bClient.executeBlocking(channelQuery).getResults();
	}
	
	
	@Override
	public List<Command<InventoryEntry>> process(ProductProjection item) throws Exception {
		
		List<Command<InventoryEntry>> actions = new ArrayList<>();
		
		if(channels.size() > 0) {
			channels.forEach(c -> {
				item.getAllVariants().forEach(v -> {
					InventoryEntry current = getCurrentInventory(v.getSku(), c);
					if(null == current) {
						actions.add(InventoryEntryCreateCommand.of(InventoryEntryDraft.of(v.getSku(), invProvider.calculateNewQuantity())));
					}
					else {
						actions.add(InventoryEntryUpdateCommand.of(current, ChangeQuantity.of(invProvider.calculateNewQuantity()))); 				
					}
				});
			});
		}
		else {
			// no channels -> simple add one draft object per variant
			item.getAllVariants().forEach(v -> {
				InventoryEntry current = getCurrentInventory(v.getSku(), null);
				if(null == current) {
					actions.add(InventoryEntryCreateCommand.of(InventoryEntryDraft.of(v.getSku(), invProvider.calculateNewQuantity())));
				}
				else {
					actions.add(InventoryEntryUpdateCommand.of(current, ChangeQuantity.of(invProvider.calculateNewQuantity()))); 				
				}		
			});
		}
		
		return actions;
	}
	
	private InventoryEntry getCurrentInventory(String sku, Channel channel) {
		
		InventoryEntryQuery q = null;
		
		if(null != channel) {
			q = InventoryEntryQueryBuilder.of().plusPredicates(m -> m.sku().is(sku).and(m.supplyChannel().is(channel))).build();
		}
		else {
			q = InventoryEntryQueryBuilder.of().plusPredicates(m -> m.sku().is(sku)).build();
		}
		
		PagedQueryResult<InventoryEntry> res = bClient.executeBlocking(q);
		
		return res.getResults().stream().findFirst().orElse(null);
	}
}
