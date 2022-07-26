package party.iroiro.luajava.util;

import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AsmLookupProvider implements LookupProvider {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final ConcurrentMap<String, Class<?>> extenders = new ConcurrentHashMap<>();
    private final LookupLoader loader = new LookupLoader(LookupLoader.class.getClassLoader());

    private Class<?> lookupExtender(Class<?> iClass) {
        try {
            String internalName = Type.getInternalName(iClass);
            Class<?> extender = extenders.get(internalName);
            if (extender != null) {
                return extender;
            }

            ClassReader reader = new ClassReader("party.iroiro.luajava.util.SampleExtender");
            ClassWriter writer = new ClassWriter(0);
            AtomicReference<String> className = new AtomicReference<>();
            ClassVisitor visitor = new ClassVisitor(Opcodes.ASM4, writer) {
                @Override
                public void visit(int version, int access, String name, String signature,
                                  String superName, String[] interfaces) {
                    if (internalName.startsWith("java/")) {
                        name = name + '$' + counter.getAndIncrement();
                    } else {
                        name = internalName + "LuaJavaImpl$" + counter.getAndIncrement();
                    }
                    cv.visit(version, access, name,
                            signature, superName, new String[]{internalName});
                    className.set(name.replace('/', '.'));
                }
            };
            reader.accept(visitor, 0);
            loader.add(className.get(), writer.toByteArray());
            extender = loader.findClass(className.get());
            extenders.put(internalName, extender);
            return extender;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
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
        Class<?> extender = lookupExtender(method.getDeclaringClass());
        return fromExtender(extender)
                .unreflectSpecial(method, extender);
    }

    public Class<?> wrap(Class<?> iClass) {
        return lookupExtender(iClass);
    }

    public ClassLoader getLoader() {
        return loader;
    }
}
