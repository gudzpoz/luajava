#include "jni.h"
#include "lua.hpp"

#include "jua.h"

#include <cstdio>

inline int jInvoke(lua_State * L, const char * reg, jmethodID methodID) {
  jobject * data = (jobject *) luaL_checkudata(L, 1, reg);
  const char * name = lua_tostring(L, lua_upvalueindex(1));
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  jstring str = env->NewStringUTF(name);
  jint ret = env->CallStaticIntMethod(juaapi_class, methodID,
      (jint) stateIndex, *data, str, lua_gettop(L) - 1);
  env->DeleteLocalRef(str);
  if (ret == -1) {
    return luaL_error(L, "No matching method found");
  } else {
    return ret;
  }
}

inline int jIndex(lua_State * L, const char * reg, jmethodID methodID, lua_CFunction func, bool ret) {
  jobject * data = (jobject *) luaL_checkudata(L, 1, reg);
  const char * name = luaL_checkstring(L, 2);
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  jstring str = env->NewStringUTF(name);
  jint retVal = env->CallStaticIntMethod(juaapi_class, methodID, (jint) stateIndex, *data, str);
  env->DeleteLocalRef(str);
  if ((retVal & 0x1) != 0 && ret) {
    return 1;
  } else if ((retVal & 0x2) != 0 && ret) {
    lua_pushcclosure(L, func, 1);
    return 1;
  } else {
    return 0;
  }
}

inline int jIndex(lua_State * L, const char * reg, jmethodID methodID, lua_CFunction func) {
  return jIndex(L, reg, methodID, func, true);
}

int jclassInvoke(lua_State * L) {
  return jInvoke(L, JAVA_CLASS_META_REGISTRY, juaapi_classinvoke);
}

int jclassIndex(lua_State * L) {
  return jIndex(L, JAVA_CLASS_META_REGISTRY, juaapi_classindex, &jclassInvoke);
}

int jclassNewIndex(lua_State * L) {
  return jIndex(L, JAVA_CLASS_META_REGISTRY, juaapi_classnewindex, NULL, false);
}

int jobjectInvoke(lua_State * L) {
  return jInvoke(L, JAVA_OBJECT_META_REGISTRY, juaapi_objectinvoke);
}

int jobjectCall(lua_State * L) {
  return jInvoke(L, JAVA_OBJECT_META_REGISTRY, juaapi_objectinvoke);
}

int jobjectIndex(lua_State * L) {
  return jIndex(L, JAVA_OBJECT_META_REGISTRY, juaapi_objectindex, &jobjectInvoke);
}

int jobjectNewIndex(lua_State * L) {
  return jIndex(L, JAVA_OBJECT_META_REGISTRY, juaapi_objectnewindex, NULL, false);
}

int jarrayLength(lua_State * L) {
  jobject * data = (jobject *) luaL_checkudata(L, 1, JAVA_ARRAY_META_REGISTRY);
  JNIEnv * env = getJNIEnv(L);
  int len = (int) env->CallStaticIntMethod(juaapi_class, juaapi_arraylen, *data);
  lua_pushinteger(L, len);
  return 1;
}

inline int jarrayJIndex(lua_State * L, jmethodID func, bool ret) {
  jobject * data = (jobject *) luaL_checkudata(L, 1, JAVA_ARRAY_META_REGISTRY);
  int i = (int) luaL_checknumber(L, 2);
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  int retVal = (int) env->CallStaticIntMethod(juaapi_class, func, (jint) stateIndex, *data, i);
  if (ret) {
    return retVal;
  } else {
    env->CallStaticIntMethod(juaapi_class, func, (jint) stateIndex, *data, i);
    return 0;
  }
}

int jarrayIndex(lua_State * L) {
  return jarrayJIndex(L, juaapi_arrayindex, true);
}

int jarrayNewIndex(lua_State * L) {
  return jarrayJIndex(L, juaapi_arraynewindex, false);
}