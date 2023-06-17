#ifndef CUSTOM_H
#define CUSTOM_H

#include "lua.hpp"

int luaopen_jua(lua_State * L) {
    luaL_register(L, LUA_JAVALIBNAME, javalib);
    return 1;
}

static void luaJavaSetup(lua_State * L, JNIEnv * env, int lid) {
    lua_atpanic(L, &fatalError);
    luaJ_openlib(L, "");
    luaJ_openlib_call(L, LUA_JAVALIBNAME, &luaopen_jua);

    lua_pushstring(L, JAVA_STATE_INDEX);
    lua_pushinteger(L, lid);
    lua_settable(L, LUA_REGISTRYINDEX);

    initMetaRegistry(L);
}

static int initLuaJitBindings(JNIEnv * env) {
    return initBindings(env);
}

#endif /* !CUSTOM_H */