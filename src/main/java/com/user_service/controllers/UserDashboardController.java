package com.user_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userdashboard")
public class UserDashboardController {
	
	
	@GetMapping("/open")
	public String openUserDashboard() {
		System.out.println("user authenticated using jwt token ");
		System.out.println("inside user dashboard");
		
		return "welcome to user dashboard";
		
		
	}

}
