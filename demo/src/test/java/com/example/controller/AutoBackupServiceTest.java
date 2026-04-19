package com.example.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.example.model.InMemoryCache;

class AutoBackupServiceTest {

    @AfterEach
    void cleanup() {
        AutoBackupService.stop();
        InMemoryCache.getInstance().refresh(List.of(), List.of(), List.of());
    }

    
    @Test
    void startShouldNotScheduleTwice() {
        AutoBackupService.start(1);
        var first = getTask();

        AutoBackupService.start(1);
        var second = getTask();

        assertSame(first, second);
    }


    @Test
    void stopShouldCancelTask() {
        AutoBackupService.start(1);
        var task = getTask();

        AutoBackupService.stop();

        assertTrue(task.isCancelled());
    }

    // Helper
    private java.util.concurrent.ScheduledFuture<?> getTask() {
        try {
            var field = AutoBackupService.class.getDeclaredField("task");
            field.setAccessible(true);
            return (java.util.concurrent.ScheduledFuture<?>) field.get(null);
        } catch (Exception e) {
            fail("Unable to access task field");
            return null;
        }
    }
}
