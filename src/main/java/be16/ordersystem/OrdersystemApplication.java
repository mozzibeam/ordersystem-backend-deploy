package be16.ordersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OrdersystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersystemApplication.class, args);
	}

}
