package org.jaoed.target;

import java.lang.Thread;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jaoed.service.Service;

public class PooledSender implements ResponseProcessor, Service, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(PooledSender.class);

    private final Thread senderThread;
    private final BlockingQueue<TargetResponse> outputQueue;
    private final ExecutorService threadPool;
    private final int outputQueueSize;
    private final int pollInterval;
    private final int poolSize;

    private volatile boolean running;

    public PooledSender(int outputQueueSize, int pollInterval, int poolSize) {
        this.outputQueue = new ArrayBlockingQueue<>(outputQueueSize);
        this.outputQueueSize = outputQueueSize;
        this.running = false;
        this.pollInterval = pollInterval;
        this.threadPool = Executors.newFixedThreadPool(poolSize);
        this.poolSize = poolSize;
        this.senderThread = new Thread(this);
    }

    @Override
    public void run() {
        LOG.info("starting pooled sender [queue = {}; pool = {}]", outputQueueSize, poolSize);

        while (running) {
            try {
                TargetResponse response = outputQueue.poll(pollInterval, TimeUnit.MILLISECONDS);
                if (response != null) {
                    threadPool.submit(() -> response.sendResponse());
                }
            } catch (Exception e) {
                LOG.error("error sending response", e);
            }
        }
    }

    @Override
    public boolean enqueue(TargetResponse response) {
        return outputQueue.offer(response);
    }

    @Override
    public void start() {
        this.running = true;
        senderThread.start();
    }

    @Override
    public void stop() {
        LOG.info("stopping pooled sender thread");
        this.running = false;
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS))
                threadPool.shutdownNow();
            senderThread.join();
        } catch (Exception e) {
            LOG.error("error stopping pooled sender thread", e);
            threadPool.shutdownNow();
        }
    }
}
