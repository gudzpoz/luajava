package party.iroiro.luajava.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LookupLoader extends ClassLoader {
    private final ConcurrentMap<String, Class<?>> classes = new ConcurrentHashMap<>();

    public LookupLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> findClass(String s) throws ClassNotFoundException {
        Class<?> c = classes.get(s);
        if (c == null) {
            throw new ClassNotFoundException(s);
        } else {
            return c;
        }
    }

    public void add(String name, byte[] content) {
        classes.put(name, defineClass(name, content, 0, content.length));
    }
}
