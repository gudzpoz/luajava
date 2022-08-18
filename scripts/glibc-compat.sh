#!/bin/bash

# (Somehow) automates finding the versions of symbols
PLATFORMS=(
    LJ_TARGET_ARM64
    LJ_TARGET_ARM
    LJ_64
    LJ_32
)

function find_symbols {
    libs=($(eval echo \${$1[@]}))
    symbols=($(eval echo \${$2[@]}))
    started=""
    for i in ${!PLATFORMS[@]}; do
        if [ -n "$started" ]; then
            echo "#elif ${PLATFORMS[$i]}"
        else
            echo "#if ${PLATFORMS[$i]}"
            started=true
        fi
        for symbol in ${symbols[@]}; do
            result=$(nm --dynamic --with-symbol-versions "${libs[$i]}" \
                | grep --word-regexp "$symbol" \
                | grep --invert-match '@@' \
                | awk -F'@' '{ print $2 }')
            echo "__asm__(\".symver ${symbol},${symbol}@${result}\");"
        done
    done
    echo "#endif"
}

# This is accustomed to Arch Linux paths.
GLIBC_MATH_LIBS=(
    /usr/aarch64-linux-gnu/lib/libm.so.6
    /usr/arm-linux-gnueabihf/lib/libm.so.6
    /usr/lib/libm.so.6
    /usr/lib32/libm.so.6
)

USED_MATH_SYMBOLS=(
    exp
    log
    log2
    pow
)

find_symbols GLIBC_MATH_LIBS USED_MATH_SYMBOLS

# This is accustomed to Arch Linux paths.
GLIBC_DL_LIBS=(
    /usr/aarch64-linux-gnu/lib/libc.so.6
    /usr/arm-linux-gnueabihf/lib/libc.so.6
    /usr/lib/libc.so.6
    /usr/lib32/libc.so.6
)

USED_DL_SYMBOLS=(
    dlclose
    dlerror
    dlopen
    dlsym
)

find_symbols GLIBC_DL_LIBS USED_DL_SYMBOLS
