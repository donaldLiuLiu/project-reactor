package com.sc.reactor.async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FutureCall {

    //Future
    public Future<String> futureCall(int start, int count) {
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
        return future;
    }

    //TODO CompletableFuture


    public void stream() {
        List<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        list.add("789");

        //数据流流动过程调试
        list.stream().filter(val -> {
            System.out.println("filter run: " + val);
            return true;
        }).map(val -> {
            System.out.println("map run: " + val);
            return val;
        }).collect(ArrayList::new, (l, v) -> {
            System.out.println("collect run: " + v);
            l.add(v);
        }, (l1, l2) -> {
            l1.addAll(l2);
        });

    }


    //并行流：内部开多个线程处理
    public void parrallelStream() {
        List<List<Integer>> listOne = new ArrayList<>();
        listOne.add(Arrays.asList(1, 2));
        listOne.add(Arrays.asList(3, 4));

        List<Integer> resultOne = listOne.stream().flatMap(l -> l.stream()).collect(Collectors.toList());
        System.out.println(resultOne);

        List<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        list.add("789");

        Stream<String> parallelStream = list.parallelStream(); //并行流
        System.out.println(parallelStream);
        Stream<String> mapStream = parallelStream.map(l -> l.substring(2)); //开多个线程处理，返回值是普通流
        System.out.println(mapStream);
        Stream<String> mapParallelStream = mapStream.parallel(); //普通流并行化
        List<String> result = mapParallelStream.collect(() -> {
            return new ArrayList<String>();
        }, (newList, val) -> {
            newList.add(val);
        }, (list1, list2) -> {  //有些流式方法需要定义多个线程的结果的combiner
            //throw new RuntimeException("123");
            list1.addAll(list2);
        });
        System.out.println(result);

    }

    public static void main(String argv[]) throws Exception {
        FutureCall futureCall = new FutureCall();

        /*Future<String> future = futureCall.futureCall(1, 10);
        System.out.println("future ...");
        String result = future.get();
        System.out.println(result);*/

        //futureCall.parrallelStream();

        futureCall.stream();
    }

}
