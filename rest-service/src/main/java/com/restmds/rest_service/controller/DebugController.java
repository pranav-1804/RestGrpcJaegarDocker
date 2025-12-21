package com.restmds.rest_service.controller;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import io.opentelemetry.api.trace.Span;


@RestController
@RequestMapping("/pod-id")
public class DebugController {

    @Value("${spring.application.name:rest-service}")
    private String serviceName;
    
    @Value("${hostname:unknown}")
    private String podId;
    
    @GetMapping()
    public Map<String, String> podId(@RequestHeader(value = "X-Demo-Id", required = false) String demoId) {

        if (demoId != null && !demoId.isEmpty()) {
                try { Span.current().setAttribute("demo.id", demoId); } catch (Exception e) { /* ignore */ }
        }
        return new TreeMap<>(Map.of(
            "service", serviceName,
            "podName", System.getenv("POD_NAME") != null ? System.getenv("POD_NAME") : "unknown",
            "podIp", System.getenv("POD_IP") != null ? System.getenv("POD_IP") : "unknown",
            "nodeName", System.getenv("HOSTNAME") != null ? System.getenv("HOSTNAME") : "unknown",
            "timestamp", Instant.now().toString()
        ));
    }

}
