# non.luajava #

### Supported platforms ###

* Windows
* Linux
* Mac OS X
* Android
* iOS

### Compiling ###

Navigate to `jni/` directory and execute

```shell
ant
```

To build natives for all platforms.

To build natives only for specified platforms (for example for 32bit Linux), run specified ant file:

```shell
ant -f build-linux32.xml
```