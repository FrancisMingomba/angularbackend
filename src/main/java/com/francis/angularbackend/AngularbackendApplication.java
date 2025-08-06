package com.francis.angularbackend;

import com.francis.angularbackend.entities.User;
import com.francis.angularbackend.repositories.UserRepositories;
import org.flywaydb.core.internal.util.JsonUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AngularbackendApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(AngularbackendApplication.class, args);
		var repository = context.getBean(UserRepositories.class);
		// repository.findAll().forEach(u -> System.out.println(u.getEmail()));
		repository.deleteById(2l);







	}

}
