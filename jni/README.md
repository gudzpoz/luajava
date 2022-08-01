# JNI Notes

There are a few things that one should take note of.

1. Lua makes heavy use of `longjmp`, which simply breaks C++ deconstructor logic. Therefore:
   - Ha, RAII is gone forever. 
   - You are only safe with stack-allocated variables. Stay away from `new`.
   - If you indeed want to allocate memory for things, use `lua_newuserdata` and let Lua handle the GC.
   - Do not use classes from the standard library unless you are sure that no logic is put in the deconstructor.
   - Usually functions from `#include <c...>` headers are safe, which simply wrap around the C funciton.
3. Some platforms (e.g. Android or iOS) might forbid the use of `try` `catch` blocks. Do not use them.
4. Lua is written in C and puts a lot of names under the global namespace.
   Since we are doing an amalgamated compilation, there are risks of name collision.
   - For example, Lua defines a `next` symbol / macro, which collides with the standard iterator interface. Take care.
