#ifndef JUA_H
#define JUA_H

#include "lua.hpp"
#include "jni.h"

#define JAVA_STATE_INDEX "__JavaJuaStateIndex"

extern const char JAVA_CLASS_META_REGISTRY[];
extern const char JAVA_OBJECT_META_REGISTRY[];
extern const char JAVA_ARRAY_META_REGISTRY[];
extern const char JAVA_PACKAGE_META_REGISTRY[];

extern jclass    juaapi_class;
extern jmethodID juaapi_classnew;
extern jmethodID juaapi_classindex;
extern jmethodID juaapi_classinvoke;
extern jmethodID juaapi_classsiginvoke;
extern jmethodID juaapi_classnewindex;
extern jmethodID juaapi_objectindex;
extern jmethodID juaapi_objectinvoke;
extern jmethodID juaapi_objsiginvoke;
extern jmethodID juaapi_objectnewindex;
extern jmethodID juaapi_arraynew;
extern jmethodID juaapi_arraylen;
extern jmethodID juaapi_arrayindex;
extern jmethodID juaapi_arraynewindex;
extern jmethodID juaapi_luaify;
extern jmethodID juaapi_import;
extern jmethodID juaapi_proxy;
extern jmethodID juaapi_load;
extern jmethodID throwable_tostring;

jclass bindJavaClass(JNIEnv * env, const char * name);
jmethodID bindJavaStaticMethod(JNIEnv * env, jclass c, const char * name, const char * sig);
jmethodID bindJavaMethod(JNIEnv * env, jclass c, const char * name, const char * sig);
int initBindings(JNIEnv * env);

void initMetaRegistry(lua_State * L);

int getStateIndex(lua_State * L);
JNIEnv * getJNIEnv(lua_State * L);

int fatalError(lua_State * L);

lua_State * luaJ_newthread(lua_State * L, int lid);
void luaJ_pushobject(JNIEnv * env, lua_State * L, jobject obj);
void luaJ_pushclass(JNIEnv * env, lua_State * L, jobject clazz);
void luaJ_pusharray(JNIEnv * env, lua_State * L, jobject array);
jobject luaJ_toobject(lua_State * L, int index);
int luaJ_isobject(lua_State * L, int index);

int luaJ_insertloader(lua_State * L, const char * searchers);

inline int checkOrError (JNIEnv * env, lua_State * L, jint ret) {
  jthrowable e = env->ExceptionOccurred();
  if (e == NULL) {
    if (ret >= 0) {
      return (int) ret;
    } else {
      return lua_error(L);
    }
  }
  env->ExceptionClear();
  jstring message = (jstring) env->CallObjectMethod(e, throwable_tostring);
  env->DeleteLocalRef(e);
  const char * str = env->GetStringUTFChars(message, NULL);
  lua_pushstring(L, str);
  env->ReleaseStringUTFChars(message, str);
  env->DeleteLocalRef((jobject) message);
  return lua_error(L);
}

#endif /* JUA_H! */