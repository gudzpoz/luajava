#include "jni.h"
#include "lua.hpp"

#include "jua.h"

inline int jInvokeObject(lua_State * L, jmethodID methodID,
                         jobject data, const char * name, int params) {
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  jint ret;
  if (name == NULL) {
    ret = env->CallStaticIntMethod(juaapi_class, methodID,
                                   (jint) stateIndex, data, NULL, params);
  } else {
    jstring str = env->NewStringUTF(name);
    ret = env->CallStaticIntMethod(juaapi_class, methodID,
                                   (jint) stateIndex, data, str, params);
    env->DeleteLocalRef(str);
  }
  return checkOrError(env, L, ret);
}

inline int jInvoke(lua_State * L, const char * reg, jmethodID methodID) {
  jobject * data = (jobject *) luaL_checkudata(L, 1, reg);
  const char * name = lua_tostring(L, lua_upvalueindex(1));
  return jInvokeObject(L, methodID, *data, name, lua_gettop(L) - 1);
}

inline int jIndex(lua_State * L, const char * reg, jmethodID methodID, lua_CFunction func, bool ret) {
  jobject * data = (jobject *) luaL_checkudata(L, 1, reg);
  const char * name = luaL_checkstring(L, 2);
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  jstring str = env->NewStringUTF(name);
  jint retVal = env->CallStaticIntMethod(juaapi_class, methodID, (jint) stateIndex, *data, str);
  env->DeleteLocalRef(str);
  if (retVal == -1) {
    return checkOrError(env, L, -1);
  }
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

int jarrayInvoke(lua_State * L) {
  return jInvoke(L, JAVA_ARRAY_META_REGISTRY, juaapi_objectinvoke);
}

int jclassInvoke(lua_State * L) {
  return jInvoke(L, JAVA_CLASS_META_REGISTRY, juaapi_classinvoke);
}

int jclassIndex(lua_State * L) {
  return jIndex(L, JAVA_CLASS_META_REGISTRY, juaapi_classindex, &jclassInvoke);
}

int jclassCall(lua_State * L) {
  jobject * data = (jobject *) lua_touserdata(L, 1);
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  return checkOrError(env, L, env->CallStaticIntMethod(juaapi_class, juaapi_classnew,
    (jint) stateIndex, *data, lua_gettop(L) - 1));
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

static void checkJobject(lua_State * L, int n) {
  if (luaL_testudata(L, n, JAVA_CLASS_META_REGISTRY) != NULL ||
      luaL_testudata(L, n, JAVA_OBJECT_META_REGISTRY) != NULL ||
      luaL_testudata(L, n, JAVA_ARRAY_META_REGISTRY) != NULL) {
    return;
  }
  luaL_error(L, "bad argument #%d to jobjectEquals: %s, %s or %s expected",
             n, JAVA_CLASS_META_REGISTRY, JAVA_OBJECT_META_REGISTRY, JAVA_ARRAY_META_REGISTRY);
}

int jobjectEquals(lua_State * L) {
  checkJobject(L, 1);
  checkJobject(L, 2);
  jobject * obj1 = (jobject *) lua_touserdata(L, 1);
  jobject * obj2 = (jobject *) lua_touserdata(L, 2);
  JNIEnv * env = getJNIEnv(L);
  lua_pushboolean(L, env->IsSameObject(*obj1, *obj2));
  return 1;
}

int jfunctionWrapper(lua_State * L) {
  jobject * data = (jobject *) lua_touserdata(L, lua_upvalueindex(1));
  return jInvokeObject(L, juaapi_objectinvoke, *data, NULL, lua_gettop(L));
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
  int retVal = checkOrError(env, L,
    env->CallStaticIntMethod(juaapi_class, func, (jint) stateIndex, *data, i));
  return ret ? retVal : 0;
}

int jarrayIndex(lua_State * L) {
  if (lua_isnumber(L, 2)) {
    return jarrayJIndex(L, juaapi_arrayindex, true);
  }
  if (lua_isstring(L, 2)) {
    return jIndex(L, JAVA_ARRAY_META_REGISTRY, juaapi_objectindex, &jarrayInvoke);
  }
  return luaL_error(L, "bad argument #2 to __index (expecting number or string)");
}

int jarrayNewIndex(lua_State * L) {
  return jarrayJIndex(L, juaapi_arraynewindex, false);
}

// c = jobject('methodName', 'signature') --> returns a closure
inline int jSigCall(lua_State * L, lua_CFunction func) {
  int top = lua_gettop(L);
  if (top == 3) {
    lua_pushcclosure(L, func, 3);
    return 1;
  } else if (top == 2) {
    lua_pushcclosure(L, func, 2);
    return 1;
  } else {
    return 0;
  }
}

// c = jobject('methodName', 'signature') --> returns a closure
// c(param1, param2) --> method call
inline int jSigInvoke(lua_State * L, const char * reg, jmethodID methodID) {
  jobject * data = (jobject *) luaL_checkudata(L, lua_upvalueindex(1), reg);
  const char * name = luaL_checkstring(L, lua_upvalueindex(2));
  const char * signature = luaL_optstring(L, lua_upvalueindex(3), NULL);

  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);

  jstring nameS = env->NewStringUTF(name);
  jstring signatureS = signature == NULL ? NULL : env->NewStringUTF(signature);
  int ret = env->CallStaticIntMethod(juaapi_class, methodID,
                                     (jint) stateIndex, *data, nameS, signatureS, lua_gettop(L));
  if (signature != NULL) {
    env->DeleteLocalRef(signatureS);
  }
  env->DeleteLocalRef(nameS);
  return checkOrError(env, L, ret);
}

int jclassSigInvoke(lua_State * L) {
  return jSigInvoke(L, JAVA_CLASS_META_REGISTRY, juaapi_classsiginvoke);
}

int jobjectSigInvoke(lua_State * L) {
  return jSigInvoke(L, JAVA_OBJECT_META_REGISTRY, juaapi_objsiginvoke);
}

int jclassSigCall(lua_State * L) {
  return jSigCall(L, &jclassSigInvoke);
}

int jobjectSigCall(lua_State * L) {
  return jSigCall(L, &jobjectSigInvoke);
}

// Calls juaapi_load and loads with an ExternalLoader
int jmoduleLoad(lua_State * L) {
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  const char * name = luaL_checkstring(L, 1);
  jstring moduleName = env->NewStringUTF(name);
  int ret = env->CallStaticIntMethod(juaapi_class, juaapi_load,
                                     (jint) stateIndex, moduleName);
  env->DeleteLocalRef(moduleName);
  return checkOrError(env, L, ret);
}

// Calls juaapi_loadmodule and loads a Java static method
int jloadModule(lua_State * L) {
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  const char * name = luaL_checkstring(L, 1);
  jstring moduleName = env->NewStringUTF(name);
  env->CallStaticIntMethod(juaapi_class, juaapi_loadmodule,
                           (jint) stateIndex, moduleName);
  env->DeleteLocalRef(moduleName);
  return checkOrError(env, L, 1);
}
