# Interactive Console

We build a tiny console application with [JLine3](https://github.com/jline/jline3) every release. You may download the `example-all.jar` from our [Release page](https://github.com/gudzpoz/luajava/releases). Or you can get the lastest snapshot from [our workflow artifacts](https://github.com/gudzpoz/luajava/actions/workflows/build-natives.yml): choose the lastest build and find the `example` artifact.

The jar bundles desktop natives with it, so you should be about to play around with it on your own computer.

<ClientOnly><Asciinema :file="$withBase('/example.cast')" /></ClientOnly>

`Lua Version` can be any of `5.1`, `5.2`, `5.3`, `5.4` or `jit`.
