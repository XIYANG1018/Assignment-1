package com.xiyang;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SkierApp {
    private static final int TOTAL_REQUESTS = 200000; // 200K
    private static final int THREAD_COUNT = 200;
    private static final int REQUEST_PER_THREAD = 1000;
    private static final BlockingDeque<LiftRideEvent> eventProducer = new LinkedBlockingDeque<>();

    public static void main(String[] args) {
        System.out.println("Starting...");
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successfulRequests = new AtomicInteger(0);
        AtomicInteger unsuccessfulRequests = new AtomicInteger(0);

        // A dedicated thread to generate events
        Thread eventProducerThread = new Thread(() -> {
            for (int i = 0; i < TOTAL_REQUESTS; i++) {
                eventProducer.offer(new LiftRideEvent());
            }
            System.out.println("Event producer finished.");
        });

        long start = System.currentTimeMillis();
        eventProducerThread.start();
        System.out.println("Worker threads starting...");

        // worker
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                SkierClient skierClient = new SkierClient();
                for (int j = 0; j < REQUEST_PER_THREAD; j++) {
                    try {
                        LiftRideEvent liftRideEvent = eventProducer.take();
                        skierClient.postLiftRideEvent(liftRideEvent);
                        successfulRequests.incrementAndGet();
                    } catch (Exception e) {
                        unsuccessfulRequests.incrementAndGet();
                        System.err.println("Request failed: " + e.getMessage());
                    }
                }

                countDownLatch.countDown();

            });
        }

        try {
            countDownLatch.await();
            eventProducerThread.join();

            long end = System.currentTimeMillis();
            long wallTime = (end - start) / 1000;

            System.out.println("Wall Time: " + wallTime + " seconds");
            System.out.println("Throughput: " + (TOTAL_REQUESTS / wallTime) + " requests/second");
            System.out.println("Successful requests: " + successfulRequests.get());
            System.out.println("Unsuccessful requests: " + unsuccessfulRequests.get());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread interrupted: " + e.getMessage());
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}