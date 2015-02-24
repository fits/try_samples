package sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /*
         * 直接 BeforeInterceptor をインスタンス化すると @Autowired が処理されないので
         * @Bean を付与したメソッド経由でインスタンスを取得する
         */
        registry.addInterceptor(beforeInterceptor());
    }

    @Bean
    public BeforeInterceptor beforeInterceptor() {
        return new BeforeInterceptor();
    }
}
