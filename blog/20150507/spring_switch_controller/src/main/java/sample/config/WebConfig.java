package sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import sample.mapping.SampleRequestMappingHandlerMapping;

@Configuration
@EnableWebMvc
public class WebConfig {
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		return new SampleRequestMappingHandlerMapping();
	}
}
