package com.app;

import com.app.DAL.SongDAL;
import com.app.models.IndexedSong;
import com.app.models.KeyPoint;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
               
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
        	
        	log.info("Hello, world.");
        	         	
        	/*
            System.out.println("Let's inspect the beans provided by Spring Boot:");        
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
             for (String beanName : beanNames) {
                 System.out.println(beanName);
             
             }
            */
                  
        };
    }

}