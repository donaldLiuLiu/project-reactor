package com.sc.reactor.lazy;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class LazyList<T> implements MyList<T> {
    private T head;
    private Supplier<MyList<T>> tail;

    public LazyList(T head, Supplier<MyList<T>> tailSupplier) {
        this.head = head;
        this.tail = tailSupplier;
    }

    public MyList<T> filter(Predicate<T> p) {
        return isEmpty() ?
                this :
                p.test(head()) ?
                        new LazyList<>(head(), () -> tail().filter(p)) : tail().filter(p);
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public MyList<T> tail() {
        return tail.get();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
