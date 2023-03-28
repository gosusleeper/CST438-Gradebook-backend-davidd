package com.cst438.services;


import java.util.ArrayList;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;


public class RegistrationServiceMQ extends RegistrationService {

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}

	// ----- configuration of message queues

	@Autowired
	Queue registrationQueue;


	// ----- end of configuration of message queue
	

	// receiver of messages from Registration service
	//enrollmentController.java and registrationservice rest
	
	//new code below
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(EnrollmentDTO enrollmentDTO) {
		
		System.out.println("Receive enrollment :" + enrollmentDTO);
		
		//get message from q, get enrollment entity and save to grade book database asynchronous
		
		//go through enrollment
		Enrollment e = new Enrollment();
			//set student email
			e.setStudentEmail(enrollmentDTO.studentEmail);
			//set student name
			e.setStudentName(enrollmentDTO.studentName);
			//get course
			Course c = courseRepository.findById(enrollmentDTO.course_id).orElse(null);
			if (c==null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course id not found.");
			}
			//set course to enrollment
			e.setCourse(c);
			//save enrollment in repository
			e = enrollmentRepository.save(e);
		}
		
	

	// sender of messages to Registration Service
	@Override
	public void sendFinalGrades(int course_id, CourseDTOG courseDTO) {
		 
		//String course_id_string = Integer.toString(course_id);
		//EnrollmentDTO enrollmentDTO = new EnrollmentDTO(courseDTO., courseDTO.grades, course_id);

		//get course based on id
		Course c = courseRepository.findById(course_id).orElse(null);
		
		//set course based on provided DTO
		CourseDTOG cdto = courseDTO;
		cdto.course_id = course_id;
		cdto.grades = new ArrayList<>();
		CourseDTOG.GradeDTO gdto = new CourseDTOG.GradeDTO();
		//scroll through enrollments
		for (Enrollment e1: c.getEnrollments()) {
			double total=0.0;
			int count = 0;
			//get grades from the assignment
			for (AssignmentGrade ag : e1.getAssignmentGrades()) {
				count++;
				//get total points
				total = total + Double.parseDouble(ag.getScore());
			}
			//calculate average
			double average = total/count;
			gdto.grade=letterGrade(average);
			gdto.student_email=e1.getStudentEmail();
			gdto.student_name=e1.getStudentName();
			//cxve grade
			cdto.grades.add(gdto);
			
			
			System.out.println("Course="+course_id+" Student="+e1.getStudentEmail()+" grade="+gdto.grade);
		}

		//send to gradebookserviceMQ
		rabbitTemplate.convertAndSend(registrationQueue.getName(), courseDTO);
		
		System.out.println("Message send to registration service for courseDTO "+ courseDTO); 
		
	}
	
	//function to get letter grade
	private String letterGrade(double grade) {
		if (grade >= 90) return "A";
		if (grade >= 80) return "B";
		if (grade >= 70) return "C";
		if (grade >= 60) return "D";
		return "F";
	}	
	
	//new code end
	
	}

