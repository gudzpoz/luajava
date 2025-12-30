#!/usr/bin/env sh
# https://github.com/mjansson/lua_lib/blob/master/lua/luajit/build-ios.sh

LUAJIT=.
DEVDIR=`xcode-select -print-path`
IOSSDKVER=8.0
SIMVER=8.1
IOSDIR=$DEVDIR/Platforms
IOSBIN=$DEVDIR/Toolchains/XcodeDefault.xctoolchain/usr/bin/

BUILD_DIR=$LUAJIT/lib/ios

rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR
rm *.a 1>/dev/null 2>/dev/null

echo "########## Building for arm64 (iOS) ##########"
ISDKF="-arch arm64 -isysroot $IOSDIR/iPhoneOS.platform/Developer/SDKs/iPhoneOS.sdk -miphoneos-version-min=$IOSSDKVER"
make -j -C $LUAJIT HOST_CC="xcrun clang -m64 -arch x86_64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS clean
make -j -C $LUAJIT HOST_CC="xcrun clang -m64 -arch x86_64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS amalg
mkdir -p $BUILD_DIR/arm-device
mv $LUAJIT/src/libluajit.a $BUILD_DIR/arm-device/libluajit.a

echo "########## Building for arm64 (iOS simulator) ##########"
ISDKF="-arch arm64 -isysroot $IOSDIR/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator.sdk -mios-simulator-version-min=$SIMVER"
make -j -C $LUAJIT HOST_CC="xcrun clang -m64 -arch x86_64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS clean
make -j -C $LUAJIT HOST_CC="xcrun clang -m64 -arch x86_64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS amalg
mkdir -p $BUILD_DIR/arm-simulator
mv $LUAJIT/src/libluajit.a $BUILD_DIR/arm-simulator/libluajit.a

echo "########## Building for x86_64 (iOS simulator) ##########"
ISDKF="-arch x86_64 -isysroot $IOSDIR/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator.sdk -miphoneos-version-min=$IOSSDKVER"
make -j -C $LUAJIT HOST_CC="clang -m64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS clean
make -j -C $LUAJIT HOST_CC="clang -m64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS amalg
mkdir -p $BUILD_DIR/x86-simulator
mv $LUAJIT/src/libluajit.a $BUILD_DIR/x86-simulator/libluajit.a

ls $BUILD_DIR/*/
