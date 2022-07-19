#include "jua.h"
#include "juaapi.h"
#include "jualib.h"

static int javaMethod(lua_State * L) {
  if (luaL_testudata(L, 1, JAVA_OBJECT_META_REGISTRY) != NULL) {
    return jobjectSigCall(L);
  }

  if (luaL_testudata(L, 1, JAVA_CLASS_META_REGISTRY) != NULL) {
    return jclassSigCall(L);
  }

  return luaL_error(L, "bad argument #1 to 'java.method': %s or %s expected",
    JAVA_CLASS_META_REGISTRY, JAVA_OBJECT_META_REGISTRY);
}

static int javaNew(lua_State * L) {
  if (luaL_testudata(L, 1, JAVA_CLASS_META_REGISTRY) != NULL
    || luaL_testudata(L, 1, JAVA_OBJECT_META_REGISTRY) != NULL) {
    return checkOrError(L, jclassCall(L));
  } else {
    return luaL_error(L, "bad argument #1 to 'java.new': %s or %s expected",
      JAVA_CLASS_META_REGISTRY, JAVA_OBJECT_META_REGISTRY);
  }
}

static int javaLuaify(lua_State * L) {
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  return checkOrError(L, env->CallStaticIntMethod(juaapi_class, juaapi_luaify, (jint) stateIndex));
}

static int javaImport(lua_State * L) {
  const char * className = luaL_checkstring(L, 1);

  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);

  jstring str = env->NewStringUTF(className);
  int ret = env->CallStaticIntMethod(juaapi_class, juaapi_import, (jint) stateIndex,
                                     str);
  env->DeleteLocalRef(str);
  return checkOrError(L, ret);
}

static int javaProxy(lua_State * L) {
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  return checkOrError(L, env->CallStaticIntMethod(juaapi_class, juaapi_proxy, (jint) stateIndex));
}

static int javaArray(lua_State * L) {
  if (luaL_testudata(L, 1, JAVA_CLASS_META_REGISTRY) != NULL
    || luaL_testudata(L, 1, JAVA_OBJECT_META_REGISTRY) != NULL) {
    JNIEnv * env = getJNIEnv(L);
    int stateIndex = getStateIndex(L);
    int top = lua_gettop(L);
    jobject * data = (jobject *) lua_touserdata(L, 1);
    if (top == 2) {
      return checkOrError(L, env->CallStaticIntMethod(juaapi_class, juaapi_arraynew,
        (jint) stateIndex, *data, (jint) lua_tointeger(L, 2)));
    }
    if (top > 2) {
      return checkOrError(L, env->CallStaticIntMethod(juaapi_class, juaapi_arraynew,
        (jint) stateIndex, *data, (jint) (1 - top)));
    }
    return luaL_error(L, "bad argument #2 to 'java.array': number expected, got none");
  }
  return luaL_error(L, "bad argument #1 to 'java.array': %s or %s expected",
    JAVA_CLASS_META_REGISTRY, JAVA_OBJECT_META_REGISTRY);
}

const luaL_Reg javalib[] = {
  { "method",    javaMethod },
  { "new",       javaNew },
  { "luaify",    javaLuaify },
  { "import",    javaImport },
  { "proxy",     javaProxy },
  { "array",     javaArray },
  {NULL, NULL}
};
