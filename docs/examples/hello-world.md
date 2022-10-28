# Hello World

```java
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua51.Lua51;

public class Hello {
  public static void main(String[] args) {
    try (Lua L = new Lua51()) {
      L.openLibraries();
      L.run("System = java.import('java.lang.System')");
      L.run("System.out:println('Hello World from Lua!')");
    }
  }
}
```
