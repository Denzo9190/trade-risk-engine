package com.denzo.traderisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TradeRiskEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeRiskEngineApplication.class, args);
	}
}
