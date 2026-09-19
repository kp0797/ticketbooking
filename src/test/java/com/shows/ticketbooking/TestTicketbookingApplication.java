package com.shows.ticketbooking;

import org.springframework.boot.SpringApplication;

public class TestTicketbookingApplication {

	public static void main(String[] args) {
		SpringApplication.from(TicketbookingApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
