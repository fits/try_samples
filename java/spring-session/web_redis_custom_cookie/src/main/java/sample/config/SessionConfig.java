package sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SessionConfig {
    @Bean
    CookieSerializer cookieSerializer() {
        return new MultiCookieSerializer();
    }

    private class MultiCookieSerializer implements CookieSerializer {
        private ConcurrentHashMap<String, CookieSerializer> mapping = new ConcurrentHashMap<>();

        @Override
        public void writeCookieValue(CookieValue cookieValue) {
            getSerializer(cookieValue.getRequest()).writeCookieValue(cookieValue);
        }

        @Override
        public List<String> readCookieValues(HttpServletRequest request) {
            return getSerializer(request).readCookieValues(request);
        }

        private CookieSerializer getSerializer(HttpServletRequest request) {
            var name = createCookieName(request);

            return mapping.computeIfAbsent(name, n -> {
                var res = new DefaultCookieSerializer();
                res.setCookieName(n);
                return res;
            });
        }

        private String createCookieName(HttpServletRequest request) {
            return request.getServerName() + "-SID";
        }
    }
}
