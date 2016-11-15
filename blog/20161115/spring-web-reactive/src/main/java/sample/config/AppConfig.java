package sample.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.config.EnableWebReactive;

@EnableWebReactive
@ComponentScan("sample.controller")
public class AppConfig {
}
