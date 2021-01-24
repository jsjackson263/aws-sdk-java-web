package com.example.demo.handleformsubmission;

import org.springframework.stereotype.Component;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;

@Component("DynamoDBEnhanced")
public class DynamoDBEnhanced {
	
	private final ProvisionedThroughput DEFAULT_PROVISIONED_THROUGHPUT = 
			ProvisionedThroughput.builder()
			.readCapacityUnits(50L)
			.writeCapacityUnits(50L)
			.build();

	private final TableSchema<GreetingItem> TABLE_SCHEMA = 
		StaticTableSchema.builder(GreetingItem.class)
		.newItemSupplier(GreetingItem::new)
		.addAttribute(String.class, a -> a.name("idblog")
				.getter(GreetingItem::getId)
				.setter(GreetingItem::setId)
				.tags(primaryPartitionKey()))
		.addAttribute(String.class, a -> a.name("author")
				.getter(GreetingItem::getName)
				.setter(GreetingItem::setName))
		.addAttribute(String.class, a -> a.name("title")
				.getter(GreetingItem::getTitle)
				.setter(GreetingItem::setTitle))
		.addAttribute(String.class, a -> a.name("body")
				.getter(GreetingItem::getMessage)
				.setter(GreetingItem::setMessage))
		.build();
	
	//uses the enhanced client to inject a new post into a DynamoDB table
	public void injectDynamoItem(Greeting item) {
		Region region = Region.AP_SOUTHEAST_2; 
		DynamoDbClient ddb = DynamoDbClient.builder()
				.region(region)
				.credentialsProvider(EnvironmentVariableCredentialsProvider.create())
				.build();
		
		try {
			
			DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
					.dynamoDbClient(ddb)
					.build();
			
			//create a DynamoDbTable object
			DynamoDbTable<GreetingItem> mappedTable = enhancedClient.table("Greeting", TABLE_SCHEMA);
			GreetingItem greetingItem = new GreetingItem();
			greetingItem.setName(item.getName());
			greetingItem.setMessage(item.getBody());
			greetingItem.setTitle(item.getTitle());
			greetingItem.setId(item.getId());
			
			PutItemEnhancedRequest enhancedRequest = PutItemEnhancedRequest.builder(GreetingItem.class)
					.item(greetingItem)
					.build();
			
			mappedTable.putItem(enhancedRequest);

		} catch (Exception e) {
			e.getStackTrace();
		}
				
		
	}
			
			
	public class GreetingItem {
		//set up data members that correspond to columns in the Work table
		private String id;
		private String name;
		private String message;
		private String title;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
	}
}
