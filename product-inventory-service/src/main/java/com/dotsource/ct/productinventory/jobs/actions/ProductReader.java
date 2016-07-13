/**
 * 
 */
package com.dotsource.ct.productinventory.jobs.actions;

import java.time.Duration;

import javax.annotation.PostConstruct;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.queries.PagedQueryResult;

/**
 * @author mgatz
 *
 */
public class ProductReader implements ItemReader<ProductProjection> {

	@Autowired
	private SphereClient client;
	
	private int lastPosition = 0;

	private BlockingSphereClient bClient;
	
	@PostConstruct
	public void postConstruct() {
		bClient = BlockingSphereClient.of(client, Duration.ofMillis(750));
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemReader#read()
	 */
	@Override
	public ProductProjection read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		ProductProjectionQuery q = ProductProjectionQuery
				.ofCurrent()
				.withOffset(lastPosition++).withLimit(1)
				.plusSort(m -> m.createdAt().sort().asc());
		
		PagedQueryResult<ProductProjection> result = bClient.executeBlocking(q);
		
		return result.getResults().size() == 1 ? result.getResults().get(0) : null;
	}
}
