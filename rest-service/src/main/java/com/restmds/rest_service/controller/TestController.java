package com.restmds.rest_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.opentelemetry.api.trace.Span;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String home(@RequestHeader(value = "X-Demo-Id", required = false) String demoId) {
        if (demoId != null && !demoId.isEmpty()) {
                try { Span.current().setAttribute("demo.id", demoId); } catch (Exception e) { /* ignore */ }
        }
        return "REST Service is running!";
    }
}
