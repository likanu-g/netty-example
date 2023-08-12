package io.netty.difference;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class ScheduleExamples {
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    /**
     * Scheduling a task after 60s with a ScheduledExecutorService
     * */
    public static void schedule() {
        ScheduledExecutorService executor =
                Executors.newScheduledThreadPool(10);

        ScheduledFuture<?> future = executor.schedule(
                () -> System.out.println("Now it is 6 seconds later at" + new Date()), 6, TimeUnit.SECONDS);
        //...
        executor.shutdown();
    }

    /**
     * Scheduling a task with EventLoop
     * */
    public static void scheduleViaEventLoop() {
        ScheduledFuture<?> future = CHANNEL_FROM_SOMEWHERE.eventLoop().schedule(
                () -> System.out.println("60 seconds later"), 60, TimeUnit.SECONDS);
    }

    /**
     * Scheduling a recurring task with EventLoop
     * */
    public static void scheduleFixedViaEventLoop() {
        ScheduledFuture<?> future = CHANNEL_FROM_SOMEWHERE.eventLoop().scheduleAtFixedRate(
                () -> System.out.println("Run every 60 seconds"), 60, 60, TimeUnit.SECONDS);
    }

    /**
     * Canceling a task using ScheduledFuture
     * */
    public static void cancelingTaskUsingScheduledFuture(){
        ScheduledFuture<?> future = CHANNEL_FROM_SOMEWHERE.eventLoop().scheduleAtFixedRate(
                () -> System.out.println("Run every 60 seconds"), 60, 60, TimeUnit.SECONDS);
        // Some other code that runs...
        boolean mayInterruptIfRunning = false;
        future.cancel(mayInterruptIfRunning);
    }

    public static void main(String[] args) {
        System.out.println(new Date());
        schedule();
        System.out.println(new Date());
    }
}
