if [ "x$NDK" == "x" ]; then
    NDK=/opt/android-ndk
fi

if [ "x$BUILD_ARCH" == "x" ]; then
    BUILD_ARCH=linux-$(uname -m)
fi

CUR=$(cd $(dirname $0) && pwd)
LJL=$CUR/luajit/src/libluajit.a
DEST=$CUR/ext/$1

case "$1" in
clean)
    make -C luajit clean
    ;;
windows32)
    # Windows 32
    make -C luajit HOST_CC="gcc -m32" CROSS=i686-w64-mingw32- TARGET_SYS=Windows DESTDIR="$DEST" PREFIX=
    ;;
windows64)
    # Windows 64
    make -C luajit HOST_CC="gcc" CROSS=x86_64-w64-mingw32- TARGET_SYS=Windows DESTDIR="$DEST" PREFIX=
    ;;
linux32)
    # Android/MIPS, mips (MIPS32R1 hard-float), Android 4.0+ (ICS)
    make -C luajit HOST_CC="gcc -m32" TARGET_SYS=Linux DESTDIR="$DEST" PREFIX=
    ;;
linux64)
    # Android/MIPS, mips (MIPS32R1 hard-float), Android 4.0+ (ICS)
    make -C luajit HOST_CC="gcc" TARGET_SYS=Linux DESTDIR="$DEST" PREFIX=
    ;;
armeabi)
    # Android/ARM, armeabi (ARMv5TE soft-float), Android 2.2+ (Froyo)
    NDKABI=8
    NDKVER=$NDK/toolchains/arm-linux-androideabi-4.8
    NDKP=$NDKVER/prebuilt/$BUILD_ARCH/bin/arm-linux-androideabi-
    NDKF="--sysroot $NDK/platforms/android-$NDKABI/arch-arm"
    make -C luajit HOST_CC="gcc -m32" CROSS=$NDKP TARGET_FLAGS="$NDKF" DESTDIR="$DEST" PREFIX=
    ;;
armeabi-v7a)
    # Android/ARM, armeabi-v7a (ARMv7 VFP), Android 4.0+ (ICS)
    NDKABI=14
    NDKVER=$NDK/toolchains/arm-linux-androideabi-4.8
    NDKP=$NDKVER/prebuilt/$BUILD_ARCH/bin/arm-linux-androideabi-
    NDKF="--sysroot $NDK/platforms/android-$NDKABI/arch-arm"
    NDKARCH="-march=armv7-a -mfloat-abi=softfp -Wl,--fix-cortex-a8"
    make -C luajit HOST_CC="gcc -m32" CROSS=$NDKP TARGET_FLAGS="$NDKF $NDKARCH" DESTDIR="$DEST" PREFIX=
    ;;
mips)
    # Android/MIPS, mips (MIPS32R1 hard-float), Android 4.0+ (ICS)
    NDKABI=14
    NDKVER=$NDK/toolchains/mipsel-linux-android-4.8
    NDKP=$NDKVER/prebuilt/$BUILD_ARCH/bin/mipsel-linux-android-
    NDKF="--sysroot $NDK/platforms/android-$NDKABI/arch-mips"
    make -C luajit HOST_CC="gcc -m32" CROSS=$NDKP TARGET_FLAGS="$NDKF" DESTDIR="$DEST" PREFIX=
    ;;
x86)
    # Android/x86, x86 (i686 SSE3), Android 4.0+ (ICS)
    NDKABI=14
    NDKVER=$NDK/toolchains/x86-4.8
    NDKP=$NDKVER/prebuilt/$BUILD_ARCH/bin/i686-linux-android-
    NDKF="--sysroot $NDK/platforms/android-$NDKABI/arch-x86"
    make -C luajit HOST_CC="gcc -m32" CROSS=$NDKP TARGET_FLAGS="$NDKF" DESTDIR="$DEST" PREFIX=
    ;;

ios)
    IXCODE=xcode-select -print-path
    ISDK=$IXCODE/Platforms/iPhoneOS.platform/Developer
    ISDKVER=iPhoneOS6.0.sdk
    ISDKP=$ISDK/usr/bin/
    ISDKF="-arch armv7 -isysroot $ISDK/SDKs/$ISDKVER"
    make HOST_CC="gcc -m32 -arch i386" CROSS=$ISDKP TARGET_FLAGS="$ISDKF" TARGET_SYS=iOS
    echo 'specify one of "windows32", "windows64", "linux32", "linux64", "armeabi", "armeabi-v7a", "mips", "x86", "ios" or "clean" as first argument'
    exit 1
    ;;
*)
esac

if [ "$1" != "clean" ]; then
    cp $LJL $DEST
fi