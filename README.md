# non.luajava #
[![Build Status](https://travis-ci.org/non2d/non.luajava.svg?branch=master)](https://travis-ci.org/non2d/non.luajava)

### Supported platforms ###

* Windows
* Linux
* Mac OS X
* Android (and Ouya)
* iOS

### Compiling ###

To compile and pack natives for all platforms:

```shell
ant
```

To only compile natives:

```shell
ant compile
```

To only pack natives what was compiled before

```shell
ant pack
```

To clean compile results

```shell
ant clean
```

To compile natives only for specified platform (for example for Linux), run specified ant target:

```shell
ant compile-linux
```

targets can be:

* compile-linux
* compile-windows
* compile-mac
* compile-ios
* compile-android