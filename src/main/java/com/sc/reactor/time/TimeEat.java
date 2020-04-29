package com.sc.reactor.time;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class TimeEat {

    public static void start() {
        //Date 与 LocalDateTime

        //创建Date的可用方法 new Date(long milliSeconds);
        Date date = new Date(System.currentTimeMillis());
        LocalDateTime localDateTime = LocalDateTime.of(2010, 10, 9, 10, 9, 9); //2010-10-09 10:09:09
        long milliSeconds = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli(); //+8时区 的毫秒值
        //OffsetDateTime odt = OffsetDateTime.now(ZoneId.systemDefault());
        //ZoneOffset defaultOffset = odt.getOffset();
        //long milliSeconds = localDateTime.toInstant(defaultOffset).toEpochMilli(); //系统默认时区 的毫秒值
        new Date(milliSeconds);
        //老代码中Date可以通过如上LocalDateTime方式构建

        //老代码中已有值Date转化为LocalDateTime
        Instant instant = date.toInstant();
        LocalDateTime.ofInstant(instant, ZoneOffset.of("+8"));

    }

    public static void localDateTime() {

        LocalDateTime.now(); //使用系统默认时区的秒构建，此代码在本地使用本地系统的默认时区，在服务器使用服务器的默认时区，服务器有两台且两台的时区不同呢

        //1 创建一个时间 2010-10-09 10:09:09 (LocalDateTime)
        LocalDateTime localDateTime = LocalDateTime.of(2010, 10, 9, 10, 9, 9);
        //2 转化为+8时区 的秒 (Instant)
        Instant instant = localDateTime.toInstant(ZoneOffset.of("+8"));
        instant.toEpochMilli(); //取毫秒值


        //通过秒 纳秒(相对值) 时区偏移 创建
        LocalDateTime.ofEpochSecond(1286590149, 0, ZoneOffset.of("+8"));

        //parse format
        LocalDateTime.parse("2010-10-09 10:09:09");
        LocalDateTime.parse("2010-10-09 10:09:09", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime.parse("2010-10-09 10:09:09", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        /*
        //字段读取get

        //字段修改with, 注意final
        LocalDate date1 = LocalDate.of(2014, 3, 18); //2014-03-18
        LocalDate date2 = date1.withYear(2011);  //2011-03-18

        //字段plus minus, 注意final
        LocalDate date3 = date2.plus(6, ChronoUnit.MONTHS);
        */
        LocalDateTime withDate = LocalDateTime.of(2010, 10, 9, 10, 9, 9);
        LocalDateTime withDate1 = withDate.with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime withDate2 = withDate.with(temporal -> {
            DayOfWeek dow =
                    DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
            int dayToAdd = 1;
            if (dow == DayOfWeek.FRIDAY) dayToAdd = 3;
            else if (dow == DayOfWeek.SATURDAY) dayToAdd = 2;
            return temporal.plus(dayToAdd, ChronoUnit.DAYS);
        });


    }

    public void interval() {
        //Duration Period

        /*Duration d1 = Duration.between(time1, time2);
        Duration d2 = Duration.between(dateTime1, dateTime2);
        Duration d3 = Duration.between(instant1, instant2);
        Period d4 = Period.between(date1, date2);

        Duration threeMinutes = Duration.ofMinutes(3);
        Period tenDays = Period.ofDays(10);
        Period threeWeeks = Period.ofWeeks(3);
        Period twoYearsSixMonthsOneDay = Period.of(2, 6, 1);*/

    }


    public static void main(String argv[]) {

        System.out.println(ZoneId.systemDefault().getId());

    }

}
