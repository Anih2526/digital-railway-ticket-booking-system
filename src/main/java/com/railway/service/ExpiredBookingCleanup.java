
package com.railway.service;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class ExpiredBookingCleanup implements ServletContextListener {

    private static final Logger LOGGER =
            Logger.getLogger(ExpiredBookingCleanup.class.getName());

    private ScheduledExecutorService executor;

    @Override
    public void contextInitialized(ServletContextEvent event) {

        executor = Executors.newSingleThreadScheduledExecutor();

        executor.scheduleWithFixedDelay(() -> {
            try {
                new ExpiredBookingService().releaseExpiredSeats();
            } catch (Exception e) {
                LOGGER.log(
                        Level.SEVERE,
                        "Unable to release seats for completed journeys.",
                        e
                );
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}
