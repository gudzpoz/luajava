# Lua API

## Extra Lua Types

We provide three extra `userdata` types that correspond three Java concepts. We use their abbreviations in the following documentation.

|              | [Java classes](#jclass-type) | [Java objects](#jobject-type) | [Java arrays](#jarray-type) |
|--------------|:----------------------------:|:-----------------------------:|:----------------------------|
| Abbreviation |   [`jclass`](#jclass-type)   |  [`jobject`](#jobject-type)   | [`jarray`](#jarray-type)    |

### `jclass` <Badge>type</Badge>

For a `jclass` `clazz`:

- `clazz.memberVar` returns the public static member named `memberVar`.
- `clazz.memberVar = value` assigns to the public static member if possible.
- `clazz:memberMethod(...)` calls the public static member method `memberMethod`. See [Proxied Method Calls](#proxied-method-calls) for more info.
- `class(...)` calls the corresponding constructor. See [`java.new`](#new-jclass-function).
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
- `object.memberVar = value` assigns to the public static member if possible.
- `object:memberMethod(...)` calls the public member method `memberMethod`. See [Proxied Method Calls](#proxied-method-calls) for more info.

```lua
Integer = java.import('java.lang.Integer')
i = java.new(Integer, 1024)
-- Calling a method
print(i:toString())
```

### `jarray` <Badge>type</Badge>

For a `jarray` `array`:

- `array[i]` returns `array[i - 1]`.
- `array[i] = value` assigns to `array[i - 1]`.

::: tip
Lua tables usually start the index from 1, while Java arrays from 0.
:::

## `java` <Badge>module</Badge>

| Functions    | Signature                   | Returns           | Description                                            |
|--------------|-----------------------------|-------------------|--------------------------------------------------------|
| **`import`** | `(string)`                  | `jclass \         | table`                                                 | Import a Java class or package                         |
| **`new`**    | `(jclass, ...)`             | `jobject`         | Call the constructor of the given Java type            |
| **`proxy`**  | `(string, ..., table)`      | `jobject`         | Create an object with all calls proxied to a Lua table |
| **`luaify`** | `(jobject)`                 | `any`             | Convert an object to Lua types if possible             |
| **`method`** | `(jobject, string, string)` | `function`        | Find a method                                          |
| **`array`**  | `(jclass, dim1, ...)`       | `jarray`          | Create an array with specified dimensions              |

### `import (name)` <Badge>function</Badge>

Import a Java class or package.

- **Parameters:**

    - `name`: (***string***) Either of the following
      * The full name, including the package part, of the class.

      * A package name, appended with `.*`.

- **Returns:**

    - (***jclass***) If `name` is the name of a class, return a `jclass` of the class.

    - (***table***) If `name` is a package name, appended with `.*`, return a Lua table, including all classes directly under the package.

    - (***nil***) If not found.

```lua
lang = java.import('java.lang.*')
print(lang.System:currentTimeMillis())

System = java.import('java.lang.System')
print(System:currentTimeMillis())
```

### `new (jclass, ...)` <Badge>function</Badge>

Call the constructor of the given Java type.

- **Parameters:**

    - `jclass`: (***jclass*** | ***jobject***) The class. One may pass a `jclass` or a `jobject` of `Class<?>`.

    - `...`: (***any***) Extra parameters are passed to the constructor. See also [Type Conversions](./conversions) to find out how we locate a matching method.

- **Returns:**

    - (***jobject***) The created object.

    - (***nil***) If exceptions occur or unable to locate a matching constructor.

Examples:

```lua
String = java.import('java.lang.String')

--         new String ("This is the content of the String")
str = java.new(String, 'This is the content of the String')
```

### `proxy (jclass, ..., table)` <Badge>function</Badge>

Creates a Java object implementing the specified interfaces, proxying calls to the underlying Lua table.

- **Parameters:**

    - `jclass1`: (***jclass*** | ***string*** | ***jobject***) The first interface. One may pass a `jclass` or a `string` or a `jobject` of `Class<?>`.
    - `jclass2`: (***jclass*** | ***string*** | ***jobject***) The second interface.
    - `jclassN`: (***jclass*** | ***string*** | ***jobject***) The N-th interface.

    - `table`: (***table***) The table implementing the all the methods in the interfaces.

- **Returns:**

    - (***jobject***) The created object.

    - (***nil***) If exceptions occur or unable to find the interfaces.

```lua
button = java.new(java.import('java.awt.Button'), 'Execute')
callback = {}
function callback:actionPerformed(ev)
  -- do something
end

buttonProxy = java.proxy('java.awt.ActionListener', callback)
button:addActionListener(buttonProxy)
```

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
```

### `array (jclass, dim1, ...)` <Badge>function</Badge>

Creates a Java array.

- **Parameters:**

  - `jclass`: (***jclass*** | ***jobject***) The component type. One may pass a `jclass` or a `jobject` of `Class<?>`. 

  - `dim1`: (***number***) The size of the first dimension.

  - `dim2`: (optional) (***number***) The size of the second dimension.

  - `dimN`: (optional) (***number***) The size of the N-th dimension.

- **Returns:**

  - (***jarray***) `new "jclass"[dim1][dim2]...[dimN]`

  - (***nil***) If types mismatch or some dimensions are negative.

```lua
int = java.import('int')
arr = java.array(int, 2, 16)
assert(#arr == 2)
assert(#arr[1] == 16)
```

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

#### With `jobject:method(...)`

For method resolution, see [Type Conversions](./conversions.md#lua-to-java).

Since a Lua type maps to different Java types (for example, `lua_Number` may be mapped to any Java numerical type), we have to iterate through every method to find one matching Lua parameters. For each possible method, we try to convert the values on stack from Lua to Java. If such conversion is possible, the call is then proxied to this method and the remaining methods are never tried.

::: warning
By the nature of this procedure, we do not prioritize any of the method.

For example, if you are calling `java.lang.Math.max`, which can be `Math.max(int, int)`, `Math.max(double, double)`, etc., then nobody knows which will ever get called.
:::

#### With `java.method`

To help with precisely calling a specific method, we provide [`java.method`](#method-jobject-method-signature-function), to which you may specify the signature of the method that you intend to call.

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
