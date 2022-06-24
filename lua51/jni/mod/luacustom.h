#ifndef CUSTOM_H
#define CUSTOM_H

#include "lua.hpp"

static void luaJavaSetup(lua_State * L, JNIEnv * env, int lid) {
    lua_atpanic(L, &fatalError);
    luaJ_openlib(L, "");
    luaJ_openlib_call(L, LUA_JAVALIBNAME, &luaopen_jua);

    lua_pushstring(L, JAVA_STATE_INDEX);
    lua_pushinteger(L, lid);
    lua_settable(L, LUA_REGISTRYINDEX);

    lua_pushstring(L, JNIENV_INDEX);
    JNIEnv ** udEnv = (JNIEnv **) lua_newuserdata(L, sizeof(JNIEnv *));
    *udEnv = env;
    lua_rawset(L, LUA_REGISTRYINDEX);

    initMetaRegistry(L);
}

static lua_State * luaJ_newthread(lua_State * L, int lid) {
    lua_State * K = lua_newthread(L);
    lua_pushthread(K);
    lua_pushinteger(K, lid);
    lua_settable(K, LUA_REGISTRYINDEX);
    return K;
}

static int initLua51Bindings(JNIEnv * env) {
    return initBindings(env);
}

static void luaJ_pushobject(JNIEnv * env, lua_State * L, jobject obj) {
    jobject global = env->NewGlobalRef(obj);
    if (global != NULL) {
        pushJ<JAVA_OBJECT_META_REGISTRY>(L, global);
    }
}

static void luaJ_pushclass(JNIEnv * env, lua_State * L, jobject clazz) {
    jobject global = env->NewGlobalRef(clazz);
    if (global != NULL) {
        pushJ<JAVA_CLASS_META_REGISTRY>(L, global);
    }
}

static void luaJ_pusharray(JNIEnv * env, lua_State * L, jobject array) {
    jobject global = env->NewGlobalRef(array);
    if (global != NULL) {
        pushJ<JAVA_ARRAY_META_REGISTRY>(L, global);
    }
}

static jobject luaJ_toobject(lua_State * L, int index) {
    void * p = luaL_testudata(L, index, JAVA_OBJECT_META_REGISTRY);
    if (p == NULL) {
        p = luaL_testudata(L, index, JAVA_CLASS_META_REGISTRY);
    }
    if (p == NULL) {
        p = luaL_testudata(L, index, JAVA_ARRAY_META_REGISTRY);
    }
    if (p == NULL) {
        return NULL;
    } else {
        return *((jobject *) p);
    }
}

static int luaJ_isobject(lua_State * L, int index) {
    return luaJ_toobject(L, index) != NULL;
}

#endif /* !CUSTOM_H */