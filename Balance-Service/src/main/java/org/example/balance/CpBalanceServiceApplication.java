package org.example.balance;

import org.example.rabbitMq.RabbitMqConfig;
import org.example.webSocket.WebSocketConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "org.example.balance")

@EnableScheduling

@Import(value = {
        org.example.kafka.ProducerConfigKafka.class,
        org.example.kafka.ConsumerConfigKafka.class,
        org.example.kafka.DltConfig.class,
        RabbitMqConfig.class,
        org.example.security.SecurityConfig.class,
        WebSocketConfig.class
})

public class CpBalanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CpBalanceServiceApplication.class, args);
    }

}
