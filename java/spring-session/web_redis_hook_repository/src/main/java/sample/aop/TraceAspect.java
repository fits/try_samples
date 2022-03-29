package sample.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Aspect
public class TraceAspect {
    @Before("call(* org.springframework.session.SessionRepository.*(..)) && within(org.springframework.session..*)")
    public void dump(JoinPoint jp) {
        System.out.println(
            "*** before: " + jp.toShortString() +
            ", args=" + jp.getArgs() +
            ", this=" + jp.getThis() +
            ", target=" + jp.getTarget() +
            ", host=" + getHost()
        );
    }

    private Optional<String> getHost() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(r -> r instanceof ServletRequestAttributes)
                .map(r -> ((ServletRequestAttributes) r).getRequest().getServerName());
    }
}
