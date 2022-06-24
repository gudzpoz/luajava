#include "jni.h"
#include "lua.hpp"

#include "jua.h"
#include "juaapi.h"

// For template usage
const char JAVA_CLASS_META_REGISTRY[] = "__JavaClassMetatable";
const char JAVA_OBJECT_META_REGISTRY[] = "__JavaObjectMetatable";
const char JAVA_ARRAY_META_REGISTRY[] =  "__JavaArrayMetatable";

// Bindings
// java.lang.Class, Class::forName
jclass    java_lang_class_class   = NULL;
jmethodID java_lang_class_forname = NULL;
// party.iroiro.jua.JuaAPI
jclass    juaapi_class          = NULL;
jmethodID juaapi_classnew       = NULL;
jmethodID juaapi_classindex     = NULL;
jmethodID juaapi_classinvoke    = NULL;
jmethodID juaapi_classnewindex  = NULL;
jmethodID juaapi_objectindex    = NULL;
jmethodID juaapi_objectinvoke   = NULL;
jmethodID juaapi_objectnewindex = NULL;
jmethodID juaapi_arraylen       = NULL;
jmethodID juaapi_arrayindex     = NULL;
jmethodID juaapi_arraynewindex  = NULL;
// java.lang.Throwable
jclass java_lang_throwable_class = NULL;
jmethodID throwable_getmessage   = NULL;
jmethodID throwable_tostring     = NULL;

/**
 * The new panic function that panics the JVM
 */
int fatalError(lua_State * L) {
  JNIEnv * env = getJNIEnv(L);
  env->FatalError(lua_tostring(L, -1));
  return 0;
}

/**
 * Returns a global reference to the class matching the name
 *
 * Exceptions on the Java side is not cleared (NoClassDefFoundError, for example).
 */
jclass bindJavaClass(JNIEnv * env, const char * name) {
  jclass tempClass;
  tempClass = env->FindClass(name);
  if (tempClass == NULL) {
    return NULL;
  } else {
    jclass classRef = (jclass) env->NewGlobalRef(tempClass);
    // https://stackoverflow.com/q/33481144/17780636
    // env->DeleteLocalRef(tempClass);
    if (classRef == NULL) {
      return NULL;
    } else {
      return classRef;
    }
  }
}

/**
 * Returns the methodID
 */
jmethodID bindJavaStaticMethod(JNIEnv * env, jclass c, const char * name, const char * sig) {
  jmethodID id = env->GetStaticMethodID(c, name, sig);
  if (id == NULL) {
    return NULL;
  }
  return id;
}

/**
 * Returns the methodID
 */
jmethodID bindJavaMethod(JNIEnv * env, jclass c, const char * name, const char * sig) {
  jmethodID id = env->GetMethodID(c, name, sig);
  if (id == NULL) {
    return NULL;
  }
  return id;
}

// TODO: switch to reinterpret_cast<jclass> etc.
/**
 * Init JNI cache bindings
 * See Jua.java
 * Returns zero if completed without errors
 */
int initBindings(JNIEnv * env) {
  java_lang_class_class = bindJavaClass(env, "java/lang/Class");
  java_lang_class_forname = bindJavaStaticMethod(env, java_lang_class_class,
          "forName", "(Ljava/lang/String;)Ljava/lang/Class;");

  java_lang_throwable_class = bindJavaClass(env, "java/lang/Throwable");
  throwable_getmessage = bindJavaMethod(env, java_lang_throwable_class,
          "getMessage", "()Ljava/lang/String;");
  throwable_tostring = bindJavaMethod(env, java_lang_throwable_class,
          "toString", "()Ljava/lang/String;");

  juaapi_class = bindJavaClass(env, "party/iroiro/jua/JuaAPI");
  juaapi_classnew = bindJavaStaticMethod(env, juaapi_class,
          "classNew", "(ILjava/lang/Class;I)I");
  juaapi_classindex = bindJavaStaticMethod(env, juaapi_class,
          "classIndex", "(ILjava/lang/Class;Ljava/lang/String;)I");
  juaapi_classinvoke = bindJavaStaticMethod(env, juaapi_class,
          "classInvoke", "(ILjava/lang/Class;Ljava/lang/String;I)I");
  juaapi_classnewindex = bindJavaStaticMethod(env, juaapi_class,
          "classNewIndex", "(ILjava/lang/Class;Ljava/lang/String;)I");
  juaapi_objectindex = bindJavaStaticMethod(env, juaapi_class,
          "objectIndex", "(ILjava/lang/Object;Ljava/lang/String;)I");
  juaapi_objectinvoke = bindJavaStaticMethod(env, juaapi_class,
          "objectInvoke", "(ILjava/lang/Object;Ljava/lang/String;I)I");
  juaapi_objectnewindex = bindJavaStaticMethod(env, juaapi_class,
          "objectNewIndex", "(ILjava/lang/Object;Ljava/lang/String;)I");
  juaapi_arraylen = bindJavaStaticMethod(env, juaapi_class,
          "arrayLength", "(Ljava/lang/Object;)I");
  juaapi_arrayindex = bindJavaStaticMethod(env, juaapi_class,
          "arrayIndex", "(ILjava/lang/Object;I)I");
  juaapi_arraynewindex = bindJavaStaticMethod(env, juaapi_class,
          "arrayNewIndex", "(ILjava/lang/Object;I)I");
  if (java_lang_class_class == NULL
      || java_lang_class_forname == NULL
      || java_lang_throwable_class == NULL
      || throwable_getmessage == NULL
      || throwable_tostring == NULL
      || juaapi_class == NULL
      || juaapi_classnew == NULL
      || juaapi_classindex == NULL
      || juaapi_classinvoke == NULL
      || juaapi_classnewindex == NULL
      || juaapi_objectindex == NULL
      || juaapi_objectinvoke == NULL
      || juaapi_objectnewindex == NULL
      || juaapi_arraylen == NULL
      || juaapi_arrayindex == NULL
      || juaapi_arraynewindex) {
    return -1;
  } else {
    return 0;
  }
}

