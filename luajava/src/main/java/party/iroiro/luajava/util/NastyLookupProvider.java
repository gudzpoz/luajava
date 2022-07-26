/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * See https://github.com/jOOQ/jOOR/blob/main/jOOR-java-8/src/main/java/org/joor/Reflect.java
 */

package party.iroiro.luajava.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class NastyLookupProvider implements LookupProvider {
    private final Constructor<MethodHandles.Lookup> CACHED_LOOKUP_CONSTRUCTOR;

    public NastyLookupProvider() {
        Constructor<MethodHandles.Lookup> result;

        try {
            try {
                //noinspection JavaReflectionMemberAccess
                Optional.class.getMethod("stream");
                result = null;
            } catch (NoSuchMethodException e) {
                // [jOOQ/jOOR#57] [jOOQ/jOOQ#9157]
                // A JDK 9 guard that prevents "Illegal reflective access operation"
                // warnings when running the below on JDK 9+

                result = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);

                if (!result.isAccessible()) {
                    result.setAccessible(true);
                }
            }
        } catch (Throwable ignore) {
            // Can no longer access the above in JDK 9
            result = null;
        }

        CACHED_LOOKUP_CONSTRUCTOR = result;
    }

    @Override
    public Class<?> wrap(Class<?> interfaceClass) {
        return interfaceClass;
    }

    public MethodHandle lookup(Method method)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        MethodHandles.Lookup proxyLookup;

        // Java 9 version
        if (CACHED_LOOKUP_CONSTRUCTOR == null) {
            // Java 9 version for Java 8 distribution (jOOQ Open Source Edition)
            //noinspection JavaReflectionMemberAccess
            Method privateLookupIn = MethodHandles.class.getMethod(
                    "privateLookupIn", Class.class, MethodHandles.Lookup.class);
            MethodHandles.Lookup lookup = (MethodHandles.Lookup)
                    privateLookupIn.invoke(null, method.getDeclaringClass(), MethodHandles.lookup());
            proxyLookup = lookup.in(method.getDeclaringClass());
        } else {
            proxyLookup = CACHED_LOOKUP_CONSTRUCTOR.newInstance(method.getDeclaringClass());
        }

        return proxyLookup.unreflectSpecial(method, method.getDeclaringClass());
    }

    @Override
    public ClassLoader getLoader() {
        return getClass().getClassLoader();
    }
}
