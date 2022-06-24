#ifndef JUA_H
#define JUA_H

#include "lua.hpp"
#include "jni.h"

#define LUAJAVALIBCLASS "party/iroiro/jua/LuaJavaLib"
#define JNIENV_INDEX "__TheTemporaryJNIEnv"
#define JAVA_STATE_INDEX "__JavaJuaStateIndex"

extern const char JAVA_CLASS_META_REGISTRY[];
extern const char JAVA_OBJECT_META_REGISTRY[];
extern const char JAVA_ARRAY_META_REGISTRY[];

extern jclass    juaapi_class;
extern jmethodID juaapi_classnew;
extern jmethodID juaapi_classindex;
extern jmethodID juaapi_classinvoke;
extern jmethodID juaapi_classnewindex;
extern jmethodID juaapi_objectindex;
extern jmethodID juaapi_objectinvoke;
extern jmethodID juaapi_objectnewindex;
extern jmethodID juaapi_arraylen;
extern jmethodID juaapi_arrayindex;
extern jmethodID juaapi_arraynewindex;

jclass bindJavaClass(JNIEnv * env, const char * name);
jmethodID bindJavaStaticMethod(JNIEnv * env, jclass c, const char * name, const char * sig);
jmethodID bindJavaMethod(JNIEnv * env, jclass c, const char * name, const char * sig);
int initBindings(JNIEnv * env);

void initMetaRegistry(lua_State * L);

int getStateIndex(lua_State * L);
void updateJNIEnv(JNIEnv * env, lua_State * L);
JNIEnv * getJNIEnv(lua_State * L);

int fatalError(lua_State * L);

#endif /* JUA_H! */