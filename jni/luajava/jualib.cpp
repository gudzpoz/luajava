#include "jua.h"
#include "juaapi.h"
#include "jualib.h"

static int javaRequire(lua_State * L) {
  const char * className = luaL_checkstring(L, 1);

  JNIEnv * env = getJNIEnv(L);

  bool noExceptions = env->ExceptionCheck() == JNI_FALSE;
  jclass classInstance = bindJavaClass(env, className);
  if (classInstance == NULL) {
    if (noExceptions) {
      env->ExceptionClear();
    }
    return luaL_error(L, "Unable to bind to class %s", className);
  }

  return pushJ<JAVA_CLASS_META_REGISTRY>(L, (jobject) classInstance);
}

static int javaNew(lua_State * L) {
  jobject * data = (jobject *) luaL_checkudata(L, 1, JAVA_CLASS_META_REGISTRY);

  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  return env->CallStaticIntMethod(juaapi_class, juaapi_classnew,
      (jint) stateIndex, *data, lua_gettop(L) - 1);
}

static int javaLuaify(lua_State * L) {
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  return env->CallStaticIntMethod(juaapi_class, juaapi_luaify, (jint) stateIndex);
}

static int javaImport(lua_State * L) {
  const char * className = luaL_checkstring(L, 1);

  if (className == NULL) {
    return 0;
  }

  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);

  return env->CallStaticIntMethod(juaapi_class, juaapi_import, (jint) stateIndex,
                                  env->NewStringUTF(className));
}

static int javaProxy(lua_State * L) {
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  return env->CallStaticIntMethod(juaapi_class, juaapi_proxy, (jint) stateIndex);
}

const luaL_Reg javalib[] = {
  { "require",   javaRequire },
  { "new",       javaNew },
  { "luaify",    javaLuaify },
  { "import",    javaImport },
  { "proxy",     javaProxy },
  {NULL, NULL}
};
