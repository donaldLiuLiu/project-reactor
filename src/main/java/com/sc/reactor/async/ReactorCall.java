package com.sc.reactor.async;

import com.sun.javafx.collections.MappingChange;
import com.sun.xml.internal.ws.util.CompletedFuture;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


public class ReactorCall {

    public void flux() {
        //Flux序列数据流过程调试
        Flux<Object> flux = Flux.just("one", 1, 2L, Arrays.asList(1, 2));

        flux.map(val -> {
            System.out.println("map run: " + val);
            return val;
        }).subscribe(perValue -> {
            System.out.println("onNext: " + perValue + ", threadId: " + Thread.currentThread().getId());
        }, error -> {
            System.out.println("onError: " + error);
        }, () -> {
            System.out.println("onComplete");
        }, subscription -> {
            subscription.request(Long.MAX_VALUE);
        });

        /*flux.subscribe(perValue -> {
            System.out.println("onNext: " + perValue + ", threadId: " + Thread.currentThread().getId());
        }, error -> {
            System.out.println("onError: " + error);
        }, () -> {
            System.out.println("onComplete");
        }, subscription -> {
            subscription.request(Long.MAX_VALUE);
        });*/
        System.out.println("main current thread: " + Thread.currentThread().getId());

    }

    public void request() {
        Flux<Integer> intFlux = Flux.range(1, 10000);

        intFlux.map(val -> {
            System.out.println("map run: " + val + ", current thread: " + Thread.currentThread().getId());
            return val;
        }).subscribe(new Subscriber<Integer>() {
            private Subscription subscription;
            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer o) {
                subscription.request(10);
                System.out.println("onNext: " + o + ", current thread: " + Thread.currentThread().getId());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
        System.out.println("main current thread: " + Thread.currentThread().getId());

    }


    public void generate() {
        Flux<String> flux = Flux.generate(AtomicLong::new, (state, sink) -> {
            long s = state.getAndIncrement();
            System.out.println("current state: " + s + ", current thread: " + Thread.currentThread().getId());
            sink.next("a state: " + s); //SynchronousSink: its next() method can only be called at most once per callback invocation
            //sink.next("a state again: " + s + ",again"); //IllegalStateException: More than one call to onNext
            if (s == 10) sink.complete();
            return state;
        }, (lastState) -> {
            System.out.println("last state: " + lastState.get() + ", current thread: " + Thread.currentThread().getId());
        });

        flux.handle((val, sink) -> { //It is close to generate, in the sense that it uses a SynchronousSink and only allows one-by-one emissions
            if(!val.equals("a state: 5")) {
                sink.next(val);
            }
        }).map(val -> {
            System.out.println("map run: " + val + ", current thread: " + Thread.currentThread().getId());
            return val;
        }).subscribe(perValue -> {
            System.out.println("onNext: " + perValue + ", threadId: " + Thread.currentThread().getId());
        }, error -> {
            System.out.println("onError: " + error);
        }, () -> {
            System.out.println("onComplete");
        }, subscription -> {
            subscription.request(Long.MAX_VALUE);
        });

    }

    public void generateThread() {
        Flux<String> flux = Flux.generate(AtomicLong::new, (state, sink) -> {
            long s = state.getAndIncrement();
            new Thread(() -> {
                System.out.println("current state: " + s + ", current thread: " + Thread.currentThread().getId());
                sink.next("a state: " + s + Thread.currentThread().getName()); //IllegalStateException: More than one call to onNext
            }).start();
            System.out.println("current state: " + s + ", current thread: " + Thread.currentThread().getId());
            sink.next("a state: " + s + Thread.currentThread().getName());  //IllegalStateException: More than one call to onNext
            if (s == 10) sink.complete();
            return state;
        }, (lastState) -> {
            System.out.println("last state: " + lastState.get() + ", current thread: " + Thread.currentThread().getId());
        });

        flux.map(val -> {
            System.out.println("map rund: " + val + ", current thread: " + Thread.currentThread().getId());
            return val;
        }).subscribe(perValue -> {
            System.out.println("onNext: " + perValue + ", threadId: " + Thread.currentThread().getId());
        }, error -> {
            System.out.println("onError: " + error);
        }, () -> {
            System.out.println("onComplete");
        }, subscription -> {
            subscription.request(Long.MAX_VALUE);
        });

    }

    public void create() {
        List<String> dataList = new ArrayList<>();
        dataList.add("123");
        dataList.add("456");
        dataList.add("789");

        Flux<String> flux = Flux.create(sink -> {
            //FluxSink(create): Asynchronous and Multi-threaded
            //Asynchronous: next不用等待subscribe确认，可以无限调用
            //Multi-threaded: next调用允许多线程调用，并且多个线程之间互不干扰
            dataList.forEach(data -> {
                sink.next(data + ",threadId: " + Thread.currentThread().getId());
                sink.next(data + ",(create)again,threadId: " + Thread.currentThread().getId());
            });
        });

        flux.subscribe(d -> System.out.println("new: " + d));

        flux.map(val -> {
            System.out.println("map run(create): " + val + ",(map)threadId-"+Thread.currentThread().getId());
            return val;
        }).subscribe(perValue -> {
            System.out.println("onNext: " + perValue + ",(onNext)threadId-"+Thread.currentThread().getId() + "\n");
        }, error -> {
            System.out.println("onError: " + error);
        }, () -> {
            System.out.println("onComplete");
        }, subscription -> {
            subscription.request(Long.MAX_VALUE);
        });

    }

    public void createThread() {
        List<String> dataList = new ArrayList<>();
        dataList.add("abc");
        dataList.add("def");
        dataList.add("ghi");

        Flux<String> flux = Flux.create(sink -> {
            //FluxSink(create): Asynchronous and Multi-threaded
            Runnable run = (() -> {
                dataList.forEach(data -> {
                    sink.next(data + ",(createThread)threadId: " + Thread.currentThread().getId());
                });
            });
            new Thread(run).start();
            new Thread(run).start();
        });

        flux.map(val -> {
            System.out.println("map run(createThread): " + val + ",(map)threadId-"+Thread.currentThread().getId());
            return val;
        }).subscribe(perValue -> {
            System.out.println("onNext: " + perValue + ",(onNext)threadId-"+Thread.currentThread().getId() + "\n");
        }, error -> {
            System.out.println("onError: " + error);
        }, () -> {
            System.out.println("onComplete");
        }, subscription -> {
            System.out.println("sub threadId: " + Thread.currentThread().getId());
            subscription.request(Long.MAX_VALUE);
        });
        System.out.println("main threadId: " + Thread.currentThread().getId());

    }

    public void push() {
        List<String> dataList = new ArrayList<>();
        dataList.add("p123");
        dataList.add("p456");
        dataList.add("p789");

        Flux<String> flux = Flux.push(sink -> {
            //FluxSink(push): Asynchronous but single-threaded
            //Asynchronous: next不用等subscribe确认，可以无限调用
            //single-thread: next调用线程间互斥
            dataList.forEach(data -> {
                sink.next(data + ",threadId: " + Thread.currentThread().getId());
                sink.next(data + ",(push)again,threadId:" + Thread.currentThread().getId());
            });
        });

        flux.map(val -> {
            System.out.println("map run(push): " + val + ",(map)threadId-"+Thread.currentThread().getId());
            return val;
        }).subscribe(perValue -> {
            System.out.println("onNext: " + perValue + ",(onNext)threadId-"+Thread.currentThread().getId() + "\n");
        }, error -> {
            System.out.println("onError: " + error);
        }, () -> {
            System.out.println("onComplete");
        }, subscription -> {
            subscription.request(Long.MAX_VALUE);
        });
    }

    public void pushThread() {
        List<String> dataList = new ArrayList<>();
        dataList.add("p123p");
        dataList.add("p456p");
        dataList.add("p789p");

        Flux<String> flux = Flux.push(sink -> {
            //FluxSink(push): Asynchronous but single-threaded
            Runnable run = (() -> {
                dataList.forEach(data -> {
                    sink.next(data + ",(push)threadId: " + Thread.currentThread().getId());
                });
            });
            new Thread(run).start();
            new Thread(run).start(); //
        });

        flux.map(val -> {
            System.out.println("map run(pushThread): " + val + ",(map)threadId-"+Thread.currentThread().getId());
            return val;
        }).subscribe(perValue -> {
            System.out.println("onNext: " + perValue + ",(onNext)threadId-"+Thread.currentThread().getId() + "\n");
        }, error -> {
            System.out.println("onError: " + error);
        }, () -> {
            System.out.println("onComplete");
        }, subscription -> {
            subscription.request(Long.MAX_VALUE);
        });
    }

    public void publishOn() {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);
        List<String> dataList = new ArrayList<>();
        dataList.add("publishOn-1");
        dataList.add("publishOn-2");
        dataList.add("publishOn-3");

        Flux<String> flux = Flux.create(sink -> {
            //Runnable run = (() -> {
                dataList.forEach(data -> {
                    sink.next(data + ",(publishOn)threadId: " + Thread.currentThread().getId());
                });
            //});
            //new Thread(run).start();
            //new Thread(run).start();
        });

        flux.publishOn(s).map(val -> {
            System.out.println("map run(publishOnThread): " + val + ",threadId: " + Thread.currentThread().getId());
            return val;
        }).subscribe(perValue -> {
            System.out.println("onNext: " + perValue + ",(onNext)threadId-"+Thread.currentThread().getId() + "\n");
        }, error -> {
            System.out.println("onError: " + error);
            s.dispose();
        }, () -> {
            System.out.println("onComplete");
            s.dispose();
        }, subscription -> {
            System.out.println("sub threadId: " + Thread.currentThread().getId());
            subscription.request(Long.MAX_VALUE);
        });

        System.out.println("main threadId: " + Thread.currentThread().getId());

    }

    public void subscribeOn() {
        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);
        List<String> dataList = new ArrayList<>();
        dataList.add("subscribeOn-1");
        dataList.add("subscribeOn-2");
        dataList.add("subscribeOn-3");

        Flux<String> flux = Flux.create(sink -> {
            //Runnable run = (() -> {
                dataList.forEach(data -> {
                    sink.next(data + ",(subscribeOn)threadId: " + Thread.currentThread().getId());
                });
            //});
            //new Thread(run).start();
            //new Thread(run).start();
        });

        flux.subscribeOn(s).map(val -> {
            System.out.println("map run(subscribeOnThread): " + val + ",threadId: " + Thread.currentThread().getId());
            return val;
        }).subscribe(perValue -> {
            System.out.println("onNext: " + perValue + ",(onNext)threadId-"+Thread.currentThread().getId() + "\n");
        }, error -> {
            System.out.println("onError: " + error);
            s.dispose();
        }, () -> {
            System.out.println("onComplete");
            s.dispose();
        }, subscription -> {
            System.out.println("sub threadId: " + Thread.currentThread().getId());
            subscription.request(Long.MAX_VALUE);
        });

        System.out.println("main threadId: " + Thread.currentThread().getId());


    }

