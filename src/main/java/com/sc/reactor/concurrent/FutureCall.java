package com.sc.reactor.concurrent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FutureCall {

    //Future
    public void futureCall(int start, int count) throws Exception {
        ExecutorService executor = new ThreadPoolExecutor(1, 1,
                10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        Future<String> future = executor.submit(() -> {
            String result = "";
            for (int k = start; k < count; k++) {
                result += k;
            }
            System.out.println(executor);
            executor.shutdown();
            System.out.println(executor);
            return result;
        });

        System.out.println("future ...");
        String result = future.get(5000, TimeUnit.MILLISECONDS);  //wait for completion
        System.out.println(result);
    }

    //CompletableFuture
    public void completableFuture() {
        ExecutorService executor = new ThreadPoolExecutor(1, 1,
                10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        CompletableFuture<String> completeFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务运行的线程: " + Thread.currentThread().getName());
            return "result ...";
        }, executor);
        System.out.println("主线程: " + Thread.currentThread().getName());

        completeFuture.whenComplete((result, error) -> {
            System.out.println("whenComplete运行线程: " + Thread.currentThread().getName());
            System.out.println("error输出: " + error);
            System.out.println("result输出: " + result);

            executor.shutdown();
        });

        System.out.println("主线程结束... ");
    }


    private List<FutureShop> listShop() {
        return Arrays.asList(new FutureShop("one piece"), new FutureShop("无上大快刀")
            , new FutureShop("初代鬼泣"), new FutureShop("三字经")
            , new FutureShop("百家姓"), new FutureShop("notebook"));
    }
    //比较parallel 与 completableFuture
    public void completableFutureShop() {
        List<FutureShop> shopList = listShop();
        ExecutorService executor = new ThreadPoolExecutor(1, 1,
                10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        List<CompletableFuture<String>> result = shopList.stream().map(shop ->
                CompletableFuture.supplyAsync(
                        () -> "{shopName: " + shop.getName() + ";" + "product: " + "one plus8" + ";price: " + shop.getPrice("one plus8") +"}"
                        , executor)
        ).collect(Collectors.toList());

        List<String> productPriceList = result.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        /*Stream<CompletableFuture<Void>> rr = result.stream().map(future -> future.thenAccept(System.out::println));
        CompletableFuture[] aa = (CompletableFuture[]) rr.toArray();
        CompletableFuture.allOf(aa).join();*/

    }
    public void parallelStreamShop() {
        List<FutureShop> shopList = listShop();
        List<String> productPriceList = shopList.parallelStream()
                .map(shop -> "{shopName: " + shop.getName() + ";" + "product: " + "one plus8" + ";price: " + shop.getPrice("one plus8") +"}")
                .collect(Collectors.toList());
        System.out.println(productPriceList);
    }

    public void chainCall() {
        ExecutorService executor = new ThreadPoolExecutor(1, 1,
                10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        CompletableFuture<String> completeFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "result: 第一个任务运行";
        }, executor);
        CompletableFuture<String> anotherFuture = completeFuture.thenCompose(result -> CompletableFuture.supplyAsync(() -> {
            System.out.println("前一个任务运行的返回值: " + result);
            return "result: 第二个任务运行";
        }, executor));
        anotherFuture.whenComplete((rs, ee) -> {
            System.out.println("whenComplete run");
            //System.out.println(ee);
            System.out.println(rs);

            executor.shutdown();
        });
        System.out.println("主线程");
    }

    public void combineCall() {
        ExecutorService executor = new ThreadPoolExecutor(1, 1,
                10, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        CompletableFuture<String> completeFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "result: 第一个任务运行";
        }, executor);
        CompletableFuture<String> anotherFuture = completeFuture.thenCombine(CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "result: 第二个任务运行";
        }, executor), (task1Result, task2Result) -> task1Result + task2Result);

        anotherFuture.whenCompleteAsync((rs, ee) -> {
            System.out.println("combine result");
            //System.out.println(ee);
            System.out.println(rs);

            executor.shutdown();
        });
        System.out.println("主线程");
    }

    public static void main(String argv[]) throws Exception {
        FutureCall futureCall = new FutureCall();

        //futureCall.futureCall(1, 10);

        futureCall.completableFuture();

        //futureCall.completableFutureShop();
        //futureCall.parallelStreamShop();

        //futureCall.chainCall();
        //futureCall.combineCall();

    }

}
