package com.thestarthrower;

/* inspiration from https://stackoverflow.com/questions/6470651/creating-a-memory-leak-with-java */

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassloaderLeak {
    public static final ThreadLocal<Class> context = new ThreadLocal<Class>();

    public static void startALeak(){
        /* 1. The application creates a long-running thread (or use a thread pool to leak even faster). */
        ExecutorService threadService = Executors.newFixedThreadPool(20);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                /* 2. The thread loads a class via an (optionally custom) ClassLoader. */
                try {
                    URL classUrl = new URL("file:/Users/kmaack/dev/sample_applications/classloader-leak-example/out/production/classloader-leak-example/com/thestarthrower/ClassToLeak.class");
                    URL[] classUrls = { classUrl };
                    ClassLoader loader = new URLClassLoader(classUrls);
                    Class leaky = loader.loadClass("com.thestarthrower.ClassToLeak");
                    ClassToLeak.fillTheMap();
                    /* 3b...then stores a reference to itself in a ThreadLocal. */
                    context.set(leaky);
                } catch (ClassNotFoundException ex) {
                } catch (MalformedURLException ex) {

                }
            }
        };
        for (int i=0; i<20;i++) {
            threadService.submit(task);
        }
        threadService.shutdown();
    }

    public static void main(String[] args) {
        /* 4. The thread clears all references to the custom class or the ClassLoader it was loaded from. */
        /* 5. Repeat. */
	    while (true) {
            startALeak();
        }
    }
}
