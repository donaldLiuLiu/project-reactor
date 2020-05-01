package com.sc.reactor.lazy;


public class LazyEat {


    public static MyList<Integer> from(int n) {
        return new LazyList<>(n, () -> from(n+1));
    }
    public static void lazy() {
        MyList<Integer> list = from(2);
        System.out.println(list.head());
        System.out.println(list.tail().head());

    }


    public MyList<Integer> primes(MyList<Integer> numbers) {
        return new LazyList<>(numbers.head(),
                () -> primes(numbers.tail().filter(n -> n % numbers.head() != 0)));
    }


    public static void main(String argv[]) {

        lazy();

        LazyEat lazy = new LazyEat();
        lazy.primes(from(2));

    }

}
