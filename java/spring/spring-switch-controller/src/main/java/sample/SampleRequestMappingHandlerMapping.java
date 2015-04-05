package sample;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class SampleRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
        return super.lookupHandlerMethod(changePath(lookupPath, request), new SampleHttpServletRequest(request));
    }

    private String changePath(String path, HttpServletRequest request) {
        if ("dev".equals(request.getParameter("mode"))) {
            return "/dev" + path;
        }
        return path;
    }

    class SampleHttpServletRequest extends HttpServletRequestWrapper {
        public SampleHttpServletRequest(HttpServletRequest req) {
            super(req);
        }

        @Override
        public String getServletPath() {
            return changePath(super.getServletPath(), this);
        }
    }
}
