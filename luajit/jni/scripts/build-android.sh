#!/usr/bin/env sh
# https://github.com/mjansson/lua_lib/blob/master/lua/luajit/build-android.sh
# https://github.com/LuaJIT/LuaJIT/issues/440#issuecomment-438809840

LUAJIT=.

BUILD_DIR=$LUAJIT/lib/android

rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR
rm *.a 1>/dev/null 2>/dev/null

HOST_OS=linux
NDK="${ANDROID_NDK_HOME:-$ANDROID_NDK_LATEST_HOME}"
TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/$HOST_OS-x86_64
NDKB=$TOOLCHAIN/bin
NDKAPI=21

echo "########## Building armv7-a ##########"
TARGET=armv7a-linux-androideabi
NDKP=$NDKB/${TARGET}-
NDKCC=$NDKB/${TARGET}${NDKAPI}-clang
NDKARCH="-march=armv7-a -mhard-float -mfpu=vfpv3-d16 -mfloat-abi=softfp -D_NDK_MATH_NO_SOFTFP=1 -marm -DNO_RTLD_DEFAULT=1"
make HOST_CC="gcc -m32 -I/usr/i686-linux-gnu/include" CROSS=$NDKP \
     STATIC_CC=$NDKCC DYNAMIC_CC="$NDKCC -fPIC" \
     TARGET_LD=$NDKCC TARGET_AR="$NDKB/llvm-ar rcus" TARGET_STRIP=$NDKB/llvm-strip \
     CFLAGS=-fPIC TARGET_FLAGS="$NDKARCH" \
     clean
make HOST_CC="gcc -m32 -I/usr/i686-linux-gnu/include" CROSS=$NDKP \
     STATIC_CC=$NDKCC DYNAMIC_CC="$NDKCC -fPIC" \
     TARGET_LD=$NDKCC TARGET_AR="$NDKB/llvm-ar rcus" TARGET_STRIP=$NDKB/llvm-strip \
     CFLAGS=-fPIC TARGET_FLAGS="$NDKARCH" \
     amalg
mkdir -p $BUILD_DIR/armeabi-v7a
mv $LUAJIT/src/libluajit.a $BUILD_DIR/armeabi-v7a/libluajit.a

echo "########## Building i686 ##########"
TARGET=i686-linux-android
NDKP=$NDKB/${TARGET}-
NDKCC=$NDKB/${TARGET}${NDKAPI}-clang
NDKARCH="-DNO_RTLD_DEFAULT=1"
make HOST_CC="gcc -m32 -I/usr/i686-linux-gnu/include" CROSS=$NDKP \
     STATIC_CC=$NDKCC DYNAMIC_CC="$NDKCC -fPIC" \
     TARGET_LD=$NDKCC TARGET_AR="$NDKB/llvm-ar rcus" TARGET_STRIP=$NDKB/llvm-strip \
     CFLAGS=-fPIC TARGET_FLAGS="$NDKARCH" \
     clean
make HOST_CC="gcc -m32 -I/usr/i686-linux-gnu/include" CROSS=$NDKP \
     STATIC_CC=$NDKCC DYNAMIC_CC="$NDKCC -fPIC" \
     TARGET_LD=$NDKCC TARGET_AR="$NDKB/llvm-ar rcus" TARGET_STRIP=$NDKB/llvm-strip \
     CFLAGS=-fPIC TARGET_FLAGS="$NDKARCH" \
     amalg
mkdir $BUILD_DIR/x86
mv $LUAJIT/src/libluajit.a $BUILD_DIR/x86/libluajit.a

NDKAPI=21

echo "########## Building arm64-v8a ##########"
TARGET=aarch64-linux-android
NDKP=$NDKB/${TARGET}-
NDKCC=$NDKB/${TARGET}${NDKAPI}-clang
NDKARCH="-DLJ_ABI_SOFTFP=0 -DLJ_ARCH_HASFPU=1 -DLUAJIT_ENABLE_GC64=1 -DNO_RTLD_DEFAULT=1"
make HOST_CC="gcc -m64" CROSS=$NDKP \
     STATIC_CC=$NDKCC DYNAMIC_CC="$NDKCC -fPIC" \
     TARGET_LD=$NDKCC TARGET_AR="$NDKB/llvm-ar rcus" TARGET_STRIP=$NDKB/llvm-strip \
     CFLAGS=-fPIC TARGET_FLAGS="$NDKARCH" \
     clean
make HOST_CC="gcc -m64" CROSS=$NDKP \
     STATIC_CC=$NDKCC DYNAMIC_CC="$NDKCC -fPIC" \
     TARGET_LD=$NDKCC TARGET_AR="$NDKB/llvm-ar rcus" TARGET_STRIP=$NDKB/llvm-strip \
     CFLAGS=-fPIC TARGET_FLAGS="$NDKARCH" \
     amalg
mkdir -p $BUILD_DIR/arm64-v8a
mv $LUAJIT/src/libluajit.a $BUILD_DIR/arm64-v8a/libluajit.a

echo "########## Building x86_64 ##########"
TARGET=x86_64-linux-android
NDKP=$NDKB/${TARGET}-
NDKCC=$NDKB/${TARGET}${NDKAPI}-clang
NDKARCH="-DLUAJIT_ENABLE_GC64=1 -DNO_RTLD_DEFAULT=1"
make HOST_CC="gcc -m64" CROSS=$NDKP \
     STATIC_CC=$NDKCC DYNAMIC_CC="$NDKCC -fPIC" \
     TARGET_LD=$NDKCC TARGET_AR="$NDKB/llvm-ar rcus" TARGET_STRIP=$NDKB/llvm-strip \
     CFLAGS=-fPIC TARGET_FLAGS="$NDKARCH" \
     clean
make HOST_CC="gcc -m64" CROSS=$NDKP \
     STATIC_CC=$NDKCC DYNAMIC_CC="$NDKCC -fPIC" \
     TARGET_LD=$NDKCC TARGET_AR="$NDKB/llvm-ar rcus" TARGET_STRIP=$NDKB/llvm-strip \
     CFLAGS=-fPIC TARGET_FLAGS="$NDKARCH" \
     amalg
mkdir -p $BUILD_DIR/x86_64
mv $LUAJIT/src/libluajit.a $BUILD_DIR/x86_64/libluajit.a
