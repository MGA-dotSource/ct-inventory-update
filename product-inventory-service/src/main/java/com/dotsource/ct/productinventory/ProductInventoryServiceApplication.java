package com.dotsource.ct.productinventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientFactory;

@SpringBootApplication
public class ProductInventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductInventoryServiceApplication.class, args);
	}
	
	@Bean(destroyMethod = "close")
	@DependsOn("systemConfiguration")
	public SphereClient sphereClient(SystemConfiguration config) {
		return SphereClientFactory.of().createClient(config.getProjectKey(), config.getClientID(), config.getClientSecret());
	}
	
	@Bean
	public ProductInventoryProvider productInventoryProvider() {
		return new ProductInventoryProvider();
	}
}
