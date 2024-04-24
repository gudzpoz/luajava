# Lua API

## Extra Lua Types

We provide three extra `userdata` types that correspond three Java concepts. We use their abbreviations in the following documentation.

|              | [Java classes](#jclass-type) | [Java objects](#jobject-type) | [Java arrays](#jarray-type) |
|--------------|:----------------------------:|:-----------------------------:|:----------------------------|
| Abbreviation |   [`jclass`](#jclass-type)   |  [`jobject`](#jobject-type)   | [`jarray`](#jarray-type)    |

### `jclass` <Badge>type</Badge>

For a `jclass` `clazz`:

- `clazz.memberVar` performs the following in sequence:
    1. It looks for a field named `memberVar`. It returns the public static member if it finds it.
       - If you have an inner class also named `memberVar`, you would have to manually `java.import` it.
    2. It then looks for an inner class named `memberVar`, and returns that if it is available.
       - If you have a method also named `memberVar`, you need to use `java.method` to look that up.
    4. Otherwise, it prepares for a method call. See `clazz:memberMethod(...)` below.
- `clazz.memberVar = value` assigns to the public static member. If exceptions occur, a Lua error is generated.
- `clazz:memberMethod(...)` calls the public static member method `memberMethod`. See [Proxied Method Calls](#proxied-method-calls) for more info.
- `clazz(...)`:
  - For an interface, this expects a table as the parameter and creates a proxy for it. See [`java.proxy`](#proxy-jclass-table-function).
  - Otherwise, it calls the corresponding constructor. See [`java.new`](#new-jclass-function).
- `clazz.class` returns a `jobject`, wrapping an instance of `java.lang.Class<clazz>`.

```lua Example
Integer = java.import('java.lang.Integer')
-- Accessing a static member
print(Integer.TYPE)
-- Calling a static method
print(Integer:parseInt('1024'))
-- Construct an instance
print(Integer('1024'))
-- Get a Class<Integer> instance
print(Integer.class:getName())
```

::: tip
Don't confuse `jclass` with an instance of `java.lang.Class<?>`.
The former one corresponds to Java classes, i.e., `java.lang.String` in Java.
The latter one is just a `jobject`, i.e., `java.lang.String.class` in Java.
:::

### `jobject` <Badge>type</badge>

For a `jobject` `object`:

- `object.memberVar` returns the public member named `memberVar`.
- `object.memberVar = value` assigns to the public static member. If exceptions occur, a Lua error is generated.
- `object:memberMethod(...)` calls the public member method `memberMethod`. See [Proxied Method Calls](#proxied-method-calls) for more info.

```lua
Integer = java.import('java.lang.Integer')
i = java.new(Integer, 1024)
-- Calling a method
print(i:toString())
```

### `jarray` <Badge>type</Badge>

For a `jarray` `array`:

- `array[i]` returns `array[i - 1]`. Unlike Lua tables, we raise Lua errors if the index goes out of bounds.
- `array[i] = value` assigns to `array[i - 1]`. If exceptions occur, a Lua error is generated.
- `array:memberMethod(...)` calls the public member method `memberMethod` (of `java.lang.Object` of course), for example, `array:getClass()`.

::: tip
Lua tables usually start the index from 1, while Java arrays from 0.
:::

## `java` <Badge>module</Badge>

| Functions     | Signature                   | Returns           | Description                                            |
|---------------|-----------------------------|-------------------|--------------------------------------------------------|
| **`array`**   | `(jclass, dim1, ...)`       | `jarray`          | Create an array with specified dimensions              |
| **`catched`** | `()`                        | `jobject`         | Return the latest captured Java `Throwable`            |
| **`detach`**  | `(thread)`                  | `nil`             | Detach the sub-thread from registry to allow for GC    |
| **`import`**  | `(string)`                  | `jclass \| table` | Import a Java class or package                         |
| **`loadlib`** | `(string, string)`          | `function`        | Load a Java method, similar to `package.loadlib`       |
| **`luaify`**  | `(jobject)`                 | `any`             | Convert an object to Lua types if possible             |
| **`method`**  | `(jobject, string, string)` | `function`        | Find a method                                          |
| **`new`**     | `(jclass, ...)`             | `jobject`         | Call the constructor of the given Java type            |
| **`proxy`**   | `(string, ..., table)`      | `jobject`         | Create an object with all calls proxied to a Lua table |
| **`unwrap`**  | `(jobject)`                 | `table`           | Return the backing table of a proxy object             |

::: tip There's more!
Actually, if you load the built-in `package` library (either by `Lua#openLibraries()` or `Lua#openLibrary("package")`),
you can use the Lua `require` functions to load Java side things.

See [Java-Side Modules](./examples/modules.md) for a brief introduction.
:::

### `array (jclass, dim1, ...)` <Badge>function</Badge>

Creates a Java array.

- **Parameters:**

  - `jclass`: (***jclass*** | ***jobject***) The component type. One may pass a `jclass` or a `jobject` of `Class<?>`.

  - `dim1`: (***number***) The size of the first dimension.

  - `dim2`: (optional) (***number***) The size of the second dimension.

  - `dimN`: (optional) (***number***) The size of the N-th dimension.

- **Returns:**

  - (***jarray***) `new "jclass"[dim1][dim2]...[dimN]`

- Generates a Lua error if types mismatch or some dimensions are negative.

```lua
int = java.import('int')
arr = java.array(int, 2, 16)
assert(#arr == 2)
assert(#arr[1] == 16)
```

### `catched ()` <Badge>function</Badge>

Return the latest captured Java `java.lang.Throwable` during a Java method call.

- **Parameters:**

    - ***none***

- **Returns:**

    - (***jobject***) If some recent Java method call threw a `java.lang.Throwable`.

    - (***nil***) No `Throwable` was thrown, or they were cleared.

### `detach (thread)` <Badge>function</Badge>

Detach the sub-thread from registry to allow for GC.

- **Parameters:**

    - ***thread*** The thread (e.g., a return value of `coroutine.create`)

- **Returns:**

    - (***nil***)

- Generates a Lua error if the thread is a main thread.

::: danger Check before detaching

1. Most often, you only want to use `java.detach` on threads created on the Lua side.
2. You need to ensure that proxies created on that thread is no longer used.
3. If you are not creating tons of sub-threads, you can worry less about GC
   by letting `mainThread#close` handle it all instead of manually `detach`ing.

:::
::: details Thread interface explained

In LuaJava, an `AbstractLua` instance just wraps around a `lua_State *`.
We ensure that one `lua_State *` maps to no more than one `AbstractLua` instance
by assigning each state an ID when:
1. a main state is created;
2. or when a sub-thread is created on the Java side (with `Lua#newThread`);
3. or when a sub-thread, created on the Lua side (with `coroutine.create`),
   eventually requests for an ID if it finds it necessary.

IDs are stored both on:

- the Java side: IDs are stored in `AbstractLua` instances.
- and the Lua side: IDs are stored in the table at `LUA_REGISTRYINDEX`, *with the thread itself as the key*.

However, since we keep references to the thread in the `LUA_REGISTRYINDEX`, it prevents the thread from garbage collection
(which is intentional though, as you need threads alive for proxies).

If you are sure that neither the Java side (proxies, Java API, etc.) nor the Lua side uses the thread any more,
you may manually call `java.detach` or `Lua#close` to free the thread from the global registry.
:::

### `import (name)` <Badge>function</Badge>

Import a Java class or package.

- **Parameters:**

    - `name`: (***string***) Either of the following
      * The full name, including the package part, of the class.

      * Any string, appended with possibly multiple `.*`.

- **Returns:**

    - (***jclass***) If `name` is the name of a class, return a `jclass` of the class.

    - (***table***) If `name` is a string appended with `.*`, return a Lua table,
      which looks up classes directly under a package or inner classes inside a class when indexed.
      See the following example for details.

- Generates a Lua error if class not found.

```lua
lang = java.import('java.lang.*')
print(lang.System:currentTimeMillis())

R = java.import('android.R.*')
print(R.id.input)

j = java.import('java.*.*')
print(j.lang.System:currentTimeMillis())
-- Both works
j = java.import('java.*')
print(j.lang.System:currentTimeMillis())

System = java.import('java.lang.System')
print(System:currentTimeMillis())
```

### `loadlib (classname, method)` <Badge>function</Badge>

This function provides similar functionalities to Lua's `loadlib`. It looks for a method `static public int yourSuppliedMethodName(Lua L);` inside the class, and returns it as a C function.

- **Parameters:**

    - `classname`: (***string***) The class name.

    - `method`: (***string***) The method name.

      * We expect the method to accept a single `Lua` parameter and return an integer.

- **Returns:**

    - (***function***) If the method is found, we wrap it up with a C function wrapper and return it.

    - (***nil***, ***string***) If no valid method is found, we return `nil` plus a error message. Similar to `package.loadlib`, we do not generate a Lua error in this case.

You might also want to check out [Java-Side Modules](./examples/modules.md) to see how we use this function to extend the Lua `require`.

:::: code-group
::: code-group-item Java Library
```java
package com.example;

public class LuaLib {
    public static int open(Lua L) {
        L.createTable(0, 1);
        L.push(l -> {
            l.push(1024);
            return 1;
        });
        L.setField(-2, "getNumber");
        return 1;
    }
}
```
:::
::: code-group-item Java Side
```java
Lua L = new Lua51();
L.openLibrary("package");
```
:::
::: code-group-item Lua Side
```lua
local LuaLibOpen = java.loadlib('com.example.LuaLib.open')
assert(1024 == LuaLibOpen().getNumber())
```
:::
::::

### `luaify (jobject)` <Badge>function</Badge>

Converts a Java object into its Lua equivalence. It does a [`FULL` conversion](./conversions.md#java-to-lua). See [Type Conversions](./conversions) for more information.

- **Parameters:**

    - `jobject`: (***jobject***) The object to get converted.

- **Returns:**

    - (***boolean*** | ***integer*** | ***number*** | ***table*** | ***jclass***) Depending on the Java type of `jobject`.
      * Notably, it converts `Map<?, ?>` and `Collection<?>` to Lua tables, and `Class<?>` to `jclass`.

### `method (jobject, method[, signature])` <Badge>function</Badge>

Finds a method of the `jobject` or `jclass` matching the name and signature. See [Method Resolution](#method-resolution).

- **Parameters:**

    - `jobject`: (***jobject*** | ***jclass***) The object.

    - `method`: (***string***) The method name. Use `new` to refer to the constructor.

      For proxy object, it is possible to explicitly call the default methods in the interfaces.
      Use `complete.interface.name:methodName` to refer to the method. See the examples below.

    - `signature`: (optional) (***string***) Comma separated argument type list. If not supplied, treated as an empty one.

- **Returns:**

    - (***function***) Never `nil`. The real method lookup begins after you supply arguments to this returned function.

```lua {3-5}
AtomicInteger = java.import('java.util.concurrent.atomic.AtomicInteger')
Constructor = java.method(AtomicInteger, 'new', 'int')
integer = Constructor(100)
compareAndSet = java.method(integer, 'compareAndSet', 'int,int')
compareAndSet(100, 200)
compareAndSet(200, 400)
assert(integer:get() == 400)

iter = java.proxy('java.util.Iterator', {
  remove = function(this)
    java.method(iter, 'java.util.Iterator:remove')()
  end
})
-- iter:remove() -- This throws an exception
```

### `new (jclass, ...)` <Badge>function</Badge>

Call the constructor of the given Java type.

- **Parameters:**

    - `jclass`: (***jclass*** | ***jobject***) The class. One may pass a `jclass` or a `jobject` of `Class<?>`.

    - `...`: (***any***) Extra parameters are passed to the constructor. See also [Type Conversions](./conversions) to find out how we locate a matching method.

- **Returns:**

    - (***jobject***) The created object.

- Generates a Lua error if exceptions occur or unable to locate a matching constructor.

Examples:

```lua
String = java.import('java.lang.String')

--         new String ("This is the content of the String")
str = java.new(String, 'This is the content of the String')
```

### `proxy (jclass, ..., table)` <Badge>function</Badge>

Creates a Java object implementing the specified interfaces, proxying calls to the underlying Lua table.
See also [Proxy Caveats](./proxy.md).

- **Parameters:**

    - `jclass1`: (***jclass*** | ***string*** | ***jobject***) The first interface. One may pass a `jclass` or a `string` or a `jobject` of `Class<?>`.
    - `jclass2`: (***jclass*** | ***string*** | ***jobject***) The second interface.
    - `jclassN`: (***jclass*** | ***string*** | ***jobject***) The N-th interface.

    - `table`: (***table*** | ***function***)

        - This parameter can be a table implementing the all the methods in the interfaces.

        - Or, if the interfaces sum up to a [functional interface](https://docs.oracle.com/javase/specs/jls/se8/html/jls-9.html#jls-9.8) of wider sense
          (that is, we allow different signatures as long as they share the same name),
          an intermediate table will be created and back the actual proxy automatically.

- **Returns:**

    - (***jobject***) The created object.

- Generates a Lua error if exceptions occur or unable to find the interfaces.

```lua
button = java.new(java.import('java.awt.Button'), 'Execute')
callback = {}
function callback:actionPerformed(ev)
  -- do something
end

buttonProxy = java.proxy('java.awt.ActionListener', callback)
button:addActionListener(buttonProxy)
```

### `unwrap (jobject)` <Badge>function</Badge>

Return the backing table of a proxy object.
See also [Proxy Caveats](./proxy.md).

- **Parameters:**

    - `jobject`: (***jobject***) The proxy object created with [`java.proxy`](#proxy-jclass-table-function) or [`party.iroiro.luajava.Lua#createProxy`](./javadoc/party/iroiro/luajava/Lua.html#createProxy(java.lang.Class%5B%5D,party.iroiro.luajava.Lua.Conversion))

- **Returns:**

    - (***jobject***) The backing Lua table of the Lua proxy.

- Generates a Lua error if the object is not a Lua proxy object, or belongs to another irrelevant Lua state.

## Proxied Method Calls

Java allows method overloading, which means we cannot know which method you are calling until you supply the parameters. Method finding and parameter supplying is integrated in Java.

However, for calls in Lua, the two steps can get separated:

```lua
obj:method(param1)
-- The above is actually:
m = obj.method
m(obj, param1)
```

To proxy calls to Java, we treat all missing fields, such as `obj.method`, `obj.notAField`, `obj.whatever` as a possible method call. The real resolution starts only after you supply the parameters.

The side effect of this is that a missing field is never `nil` but always a possible `function` call, so don't depend on this.

```lua
assert(type(jobject.notAField) == 'function')
```

### Method resolution

In either case, if no method matches, a Lua error is raised.

#### With `jobject:method(...)`

For method resolution, see [Type Conversions](./conversions.md#lua-to-java).

Since a Lua type maps to different Java types (for example, `lua_Number` may be mapped to any Java numerical type), we have to iterate through every method to find one matching Lua parameters. For each possible method, we try to convert the values on stack from Lua to Java. If such conversion is possible, the call is then proxied to this method and the remaining methods are never tried.

::: warning
By the nature of this procedure, we do not prioritize any of the method.

For example, if you are calling `java.lang.Math.max`, which can be `Math.max(int, int)`, `Math.max(double, double)`, etc., then nobody knows which will ever get called.
:::

::: warning
We do not support varargs. You will need to combine `java.method` and `java.array` to make that happen.

For `Object... object` however, things are easier:

```lua
String = java.import('java.lang.String')
-- We automatically convert lua tables into Object[]
assert(String:format('>>> %s', { 'content' }) == '>>> content')
```
:::

#### With `java.method`

To help with precisely calling a specific method, we provide [`java.method`](#method-jobject-method-signature-function),
to which you may specify the signature of the method that you intend to call.

::: tip
Take the above `java.lang.Math.max` as an example. You may call `Math.max(int, int)` with the following:

```lua {2}
Math = java.import('java.lang.Math')
max = java.method(Math, 'max', 'int,int')
assert(max(1.2, 2.3) == 2)
```

You may call `Math.max(double, double)` with the following:

```lua {2}
Math = java.import('java.lang.Math')
max = java.method(Math, 'max', 'double,double')
assert(max(1.2, 2.3) == 2.3)
```
:::

If you would like to access an overridden default method from a proxy object,
you may also use:

```lua
iter1 = java.proxy('java.util.Iterator', {})
-- Calls the default method
iter1:remove()

-- What if we want to access the default method from a overridden one?
iterImpl = {
  remove = function(this)
    -- Calls the default method from java.util.Iterator.
    java.method(this, 'java.util.Iterator:remove', '')()
    -- Equivalent to the following in Java
    --     Iterator.super.remove();
  end
}

iter = java.proxy('java.util.Iterator', iterImpl)
-- Calls the implemented `remove`, which then calls the default one
iter:remove()
```

::: warning

Calling default methods is not available with LuaJ bindings,
since the Java reflection does not provide a way to do so.
(We use JNI functions to achieve this within binary bindings.)

:::
