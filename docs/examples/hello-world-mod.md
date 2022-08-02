# Advanced Hello World

::: tip
You may try this with [the pre-built Console](../console.md).

<ClientOnly><Asciinema :file="$withBase('/hello.cast')" /></ClientOnly>

:::

```lua
print = java.method(java.import('java.lang.System').out,'println','java.lang.Object')
Ansi = java.import('org.fusesource.jansi.Ansi')
thread = java.import('java.lang.Thread')(function()
  print(Ansi:ansi():render('@|magenta,bold Hello World |@'))
end)

thread:start()
```
