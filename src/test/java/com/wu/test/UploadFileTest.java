package com.wu.test;


import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


public class UploadFileTest {

    @Test
    public void testFile() {
        String fileName = "slfeif.jpg";
        String substring = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(substring);
    }


    @Test
    public void test() {
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(1,1);
        map.put(2,1);
        map.put(3,1);
        map.put(4,1);
        map.put(5,1);



        for (Integer integer : map.keySet()) {
            Integer integer1 = map.get(integer);
            integer1++;
        }
        System.out.println("===========");
        for (Map.Entry<Integer, Integer> integerIntegerEntry : map.entrySet()) {

            System.out.println(integerIntegerEntry.getValue());
        }


    }

}
