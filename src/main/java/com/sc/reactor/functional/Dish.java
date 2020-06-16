package com.sc.reactor.functional;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class Dish {
    private String name;
    private boolean vegetarain;
    private int calories;
    private Type type;

    public Dish(String name, boolean vegetarain, int calories, Type type) {
        this.name = name;
        this.vegetarain = vegetarain;
        this.calories = calories;
        this.type = type;
    }

    public Dish() {
    }

    public static int compareByName(Dish o1, Dish o2) {
        return o1.getName().compareTo(o2.getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVegetarain() {
        return vegetarain;
    }

    public void setVegetarain(boolean vegetarain) {
        this.vegetarain = vegetarain;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {MEAT, FISH, OTHER}

    public static List<Dish> buildDish(Supplier<DishBuilder> supplier) {
        return Arrays.asList(supplier.get().build());
    }

    public static class DishBuilder {
        private Dish dish;

        public DishBuilder(Dish dish) {
            this.dish = dish;
        }

        public DishBuilder name(String name) {
            this.dish.name = name;
            return this;
        }
        public DishBuilder vegetarian(boolean vegetarain) {
            this.dish.vegetarain = vegetarain;
            return this;
        }
        public DishBuilder calories(int calories) {
            this.dish.calories = calories;
            return this;
        }
        public DishBuilder type(Type type) {
            this.dish.type = type;
            return this;
        }
        public Dish build() {
            return dish;
        }
    }
    public static DishBuilder builder() {
        return new DishBuilder(new Dish());
    }

}
