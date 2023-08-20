package com.staroot.pingcheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄링 활성화
@EnableAsync
public class PingcheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(PingcheckApplication.class, args);
	}

}
