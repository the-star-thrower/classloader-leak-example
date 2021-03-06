package com.thestarthrower;

/* inspiration from https://stackoverflow.com/questions/6470651/creating-a-memory-leak-with-java */

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassloaderLeak {
    //public static final ThreadLocal<ArrayList<Class>> context = new ThreadLocal<ArrayList<Class>>();

    private static ThreadLocal<ArrayList<Class>> context =
            new ThreadLocal<ArrayList<Class>>() {
                @Override public ArrayList<Class> initialValue() {
                    return new ArrayList<Class>();
                }
            };

    private static ThreadLocal<ArrayList<Class>> objectRefs =
            new ThreadLocal<ArrayList<Class>>() {
                @Override public ArrayList<Class> initialValue() {
                    return new ArrayList<Class>();
                }
            };

    public static void startALeak(){
        /* 1. The application creates a long-running thread (or use a thread pool to leak even faster). */
        ExecutorService threadService = Executors.newFixedThreadPool(20);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                /* 2. The thread loads a class via an (optionally custom) ClassLoader. */
                try {
                    System.out.println("Trying...");
                    URL classUrl = new URL("file:/Users/kmaack/dev/sample_applications/classloader-leak-example/out/production/classloader-leak-example");
                    URL[] classUrls = { classUrl };
                    ClassLoader loader = new URLClassLoader(classUrls);
                    //Class leaky = loader.loadClass("com.thestarthrower.ClassToLeak");
                    Class<?> leaky = Class.forName("com.thestarthrower.ClassToLeak");
                    ClassToLeak.fillTheMap();

                    /* 3b...then stores a reference to itself in a ThreadLocal. */
                    ArrayList<Class> classList = new ArrayList<Class>();
                    classList.addAll(context.get());
                    System.out.println("Current ThreadLocal.context contents: " + classList.toString());
                    classList.add(leaky);
                    System.out.println("New ThreadLocal.context contents: " + classList.toString());
                    context.set(classList);
                    //System.out.println("Array List going into ThreadLocal storage: " + classList.toString());
                } catch (ClassNotFoundException ex) {
                    System.out.println("Class was not found. Evidence: " + ex.getMessage());
                } catch (MalformedURLException ex) {
                    System.out.println("URL was malformed. Evidence: " + ex.getMessage());
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
            //System.out.println("KB Used: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024);
            //System.out.println("KB Free: " + (double) (Runtime.getRuntime().freeMemory()) / 1024 + "\n");
        }
    }
}
