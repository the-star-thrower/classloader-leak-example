package com.thestarthrower;

/* inspiration from https://stackoverflow.com/questions/6470651/creating-a-memory-leak-with-java */

public class ClassloaderLeak {

    private static void startALeak(){
        /* 1. The application creates a long-running thread (or use a thread pool to leak even faster). */
        /* 2. The thread loads a class via an (optionally custom) ClassLoader. */
        /* 3. The class allocates a large chunk of memory (e.g. new byte[1000000]), stores a strong reference to it in a static field, and then         stores a reference to itself in a ThreadLocal. Allocating the extra memory is optional (leaking the Class instance is enough), but it          will make the leak work that much faster. */
        /* 4. The thread clears all references to the custom class or the ClassLoader it was loaded from. */
        /* 5. Repeat. */
    }

    public static void main(String[] args) {
	// write your code here
    }
}