    public void hotGenerate() {
        DirectProcessor<String> dp = DirectProcessor.create();
        Flux<String> flux = dp.map(String::toUpperCase);

        flux.subscribe(d -> System.out.println("Subscriber 1 to Hot Source: "+d));

        dp.onNext("blue");
        dp.onNext("green");

        //此时订阅，将只能收到此处之后的数据
        flux.subscribe(d -> System.out.println("Subscriber 2 to Hot Source: "+d));

        dp.onNext("orange");
        dp.onNext("purple");
        dp.onComplete();

    }

    public void buffer() {

        Flux<List<Integer>> result =
                Flux.range(1, 10)
                        .buffer(5, 3);

        result.subscribe(s -> System.out.println(s));

    }

    public void parallel() {

        Flux.range(1, 10)
                .parallel(2)
                .runOn(Schedulers.parallel())
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + " -> " + i));

    }

    public void other() {

        //fromRunnable
        Mono<Object> result = Mono.fromRunnable(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(-1);
        });
        result.subscribe(d -> System.out.println(d));

        //fromFuture
        CompletableFuture<String> completeFuture = CompletableFuture.supplyAsync(() -> {
            return "1";
        });
        Mono<String> mono = Mono.fromFuture(completeFuture);
        mono.subscribe(d -> System.out.println(d));

    }

    public static void main(String argv[]) {
        ReactorCall reactorCall = new ReactorCall();

        //reactorCall.flux();

        //reactorCall.request();

        //reactorCall.generate();

        //reactorCall.generateThread();

        //reactorCall.create();

        //reactorCall.createThread();

        //reactorCall.push();

        //reactorCall.pushThread();

        //reactorCall.publishOn();

        //reactorCall.subscribeOn();

        //reactorCall.hotGenerate();

        //reactorCall.parallel();

        reactorCall.other();

    }

}
