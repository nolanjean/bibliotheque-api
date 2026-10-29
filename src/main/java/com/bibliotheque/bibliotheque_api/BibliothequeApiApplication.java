package com.bibliotheque.bibliotheque_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;


@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
public class BibliothequeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BibliothequeApiApplication.class, args);
	}

	@Bean
	public PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
		return resolver -> resolver.setMaxPageSize(100);
	}

}