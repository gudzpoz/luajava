package party.iroiro.luajava.util;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

public interface LookupProvider {
    Class<?> wrap(Class<?> interfaceClass);
    MethodHandle lookup(Method method) throws Throwable;
    ClassLoader getLoader();
}
