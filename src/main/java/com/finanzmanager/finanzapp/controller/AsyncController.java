package com.finanzmanager.finanzapp.controller;

import com.finanzmanager.finanzapp.service.async.AsyncDemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AsyncController {

    private final AsyncDemoService service;

    @GetMapping("/async")
    public String runAsync() throws Exception {
        service.heavyTask();
        return "Async Task wurde gestartet!";
    }
}
