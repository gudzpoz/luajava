#include "jni.h"
#include "lua.hpp"

#define LUAJAVALIBCLASS "party/iroiro/jua/LuaJavaLib"
#define JNIENV_INDEX "__TheTemporaryJNIEnv"
#define JAVA_STATE_INDEX "__JavaJuaStateIndex"

// Bindings
// java.lang.Class, Class::forName
jclass    java_lang_class_class   = NULL;
jmethodID java_lang_class_forname = NULL;
// party.iroiro.jua.JuaAPI
jclass    juaapi_class          = NULL;
jmethodID juaapi_objectindex    = NULL;
jmethodID juaapi_objectnewindex = NULL;
jmethodID juaapi_arrayindex     = NULL;
jmethodID juaapi_arraynewindex  = NULL;
jmethodID juaapi_classindex     = NULL;
jmethodID juaapi_checkindex     = NULL;
// java.lang.Throwable
jclass java_lang_throwable_class = NULL;
jmethodID throwable_getmessage   = NULL;
jmethodID throwable_tostring     = NULL;

/**
 * Opens individual libraries when one does not want them all
 */
static inline void luaJ_openlib(lua_State * L, const char *libName, lua_CFunction loader) {
  lua_pushcfunction(L, loader);
  lua_pushstring(L, libName);
  lua_call(L, 1, 0);
}

/**
 * Calls java.lang.Class.forName and returns the found class
 *
 * Requires java_lang_class_class, java_lang_class_forname
 * initialized.
 * Throws C++ LuaException when exceptions occur
 */
jclass findJavaClass(JNIEnv * env, const char * name) {
  jstring jName = env->NewStringUTF(name);
  jclass c = (jclass) env->CallStaticObjectMethod(
          java_lang_class_class, java_lang_class_forname, jName);
  env->DeleteLocalRef(jName);
  // TODO: not used yet, need a global ref
  return c;
}

/**
 * Returns a global reference to the class matching the name
 *
 * Throws C++ LuaException when class not found or whatever.
 */
inline jclass bindJavaClass(JNIEnv * env, const char * name) {
  jclass tempClass;
  tempClass = env->FindClass(name);
  if (tempClass == NULL) {
      throw LuaException("Could not find the class");
  } else {
    jclass classRef = (jclass) env->NewGlobalRef(tempClass);
    // https://stackoverflow.com/q/33481144/17780636
    // env->DeleteLocalRef(tempClass);
    if (classRef == NULL) {
      throw LuaException("Could not bind the class");
    } else {
      return classRef;
    }
  }
}

/**
 * Returns the methodID or throws LuaException
 */
inline jmethodID bindJavaStaticMethod(JNIEnv * env, jclass c, const char * name, const char * sig) {
  jmethodID id = env->GetStaticMethodID(c, name, sig);
  if (id == NULL) {
    throw LuaException("Could not find the method");
  }
  return id;
}

/**
 * Returns the methodID or throws LuaException
 */
inline jmethodID bindJavaMethod(JNIEnv * env, jclass c, const char * name, const char * sig) {
  jmethodID id = env->GetMethodID(c, name, sig);
  if (id == NULL) {
    throw LuaException("Could not find the method");
  }
  return id;
}

// TODO: switch to reinterpret_cast<jclass> etc.
/**
 * Init JNI cache bindings
 * See Jua.java
 *
 * Throws LuaException when exceptions occur
 */
static void initBindings(JNIEnv * env) {
  java_lang_class_class = bindJavaClass(env, "java/lang/Class");
  java_lang_class_forname = bindJavaStaticMethod(env, java_lang_class_class,
          "forName", "(Ljava/lang/String;)Ljava/lang/Class;");

  java_lang_throwable_class = bindJavaClass(env, "java/lang/Throwable");
  throwable_getmessage = bindJavaMethod(env, java_lang_throwable_class,
          "getMessage", "()Ljava/lang/String;");
  throwable_tostring = bindJavaMethod(env, java_lang_throwable_class,
          "toString", "()Ljava/lang/String;");

  juaapi_class = bindJavaClass(env, "party/iroiro/jua/JuaAPI");
  juaapi_checkindex = bindJavaStaticMethod(env, juaapi_class,
          "checkIndex", "(Ljava/lang/Object;Ljava/lang/String;)I");
  /*
  juaapi_classindex = bindJavaStaticMethod(env, juaapi_class,
          "", "");
  juaapi_objectindex = bindJavaStaticMethod(env, juaapi_class,
          "", "");
  juaapi_objectnewindex = bindJavaStaticMethod(env, juaapi_class,
          "", "");
  juaapi_arrayindex = bindJavaStaticMethod(env, juaapi_class,
          "", "");
  juaapi_arraynewindex = bindJavaStaticMethod(env, juaapi_class,
          "", "");
  */
}

static int getStateIndex(lua_State * L) {
  lua_pushstring(L, JAVA_STATE_INDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);
  lua_Number stateIndex = lua_tonumber(L, -1);
  lua_pop(L, 1);
  return (int) stateIndex;
}

static void updateJNIEnv(JNIEnv * env, lua_State * L) {
  JNIEnv ** udEnv;
  lua_pushstring(L, JNIENV_INDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);
  udEnv = (JNIEnv **) lua_touserdata(L, -1);
  *udEnv = env;
  lua_pop(L, 1);
}