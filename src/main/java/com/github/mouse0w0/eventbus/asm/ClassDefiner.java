package com.github.mouse0w0.eventbus.asm;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

class ClassDefiner {

    private final Map<ClassLoader, MyClassLoader> loaders = Collections.synchronizedMap(new WeakHashMap<>());

    public Class<?> defineClass(ClassLoader parent, String name, byte[] data) {
        checkClass(name, parent);
        MyClassLoader loader = loaders.computeIfAbsent(parent, MyClassLoader::new);
        return loader.define(name, data);
    }

    private static void checkClass(String name, ClassLoader classLoader) {
        try {
            Class.forName(name, false, classLoader);
            throw new IllegalStateException("Class " + name + " already defined");
        } catch (ClassNotFoundException ignored) {
        }
    }

    private static class MyClassLoader extends ClassLoader {
        static {
            ClassLoader.registerAsParallelCapable();
        }

        public MyClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> define(String name, byte[] data) {
            synchronized (getClassLoadingLock(name)) {
                Class<?> clazz = findLoadedClass(name);
                if (clazz != null) return clazz;

                clazz = defineClass(name, data, 0, data.length);
                if (clazz == null) throw new NullPointerException();
                return clazz;
            }
        }
    }
}
