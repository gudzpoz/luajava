#ifndef JUAAPI_H
#define JUAAPI_H

#include "lua.hpp"
#include "jni.h"

#include "jua.h"

/*
 * These methods are called by the lua environment
 * and thus raises errors to the lua environment.
 */

template <const char *R>
static int gc(lua_State * L) {
  jobject * data = (jobject *) luaL_checkudata(L, 1, R);
  JNIEnv * env = getJNIEnv(L);
  env->DeleteGlobalRef(*data);
  return 0;
}

/**
 * Expects the obj is a global ref
 */
template <const char *R>
static int pushJ(lua_State * L, jobject obj) {
  jobject * data = (jobject *) lua_newuserdata(L, sizeof(jobject));
  *data = obj;

  luaL_setmetatable(L, R);
  return 1;
}

#define HANDLE_EXCEPTION(env) \
    jthrowable e = env->ExceptionOccurred(); \
    if (e != NULL)

int jclassIndex(lua_State * L);
int jclassNewIndex(lua_State * L);
int jclassCall(lua_State * L);
int jclassSigCall(lua_State * L);
int jobjectIndex(lua_State * L);
int jobjectSigCall(lua_State * L);
int jobjectNewIndex(lua_State * L);
int jobjectEquals(lua_State * L);
int jarrayLength(lua_State * L);
int jarrayIndex(lua_State * L);
int jarrayNewIndex(lua_State * L);
int jfunctionWrapper(lua_State * L);
int jmoduleLoad(lua_State * L);
int jloadModule(lua_State * L);

int jpackageImport(lua_State * L);

#endif /* JUAAPI_H! */
