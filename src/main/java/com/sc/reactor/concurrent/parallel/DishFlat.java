package com.sc.reactor.concurrent.parallel;

import java.util.Optional;

public class DishFlat {

    private DishWrapper wrapper;

    public DishFlat(DishWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public DishWrapper getWrapper() {
        return wrapper;
    }

    public Optional<DishWrapper> flatWrapper() {
        return Optional.ofNullable(wrapper);
    }

    public void setWrapper(DishWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public static class DishWrapper{
        private Dish dish;

        public DishWrapper(Dish dish) {
            this.dish = dish;
        }

        public Optional<Dish> flatDish() {
            return Optional.ofNullable(dish);
        }
        public Dish getDish() {
            return dish;
        }

        public void setDish(Dish dish) {
            this.dish = dish;
        }
    }
}
