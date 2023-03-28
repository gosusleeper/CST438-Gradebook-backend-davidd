package com.cst438.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.CourseDTOG;

public class RegistrationServiceREST extends RegistrationService {

	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}") 
	String registration_url;
	
	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}
	
	//NEW CODE BELOLW
	@Override
	public void sendFinalGrades(int course_id , CourseDTOG courseDTO) { 
		

		//print out debug
		System.out.println("Sending final grades "+course_id+" "+courseDTO);
		//run send final grades
		restTemplate.put(registration_url+"/course/"+course_id, courseDTO);
		//confirm debug
		System.out.println("After sending final grades");
	}
	//new code end
}
 
