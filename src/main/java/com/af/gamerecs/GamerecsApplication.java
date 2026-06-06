package com.af.gamerecs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.af.gamerecs.config.RawgProperties;


@EnableConfigurationProperties(RawgProperties.class)
@SpringBootApplication
public class GamerecsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamerecsApplication.class, args);
	}

}
