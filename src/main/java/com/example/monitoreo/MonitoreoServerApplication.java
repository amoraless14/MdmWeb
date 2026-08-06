package com.example.monitoreo;
import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = {"Entidad"})
@EnableJpaRepositories(basePackages = {"repository"})
@ComponentScan(basePackages = {
        "com.example.monitoreo",
        "controller",
        "repository",
        "service"
})
public class MonitoreoServerApplication {

    @Bean
    CommandLineRunner test(DataSource dataSource) {
        return args -> {
            try (var con = dataSource.getConnection();
                 var st = con.createStatement();
                 var rs = st.executeQuery(
                         "SELECT current_database(), current_schema(), inet_server_addr(), inet_server_port()")) {

                while (rs.next()) {
                    System.out.println("=================================");
                    System.out.println("DB      = " + rs.getString(1));
                    System.out.println("SCHEMA  = " + rs.getString(2));
                    System.out.println("HOST    = " + rs.getString(3));
                    System.out.println("PORT    = " + rs.getString(4));
                    System.out.println("=================================");
                }
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(MonitoreoServerApplication.class, args);
    }
}