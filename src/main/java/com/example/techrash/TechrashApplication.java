package com.example.techrash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TechrashApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechrashApplication.class, args);
		Solution solution = new Solution();
		solution.generateCSVFileFromWeb();

		System.out.println("Completed");
	}

}
