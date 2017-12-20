package net.orfdev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class is what kicks off our Spring Boot webserver etc.
 * 
 * You don't need to change anything in this class, or worry much about what it does.
 * 
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
