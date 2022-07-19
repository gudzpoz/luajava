# Type Conversions

Exchanging values between Java and Lua, we allow users to control how values are converted back and forth. You might want to read [#Extra Lua Types](./api.md#extra-lua-types) first.

## Java to Lua

When pushing a Java value onto the Lua stack, you can choose from doing a `FULL` conversion, a `SEMI` conversion  or `NONE` of the conversions by supplying a second parameter of type `Lua.Conversion`.

| Java Types                    | [`NONE`](#lua-conversion-none) | [`SEMI`](#lua-conversion-semi) | [`FULL`](#lua-conversion-full) | Conversion (if ever)               |
|:------------------------------|:------------------------------:|:------------------------------:|:------------------------------:|------------------------------------|
| `null`                        | :white_check_mark:             | :white_check_mark:             | :white_check_mark:             | Lua `nil`                          |
| `LuaValue`                    | :orange_square:                | :orange_square:                | :orange_square:                | Converted if sharing main state    |
| `Boolean`                     |                                | :white_check_mark:             | :white_check_mark:             | Lua booleans                       |
| `Character`                   |                                | :white_check_mark:             | :white_check_mark:             | `lua_Integer` or `lua_Number`      |
| Boxed numerics (`Integer`...) |                                | :white_check_mark:             | :white_check_mark:             | `lua_Integer` or `lua_Number`      |
| `String`                      |                                | :white_check_mark:             | :white_check_mark:             | Lua strings                        |
| `JFunction`                   |                                | :white_check_mark:             | :white_check_mark:             | A Lua closure                      |
| Java arrays                   |                                |                                | :white_check_mark:             | Lua tables (index starting from 1) |
| `Collections<?>`              |                                |                                | :white_check_mark:             | Lua tables (index starting from 1) |
| `Map<?, ?>`                   |                                |                                | :white_check_mark:             | Lua tables                         |
| `Class<?>`                    |                                |                                | :white_check_mark:             | `jclass`                           |
| Others                        |                                |                                |                                | Proxied to the Java side           |
| Example: `BigInteger`         |                                |                                |                                | Proxied to the Java side           |
| Example: `AtomicInteger`      |                                |                                |                                | Proxied to the Java side           |

::: tip
For a `SEMI`-conversion, roughly speaking, **immutable** types are converted, while mutable types, as well as those types not having Lua counterparts, are not.

For a `FULL`-conversion, all values are **recursively** converted if possible. Note that we ignore entries in `Map<?>` with a `null` key or a `null` value.

When calling Java methods from Lua, we `SEMI`-convert the return value. Currently there is no way to specify how you want the return value converted.
:::

::: warning Examples
- **`NONE`**:

  You cannot add `jobject` types up, even if their underlying class is `Integer`.

  ```java {5}
  Lua L = new Lua51();
  L.push(1, Lua.Conversion.NONE);
  L.setGlobal("i");
  assert OK == L.run("print(i:hashCode())");
  assert OK != L.run("print(i + 1)");
  assert OK == L.run("print(java.luaify(i) + 1)");
  ```

- **`FULL`**:

  Changes in converted Lua objects are not propagated back to the original Java object.

  ```java {4-5}
  int[] array = new int[] { 100 };
  L.push(array, Lua.Conversion.FULL);
  L.setGlobal("array");
  assert L.run("array[1] = 1024") == OK;
  assert 100 == array[0];
  ```
:::

## Lua to Java

1. ***nil*** is converted to `null`.
2. ***boolean*** converted to `boolean` or the boxed `Boolean`.
3. ***integer*** / ***number*** to any of `boolean` `char` `byte` `short` `int` `long` `float` `double` or their boxed alternative. (`Double` is preferred.)

  Trying to convert a number into an `Object` will always yield a boxed `Double`.
  So pay attention when you use `Object::equals` for example.

4. ***string*** to `String`.
5. ***table*** to `Map<Object, Object>`, `List<Object&gt;` or `Object[]` , converted recursively. (`Map<Object, Object>` is preferred.)
6. ***jclass*** to `Class<?>`.
7. ***jobject*** to the underlying Java object.
8. Any type can get wrapped into a `LuaValue`.
9. If all the above is not applicable, the result is `null` on the Java side.

::: warning
Currently, you cannot convert a C closure back to a `JFunction`, even if the closure simply wraps around `JFunction`.
:::