#define LUA_METAFIELD_GC "__gc"
#define LUA_METAFIELD_LEN "__len"
#define LUA_METAFIELD_CALL "__call"
#define LUA_METAFIELD_INDEX "__index"
#define LUA_METAFIELD_NEWINDEX "__newindex"
/**
 * Inits JAVA_CLASS_META_REGISTRY, JAVA_OBJECT_META_REGISTRY
 */
void initMetaRegistry(lua_State * L) {
  if (luaL_newmetatable(L, JAVA_CLASS_META_REGISTRY) == 1) {
    lua_pushcfunction(L, &gc<JAVA_CLASS_META_REGISTRY>);
    lua_setfield(L, -2, LUA_METAFIELD_GC);
    lua_pushcfunction(L, &jclassIndex);
    lua_setfield(L, -2, LUA_METAFIELD_INDEX);
    lua_pushcfunction(L, &jclassNewIndex);
    lua_setfield(L, -2, LUA_METAFIELD_NEWINDEX);
  }
  lua_pop(L, 1);

  if (luaL_newmetatable(L, JAVA_OBJECT_META_REGISTRY) == 1) {
    lua_pushcfunction(L, &gc<JAVA_OBJECT_META_REGISTRY>);
    lua_setfield(L, -2, LUA_METAFIELD_GC);
    lua_pushcfunction(L, &jobjectIndex);
    lua_setfield(L, -2, LUA_METAFIELD_INDEX);
    lua_pushcfunction(L, &jobjectNewIndex);
    lua_setfield(L, -2, LUA_METAFIELD_NEWINDEX);
    lua_pushcfunction(L, &jobjectCall);
    lua_setfield(L, -2, LUA_METAFIELD_CALL);
  }
  lua_pop(L, 1);

  if (luaL_newmetatable(L, JAVA_ARRAY_META_REGISTRY) == 1) {
    lua_pushcfunction(L, &gc<JAVA_ARRAY_META_REGISTRY>);
    lua_setfield(L, -2, LUA_METAFIELD_GC);
    lua_pushcfunction(L, &jarrayLength);
    lua_setfield(L, -2, LUA_METAFIELD_LEN);
    lua_pushcfunction(L, &jarrayIndex);
    lua_setfield(L, -2, LUA_METAFIELD_INDEX);
    lua_pushcfunction(L, &jarrayNewIndex);
    lua_setfield(L, -2, LUA_METAFIELD_NEWINDEX);
  }
  lua_pop(L, 1);
}

int getStateIndex(lua_State * L) {
  lua_Integer stateIndex;
  if (lua_pushthread(L) == 1) {
    /* Main thread */
    lua_pushstring(L, JAVA_STATE_INDEX);
    lua_rawget(L, LUA_REGISTRYINDEX);
    stateIndex = lua_tointeger(L, -1);
    lua_pop(L, 2);
  } else {
    /* Or else use the thread itself as key */
    lua_rawget(L, LUA_REGISTRYINDEX);
    stateIndex = lua_tointeger(L, -1);
    lua_pop(L, 1);
  }
  return (int) stateIndex;
}

void updateJNIEnv(JNIEnv * env, lua_State * L) {
  JNIEnv ** udEnv;
  lua_pushstring(L, JNIENV_INDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);
  udEnv = (JNIEnv **) lua_touserdata(L, -1);
  *udEnv = env;
  lua_pop(L, 1);
}

JNIEnv * getJNIEnv(lua_State * L) {
  JNIEnv * env;
  lua_pushstring(L, JNIENV_INDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);
  env = * (JNIEnv **) lua_touserdata(L, -1);
  lua_pop(L, 1);
  return env;
}

lua_State * luaJ_newthread(lua_State * L, int lid) {
    lua_State * K = lua_newthread(L);
    lua_pushthread(K);
    lua_pushinteger(K, lid);
    lua_settable(K, LUA_REGISTRYINDEX);
    return K;
}

void luaJ_pushobject(JNIEnv * env, lua_State * L, jobject obj) {
    jobject global = env->NewGlobalRef(obj);
    if (global != NULL) {
        pushJ<JAVA_OBJECT_META_REGISTRY>(L, global);
    }
}

void luaJ_pushclass(JNIEnv * env, lua_State * L, jobject clazz) {
    jobject global = env->NewGlobalRef(clazz);
    if (global != NULL) {
        pushJ<JAVA_CLASS_META_REGISTRY>(L, global);
    }
}

void luaJ_pusharray(JNIEnv * env, lua_State * L, jobject array) {
    jobject global = env->NewGlobalRef(array);
    if (global != NULL) {
        pushJ<JAVA_ARRAY_META_REGISTRY>(L, global);
    }
}

jobject luaJ_toobject(lua_State * L, int index) {
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

int luaJ_isobject(lua_State * L, int index) {
    return luaJ_toobject(L, index) != NULL;
}