#!/usr/bin/env sh
# https://github.com/mjansson/lua_lib/blob/master/lua/luajit/build-ios.sh

LUAJIT=.
DEVDIR=`xcode-select -print-path`
IOSSDKVER=8.0
SIMVER=8.1
IOSDIR=$DEVDIR/Platforms
IOSBIN=$DEVDIR/Toolchains/XcodeDefault.xctoolchain/usr/bin/

BUILD_DIR=$LUAJIT/lib/ios
OUTPUT_DIR=$LUAJIT/lib/iosa

rm -rf $BUILD_DIR
mkdir -p $BUILD_DIR
rm *.a 1>/dev/null 2>/dev/null
mkdir -p $OUTPUT_DIR

echo "########## Building for arm64 (iOS) ##########"
ISDKF="-arch arm64 -isysroot $IOSDIR/iPhoneOS.platform/Developer/SDKs/iPhoneOS.sdk -miphoneos-version-min=$IOSSDKVER"
make -j -C $LUAJIT HOST_CC="xcrun clang -m64 -arch x86_64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS clean
make -j -C $LUAJIT HOST_CC="xcrun clang -m64 -arch x86_64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS amalg
mkdir -p $BUILD_DIR/arm64
mv $LUAJIT/src/libluajit.a $BUILD_DIR/arm64/libluajit.a

echo "########## Building for arm64 (iOS simulator) ##########"
ISDKF="-arch arm64 -isysroot $IOSDIR/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator.sdk -mios-simulator-version-min=$SIMVER"
make -j -C $LUAJIT HOST_CC="xcrun clang -m64 -arch x86_64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS clean
make -j -C $LUAJIT HOST_CC="xcrun clang -m64 -arch x86_64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS amalg
mkdir -p $BUILD_DIR/arm64-sim
mv $LUAJIT/src/libluajit.a $BUILD_DIR/arm64-sim/libluajit.a

echo "########## Building for x86_64 (iOS simulator) ##########"
ISDKF="-arch x86_64 -isysroot $IOSDIR/iPhoneSimulator.platform/Developer/SDKs/iPhoneSimulator.sdk -miphoneos-version-min=$IOSSDKVER"
make -j -C $LUAJIT HOST_CC="clang -m64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS clean
make -j -C $LUAJIT HOST_CC="clang -m64" CC="clang" CFLAGS=-fPIC CROSS=$IOSBIN TARGET_FLAGS="$ISDKF" TARGET_CFLAGS="-DLJ_NO_SYSTEM=1" TARGET_SYS=iOS amalg
mkdir -p $BUILD_DIR/x86_64
mv $LUAJIT/src/libluajit.a $BUILD_DIR/x86_64/libluajit.a

# Tweaks
LIB_DIR="$(realpath $BUILD_DIR)"
# BSD Sed...
sed -i '' \
  "s#x86_64 .\{1,\} \${linker-opts}#x86_64 -mios-simulator-version-min=\${minIOSVersion} -L\&quot;$LIB_DIR/x86_64\&quot; \${linker-opts}#" \
  ../build-ios32.xml
sed -i '' \
  "s#arm64 .\{1,\}simulator.\{1,\} \${linker-opts}#arm64 -mios-simulator-version-min=\${minIOSVersion} -L\&quot;$LIB_DIR/arm64-sim\&quot; \${linker-opts}#" \
  ../build-ios32.xml
sed -i '' \
  "s#arm64 .\{1,\}miphoneos.\{1,\} \${linker-opts}#arm64 -miphoneos-version-min=\${minIOSVersion} -L\&quot;$LIB_DIR/arm64\&quot; \${linker-opts}#" \
  ../build-ios32.xml

cat ../build-ios32.xml
ls $BUILD_DIR/*/
