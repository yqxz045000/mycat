package com.caiyi.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MycatDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MycatDemoApplication.class, args);
	}
	
	@RequestMapping("/hello.do")
	public String hello(){
		return "hello";
	}
}
