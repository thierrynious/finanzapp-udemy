package com.finanzmanager.finanzapp.service.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AsyncDemoService {

    @Async
    public CompletableFuture<String> heavyTask() throws InterruptedException {
        log.info("Starte Task in thread:{}",Thread.currentThread().getName());
        Thread.sleep(3000);
        log.info("Task beendet");
        return CompletableFuture.completedFuture("Fertig");
    }
}
