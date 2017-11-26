package com.thestarthrower;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Random;

public class ClassToLeak {
    /* 3a. The class allocates a large chunk of memory (e.g. new byte[1000000]), stores a strong reference to it in a static field */
    static HashMap mapOfInsanity = new HashMap<Integer, ByteBuffer>();
    static ByteBuffer[] arrayOfInsanity = new ByteBuffer[1000000];

    public static void fillTheMap(){
        for (int i =0;i<10000;i++){
            Integer rando = new Random().nextInt(1000);
            ByteBuffer bytebuffer = ByteBuffer.allocate(512);
            mapOfInsanity.put(rando, bytebuffer);
        }
    }
}
