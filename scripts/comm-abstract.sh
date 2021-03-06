#!/bin/bash

# bash jni/scripts/comm-abstract.sh > src/main/java/party/iroiro/jua/LuaNative.java

COMM12=`comm -12 <(grep '^    protected' lua51/src/main/java/party/iroiro/jua/Lua51Natives.java| sort) <(grep '^    protected' lua52/src/main/java/party/iroiro/jua/Lua52Natives.java| sort)`
COMM123=`comm -12 <(echo "$COMM12") <(grep '^    protected' lua53/src/main/java/party/iroiro/jua/Lua53Natives.java| sort)`
COMM1234=`comm -12 <(echo "$COMM123") <(grep '^    protected' lua54/src/main/java/party/iroiro/jua/Lua54Natives.java| sort)`
echo "package party.iroiro.jua;"
echo
echo "import java.nio.Buffer;"
echo
echo "/**"
echo " * Generated from the common parts of <code>Lua5.[1..4]</code>"
echo " */"
echo '@SuppressWarnings("unused")'
echo "public abstract class LuaNative {"
echo
echo "$COMM1234" | sed -e 's/native/abstract/' -e 's# /\*#\n#'
# Param name differs
echo "    protected abstract void lua_rawseti(long ptr, int index, int i);"
echo "}"
