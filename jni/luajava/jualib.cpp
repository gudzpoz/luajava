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

static int javaMethod(lua_State * L) {
  if (luaL_testudata(L, 1, JAVA_OBJECT_META_REGISTRY) != NULL) {
    return jobjectSigCall(L);
  }

  if (luaL_testudata(L, 1, JAVA_CLASS_META_REGISTRY) != NULL) {
    return jclassSigCall(L);
  }

  return 0;
}

static int javaNew(lua_State * L) {
  if (luaL_testudata(L, 1, JAVA_CLASS_META_REGISTRY) != NULL
    || luaL_testudata(L, 1, JAVA_OBJECT_META_REGISTRY) != NULL) {
    return jclassCall(L);
  } else {
    return luaL_error(L, "bad argument #1 to 'java.new': %s or %s expected",
      JAVA_CLASS_META_REGISTRY, JAVA_OBJECT_META_REGISTRY);
  }
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

  jstring str = env->NewStringUTF(className);
  int ret = env->CallStaticIntMethod(juaapi_class, juaapi_import, (jint) stateIndex,
                                     str);
  env->DeleteLocalRef(str);
  return ret;
}

static int javaProxy(lua_State * L) {
  JNIEnv * env = getJNIEnv(L);
  int stateIndex = getStateIndex(L);
  return env->CallStaticIntMethod(juaapi_class, juaapi_proxy, (jint) stateIndex);
}

const luaL_Reg javalib[] = {
  { "method",    javaMethod },
  { "new",       javaNew },
  { "luaify",    javaLuaify },
  { "import",    javaImport },
  { "proxy",     javaProxy },
  {NULL, NULL}
};
