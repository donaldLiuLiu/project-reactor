package com.sc.reactor.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Callback {

    //回调，同步执行
    public void callbackSync(int start, int count, Consumer<Integer> consumer) {
        for(int i=start; i<count; i++) {
            consumer.accept(i);
        }
    }

    //回调，异步执行
    public void callbackAsync(int start, int count, Consumer<Integer> consumer) {
        //ExecutorService executor = Executors.newSingleThreadExecutor();
        ExecutorService executor = new ThreadPoolExecutor(1, 1,
                10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        executor.submit(() -> {
            for(int k=start; k<count; k++) {
                consumer.accept(k);
            }
            System.out.println(executor);
            executor.shutdown();
            System.out.println(executor);
        });
    }


    public static void main(String argv[]) throws Exception {
        Callback callback = new Callback();
        callback.callbackSync(1, 10, k -> System.out.println(k));
        System.out.println("sync ...");
        callback.callbackAsync(1, 10, k -> System.out.println(k));
        System.out.println("async ...");

    }
}
