package party.iroiro.luajava.util;

import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AsmLookupProvider implements LookupProvider {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final ConcurrentMap<String, Class<?>> extenders = new ConcurrentHashMap<>();
    private final LookupLoader loader = new LookupLoader(LookupLoader.class.getClassLoader());

    private final NastyLookupProvider fallback = new NastyLookupProvider();

    private final boolean supported;

    AsmLookupProvider() {
        boolean isSupported;
        try {
            lookupExtender("party.iroiro.luajava.util.SampleExtender$Test",
                    "java.lang.Runnable");
            isSupported = true;
        } catch (Throwable e) {
            isSupported = false;
        }
        supported = isSupported;
    }

    private Class<?> lookupExtender(String name, String iName) throws Throwable {
        loader.add(name, SampleExtender.generateClass(
                name.replace('.', '/'), iName.replace('.', '/')));
        Class<?> extender = loader.findClass(name);
        extenders.put(iName, extender);
        return extender;
    }

    private Class<?> lookupExtender(Class<?> iClass) throws ClassNotFoundException {
        try {
            String iName = iClass.getName();
            Class<?> extender = extenders.get(iName);
            if (extender != null) {
                return extender;
            }

            String name;
            if (iName.startsWith("java.")) {
                name = "party.iroiro.luajava.util.SampleExtender$" + counter.getAndIncrement();
            } else {
                name = iName + "LuaJavaImpl$" + counter.getAndIncrement();
            }
            return lookupExtender(name, iName);
        } catch (Throwable e) {
            throw new ClassNotFoundException(e.toString());
        }
    }

    private MethodHandles.Lookup fromExtender(Class<?> extender) {
        try {
            Method getLookup = extender.getMethod("getLookup");
            return (MethodHandles.Lookup) getLookup.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public MethodHandle lookup(Method method) throws IllegalAccessException {
        if (supported) {
            try {
                Class<?> extender = lookupExtender(method.getDeclaringClass());
                return fromExtender(extender)
                        .unreflectSpecial(method, extender);
            } catch (ClassNotFoundException ignored) {
            }
        }
        try {
            return fallback.lookup(method);
        } catch (Throwable ex) {
            IllegalAccessException exception = new IllegalAccessException(ex.getMessage());
            exception.initCause(ex);
            throw exception;
        }
    }

    public @Nullable Class<?> wrap(Class<?> iClass) {
        if (supported) {
            try {
                return lookupExtender(iClass);
            } catch (ClassNotFoundException ignored) {
            }
        }
        return fallback.wrap(iClass);
    }

    public ClassLoader getLoader() {
        return loader;
    }
}
