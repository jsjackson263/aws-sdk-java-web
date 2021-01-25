package com.example.demo.handleformsubmission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;

@Component("PublishTextSMS")
public class PublishTextSMS {
	
	private static final Logger log = LoggerFactory.getLogger(PublishTextSMS.class);

	public void sendMessage(String id) {
		
		Region region = Region.AP_SOUTHEAST_2;
		
		SnsClient snsClient = SnsClient.builder()
				.region(region)
				.credentialsProvider(EnvironmentVariableCredentialsProvider.create())
				.build();
		
		String message = "A new item with ID value " + id + " was added to the DynamoDB table";
		String phoneNumber = System.getenv("PHONE_NUMBER"); // "<ENTER MOBILE PHONE NUMBER>"; //replace with a mobile phone number
		
		try {
			
			PublishRequest request = PublishRequest.builder()
					.message(message)
					.phoneNumber(phoneNumber)
					.build();
			
			PublishResponse result = snsClient.publish(request);
			log.info(message);
			log.info("SMS Message sent to phone number {} with result {} {}", phoneNumber, result.sdkHttpResponse().statusCode(), result.sdkHttpResponse().statusText());
					
		} catch (SnsException e) {
			log.info("There was an error sending notification message. Error details: {} ", e.awsErrorDetails().errorMessage());
			System.err.println(e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		
	}
}
