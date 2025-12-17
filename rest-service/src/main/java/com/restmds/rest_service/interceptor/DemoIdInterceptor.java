package com.restmds.rest_service.interceptor;

import io.opentelemetry.api.trace.Span;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DemoIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String demoId = request.getHeader("X-Demo-Id");
        if (demoId != null && !demoId.isEmpty()) {
            try {
                Span.current().setAttribute("demo.id", demoId);
            } catch (Exception e) {
                // harmless if OpenTelemetry API not available at runtime
            }
        }
        return true;
    }
}
