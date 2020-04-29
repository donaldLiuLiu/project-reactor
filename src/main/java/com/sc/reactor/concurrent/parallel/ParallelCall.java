package com.sc.reactor.concurrent.parallel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ParallelCall {

    /** //无状态 顺序 并行
     * 1 数据生成是否能够并行化拆分
     * 2 流式调用与并行化的排斥是否过大
     * 3 collector中combiner与parallel的关系
     * 4 fork_join
     * 5 Spliterator
     * @param n
     */
    //parallel 1 数据生成是否能够并行化拆分
    public static void parallel_create(int n) {
        IntStream intStream = IntStream.range(1, n);
        long start = System.currentTimeMillis();
        intStream.parallel().reduce(Integer::sum);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
    public static void parallel_create_1(int n) {
        long start = System.currentTimeMillis();
        IntStream.range(1, n).reduce(Integer::sum);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    //fork join
    public static void fork_join() {
        ExecutorService executor = new ThreadPoolExecutor(1, 1,
                10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    }

    public static void main(String argv[]) {
        /*int n = 1000000000;
        parallel_create(n);
        parallel_create_1(n);*/
    }

}
