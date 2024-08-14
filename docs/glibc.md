# GNU C Library (glibc) Compatibility

## glibc

The GNU C Library (glibc) is using [symbol versioning](https://refspecs.linuxfoundation.org/LSB_3.0.0/LSB-PDA/LSB-PDA.junk/symversion.html)
to provide backward compatibility for binaries compiled with older glibc.
We use `.symver` and [patchelf](https://github.com/NixOS/patchelf) to compile our binaries with newer glibc
while keeping compatibility with older ones.

The required glibc versions are listed below:

<!--
$ for v in 64 32 arm64 arm32; do \
    echo - - \`linux$v\`:; \
    echo "  " \`\`\`; \
    nm --dynamic --undefined-only --with-symbol-versions lua*/libs/linux$v/*.so \
    | grep GLIBC | sed -e 's#.\+@#  #' | sort --unique; \
    echo "  " \`\`\`; \
  done
-->

- `linux64`:
   ```
  GLIBC_2.11
  GLIBC_2.2.5
  GLIBC_2.3
  GLIBC_2.3.4
  GLIBC_2.4
  GLIBC_2.7
   ```
- `linux32`:
   ```
  GLIBC_2.0
  GLIBC_2.1
  GLIBC_2.11
  GLIBC_2.1.3
  GLIBC_2.2
  GLIBC_2.3
  GLIBC_2.3.4
  GLIBC_2.4
  GLIBC_2.7
   ```
- `linuxarm64`:
   ```
  GLIBC_2.0
  GLIBC_2.17
   ```
- `linuxarm32`:
   ```
  GLIBC_2.11
  GLIBC_2.4
  GLIBC_2.7
   ```

## musl

For example, on [Alpine Linux](https://alpinelinux.org/), you will very likely need [gcompat](https://git.adelielinux.org/adelie/gcompat)
(as well as `libstdc++`) to use the library.

Here is a `Dockerfile` snippet that is used to test this library on Alpine.

<<< ../example/docker/musl/Dockerfile
