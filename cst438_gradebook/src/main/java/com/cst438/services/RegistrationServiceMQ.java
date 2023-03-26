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
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(EnrollmentDTO enrollmentDTO) {
		
		System.out.println("Receive enrollment :" + enrollmentDTO);
		//TODO  complete this method in homework 4
		
		//get message from q, get enrollment entity and save to grade book database asynchronous
		
		//EnrollmentDTO e = enrollmentDTO;
		Enrollment e = new Enrollment();
			e.setStudentEmail(enrollmentDTO.studentEmail);
			e.setStudentName(enrollmentDTO.studentName);
			Course c = courseRepository.findById(enrollmentDTO.course_id).orElse(null);
			if (c==null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course id not found.");
			}
			e.setCourse(c);
			e = enrollmentRepository.save(e);
		}
		
	

	// sender of messages to Registration Service
	@Override
	public void sendFinalGrades(int course_id, CourseDTOG courseDTO) {
		 
		//TODO  complete this method in homework 4

		//String course_id_string = Integer.toString(course_id);
		//EnrollmentDTO enrollmentDTO = new EnrollmentDTO(courseDTO., courseDTO.grades, course_id);

		Course c = courseRepository.findById(course_id).orElse(null);
		
		CourseDTOG cdto = courseDTO;
		cdto.course_id = course_id;
		cdto.grades = new ArrayList<>();
		CourseDTOG.GradeDTO gdto = new CourseDTOG.GradeDTO();
		for (Enrollment e1: c.getEnrollments()) {
			double total=0.0;
			int count = 0;
			for (AssignmentGrade ag : e1.getAssignmentGrades()) {
				count++;
				total = total + Double.parseDouble(ag.getScore());
			}
			double average = total/count;
			gdto.grade=letterGrade(average);
			gdto.student_email=e1.getStudentEmail();
			gdto.student_name=e1.getStudentName();
			cdto.grades.add(gdto);
			
			
			System.out.println("Course="+course_id+" Student="+e1.getStudentEmail()+" grade="+gdto.grade);
		}

		
		rabbitTemplate.convertAndSend(registrationQueue.getName(), courseDTO);
		
		System.out.println("Message send to registration service for courseDTO "+ courseDTO); 
		
	}
	
	
	private String letterGrade(double grade) {
		if (grade >= 90) return "A";
		if (grade >= 80) return "B";
		if (grade >= 70) return "C";
		if (grade >= 60) return "D";
		return "F";
	}	
	
	
	
	}

