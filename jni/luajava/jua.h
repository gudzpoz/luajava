#ifndef JUA_H
#define JUA_H

#include "lua.hpp"
#include "jni.h"

#define JAVA_STATE_INDEX "__JavaJuaStateIndex"

extern const char JAVA_CLASS_META_REGISTRY[];
extern const char JAVA_OBJECT_META_REGISTRY[];
extern const char JAVA_ARRAY_META_REGISTRY[];

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
extern jmethodID juaapi_arraylen;
extern jmethodID juaapi_arrayindex;
extern jmethodID juaapi_arraynewindex;
extern jmethodID juaapi_luaify;
extern jmethodID juaapi_import;
extern jmethodID juaapi_proxy;

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

#endif /* JUA_H! */