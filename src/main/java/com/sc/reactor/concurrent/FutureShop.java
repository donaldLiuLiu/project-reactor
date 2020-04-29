package com.sc.reactor.concurrent;

public class FutureShop {
    private String name;

    public FutureShop(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public String getPrice(String product) {
        delay();
        return "100";
    }
}
