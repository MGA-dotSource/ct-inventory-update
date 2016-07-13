/**
 * 
 */
package com.dotsource.ct.productinventory.jobs;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.dotsource.ct.productinventory.jobs.actions.InventoryUpdateCommandGenerationProcessor;
import com.dotsource.ct.productinventory.jobs.actions.InventoryWriter;
import com.dotsource.ct.productinventory.jobs.actions.ProductReader;

import io.sphere.sdk.commands.Command;
import io.sphere.sdk.inventory.InventoryEntry;
import io.sphere.sdk.products.ProductProjection;

/**
 * @author mgatz
 *
 */
@Configuration
@EnableBatchProcessing
public class ProductInventoryUpdateJob {
	
	private static final String STEP_LOAD_CHANNELS = "loadChannels";

	@Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;
	
	@Bean
	@DependsOn("sphereClient")
	public ItemReader<ProductProjection> reader() {
		return new ProductReader();
	}
	
	@Bean
	@DependsOn("sphereClient")
	public ItemProcessor<ProductProjection, List<Command<InventoryEntry>>> processor() {
		return new InventoryUpdateCommandGenerationProcessor();
	}
	
	@Bean
	@DependsOn("sphereClient")
	public ItemWriter<List<Command<InventoryEntry>>> writer() {
		return new InventoryWriter();
	}
	
	@Bean
	public Step loadChannels(ItemReader<ProductProjection> reader, 
			ItemProcessor<ProductProjection, List<Command<InventoryEntry>>> processor,
			ItemWriter<List<Command<InventoryEntry>>> writer) {
		return steps.get(STEP_LOAD_CHANNELS)
				.<ProductProjection, List<Command<InventoryEntry>>> chunk(1)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
	
	@Bean
	public Job createJob(@Qualifier(STEP_LOAD_CHANNELS) Step loadChannels) {
		return jobs.get("productInventoryUpdateJob").start(loadChannels).build();
	}
}
