package com.example.demo.handleformsubmission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

@Controller
public class GreetingController {

	@Autowired
	private DynamoDBEnhanced dde;
	
	@Autowired
	private PublishTextSMS msg;
	
	
	@GetMapping("/")
	public String greetingForm(Model model) {
		model.addAttribute("greeting", new Greeting());
		
		return "greeting";
	}
	
	
	@PostMapping("/greeting")
	public String greetingsubmit(@ModelAttribute Greeting greeting) {
		
		//persist submitted data into a DynamoDB table using the enhanced client
		dde.injectDynamoItem(greeting);
		
		//send a mobile notification
		msg.sendMessage(greeting.getId());
		
		return "result";
	}
}
