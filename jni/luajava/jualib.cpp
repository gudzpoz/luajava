#include "jua.h"
#include "juaapi.h"
#include "jualib.h"

static int javaRequire(lua_State * L) {
  const char * className = luaL_checkstring(L, 1);

  JNIEnv * env = getJNIEnv(L);

  jclass classInstance = bindJavaClass(env, className);
  if (classInstance == NULL) {
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

static const luaL_Reg javalib[] = {
  {"require",   javaRequire},
  {"new",       javaNew},
  {NULL, NULL}
};

int luaopen_jua(lua_State *L) {
  luaL_register(L, LUA_JAVALIBNAME, javalib);
  return 1;
}
