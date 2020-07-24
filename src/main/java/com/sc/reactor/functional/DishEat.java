package com.sc.reactor.functional;

import java.util.*;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DishEat<multi100> {

    public static List<Dish> menu() {
        List<Dish> menu = Arrays.asList(
                Dish.builder().name("pork").calories(800).vegetarian(false).type(Dish.Type.MEAT).build(),
                Dish.builder().name("beef").calories(700).vegetarian(false).type(Dish.Type.MEAT).build(),
                Dish.builder().name("chicken").calories(400).vegetarian(false).type(Dish.Type.MEAT).build(),
                Dish.builder().name("french fries").calories(530).vegetarian(false).type(Dish.Type.OTHER).build(),
                Dish.builder().name("rice").calories(300).vegetarian(true).type(Dish.Type.MEAT).build(),
                Dish.builder().name("season fruit").calories(120).vegetarian(true).type(Dish.Type.OTHER).build(),
                Dish.builder().name("pizza").calories(550).vegetarian(true).type(Dish.Type.OTHER).build(),
                Dish.builder().name("prawns").calories(300).vegetarian(false).type(Dish.Type.FISH).build(),
                Dish.builder().name("salmon").calories(450).vegetarian(false).type(Dish.Type.FISH).build());

        return menu;
    }

    //filter distinct skip limit
    public static void filter() {
        //filter distinct skip limit
        List<Dish> menu = menu();
        List<Dish.Type> result = menu.stream()
                .map(Dish::getType)
                .distinct()
                .skip(1)
                .limit(2)
                .collect(Collectors.toList());
        result.stream().forEach(System.out::println);
    }

    //sort
    public static void sort() {
        List<Dish> menu = menu();
        menu.sort(Dish::compareByName);

        //Comparator.comparing(...)
        menu.stream().sorted(Comparator.comparing(Dish::getName)); //menu.stream().sorted(Comparator.comparing(Dish::getName, String::compareTo));

        //thenComparing
        menu.stream().sorted(Comparator.comparing(Dish::getCalories).thenComparing(Dish::getName));

        //reverseOrder():返回倒序Comparator
        menu.stream().sorted(Comparator.comparing(Dish::getName, Comparator.reverseOrder()));

        //reversed(): 返回Comparator的倒序装饰 （优先）
        menu.stream().sorted(Comparator.comparing(Dish::getName).reversed());

    }

    //map flatMap
    public static void map() {
        //map:流中元素操作 flatMap:生成一个新的流
        String[] strings = new String[]{"Goodbye", "world"};
        Stream<String> words = Arrays.stream(strings);
        List<String> rs = words
                .map(word -> word.split(""))
                .flatMap(Arrays::stream)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(rs);

        //map flatMap
        List<Integer> one = Arrays.asList(1, 2, 3);
        List<Integer> two = Arrays.asList(3, 4);
        List<Integer[]> rus = one.stream().map(o -> {
            List<Integer[]> r = two.stream().map(t -> {
                return new Integer[]{o, t};
            }).collect(Collectors.toList());
            return r;
        }).flatMap(l -> l.stream()).collect(Collectors.toList());
        rus.forEach(each -> System.out.println(each[0] + "-" + each[1]));

        List<Integer[]> rrs = one.stream().flatMap(i -> {
            return two.stream().map(j -> new Integer[]{i, j});
        }).collect(Collectors.toList());
        rrs.forEach(each -> System.out.println(each[0] + "-" + each[1]));
    }

    //reduce
    public static void reduce() {
        //reduce
        List<Integer> nbs = Arrays.asList(1, 2, 3);
        Optional<Integer> rens = nbs.stream().reduce(Integer::sum);
        rens.ifPresent(ren -> System.out.println(ren));

        Optional<Integer> maxs = nbs.stream().reduce(Integer::max);
        maxs.ifPresent(max -> System.out.println(max));
    }

    //IntStream
    public static void intStream() {
        List<Dish> menu = menu();
        IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
        int sum = intStream.sum();
        System.out.println(sum);
    }

    //构建流 Stream<T>
    public static void buildStream() {

        Stream<String> strStream = Stream.of("123", "321", "113");

        int[] numbers = {2, 3, 5, 7, 11, 13};
        IntStream intStream = Arrays.stream(numbers);

        Dish[] dishs = {Dish.builder().name("pork").calories(800).vegetarian(false).type(Dish.Type.MEAT).build(),
                Dish.builder().name("beef").calories(700).vegetarian(false).type(Dish.Type.MEAT).build()};
        Stream<Dish> dishStream = Arrays.stream(dishs);

        //集合流 集合.stream()

        //文件的流 Files.lines()

        //流生成器
        Stream<Integer> iterateStream = Stream.iterate(0, n -> n + 2);
        iterateStream.limit(10).forEach(System.out::println);

        Stream<Object> generateStream = Stream.generate(Math::random);
        generateStream.limit(10).forEach(System.out::println);


    }

    //collect
    public static void collect() {
        List<Dish> menu = menu();

        //counting
        Long counting = menu.stream().collect(Collectors.counting());
        long count = menu.stream().count();

        //maxBy minBy
        Optional<Integer> maxReduce = menu.stream().map(Dish::getCalories).reduce(Integer::max);
        OptionalInt maxIntStream = menu.stream().mapToInt(Dish::getCalories).max();
        Optional<Dish> maxCollect = menu.stream().collect(Collectors.maxBy(Comparator.comparing(Dish::getCalories)));

        //sum
        Integer sumCollect = menu.stream().collect(Collectors.summingInt(Dish::getCalories));

        //average
        Double averageCollect = menu.stream().collect(Collectors.averagingInt(Dish::getCalories));

        //summary
        IntSummaryStatistics summaryCollect = menu.stream().collect(Collectors.summarizingInt(Dish::getCalories));

        //joining
        String joining = menu.stream().map(Dish::getName).collect(Collectors.joining(",", "(", ")"));

        //grouping
        Map<Dish.Type, Map<String, List<Dish>>> result =
                menu.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.groupingBy(dish -> {
            if (dish.getCalories() <= 400) return "低";
            else return "高";
        })));

        //多级grouping   第二个参数是Collector,返回值Collector,以此支持多级
        /*Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier,
                Collector<? super T, A, D> downstream)*/
        Map<Dish.Type, Long> mapCountingCollect =
                menu.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.counting()));
        Map<Dish.Type, Dish> mapMaxCollect =
                menu.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(Dish::getCalories)), Optional::get)));

        //多级mapping
        /*Collector<T, ?, R> mapping(Function<? super T, ? extends U> mapper,
                Collector<? super U, A, R> downstream)*/
        menu.stream().map(Dish::getName).collect(Collectors.toList());
        menu.stream().collect(Collectors.mapping(Dish::getName, Collectors.toList()));

        //toList toSet toMap toCollection
        menu.stream().map(Dish::getName).collect(Collectors.toList());
        menu.stream().map(Dish::getName).collect(Collectors.toCollection(ArrayList::new));




    }
    public static void partitionPrimes(int n) {
        Map<Boolean, List<Integer>> result = IntStream.rangeClosed(2, n).boxed()
                    .collect(Collectors.partitioningBy(candidate -> isPrime(candidate)));
        System.out.println(result);
    }
    public static boolean isPrime(int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return IntStream.rangeClosed(2, candidateRoot)
                .noneMatch(i -> candidate % i == 0);
    }

    /** //无状态 顺序 并行
     * @see ParallelCall
     *
     */

    //Optional
    public static Dish defaultDish() {
        return Dish.builder().name("chicken").calories(800).vegetarian(false).type(Dish.Type.MEAT).build();
    }
    public static void eatDish(Dish dish) {
        System.out.println("eat: " + dish.getName());
    }
    public static void optiaonl() {
        Dish dish = Dish.builder().name("pork").calories(800).vegetarian(false).type(Dish.Type.MEAT).build();
        Dish defaultDish = Dish.builder().name("chicken").calories(800).vegetarian(false).type(Dish.Type.MEAT).build();

        Optional.of(dish); //如果dish==null抛异常

        Optional.ofNullable(dish)       //如果dish为空，生成一个默认的Optional<?> EMPTY{value==null}
                .orElse(defaultDish);   //如果value!=null?value:defaultDish.get()

        Optional.ofNullable(dish).orElseGet(DishEat::defaultDish);  //orElseGet

        Optional.ofNullable(dish).ifPresent(DishEat::eatDish);  //如果value!=null使用consumer消费之

        String dishName = Optional.ofNullable(dish).map(Dish::getName).orElse("default dish name");


        DishFlat dishFlat = new DishFlat(new DishFlat.DishWrapper(dish));
        Optional<String> result = Optional.ofNullable(dishFlat)
                                        .map(DishFlat::getWrapper)
                                        .map(DishFlat.DishWrapper::getDish)
                                        .map(Dish::getName);
        System.out.println(result.orElse("default dishFlat"));

        Optional<String> flatResult = Optional.ofNullable(dishFlat)
                .flatMap(DishFlat::flatWrapper)
                .flatMap(DishFlat.DishWrapper::flatDish)
                .map(Dish::getName);


    }


    //使用变量保存lambda
    public final static DoubleFunction multi100 = x -> x*100;
    //方法返回lambda
    public final static DoubleUnaryOperator curriedConverter(double f, double b) {
        return x -> x * f + b;
    }


    public static void main(String argv[]) {
        //filter();
        //map();
        //reduce();

        //intStream();

        //buildStream();

        //collect();
        //partitionPrimes(100);

        //optiaonl();

        sort();
    }
}
