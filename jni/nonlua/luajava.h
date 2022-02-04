#ifndef LUAJAVA_H
#define LUAJAVA_H

#include <lua.hpp>
#include <jni.h>

#include "luaexception.h"

#define CPTRCLASS "party/iroiro/jua/CPtr"
#define JAVAFUNCTIONCLASS "party/iroiro/jua/LuaFunction"
#define LUAJAVACLASS "party/iroiro/jua/LuaJava"

/* Constant that is used to index the JNI Environment */
#define LUAJAVAJNIENVTAG "__JNIEnv"
/* Defines whether the metatable is of a java Object */
#define LUAJAVAOBJECTIND "__IsJavaObject"
/* Defines the lua State Index Property Name */
#define LUAJAVASTATEINDEX "LuaJavaStateIndex"
/* Constant that defines where in the metatable should I place the function name */
#define LUAJAVAOBJFUNCCALLED "__FunctionCalled"

#define LUA_INDEXTAG "__index"
#define LUA_NEWINDEXTAG "__newindex"
#define LUA_GCTAG "__gc"
#define LUA_CALLTAG "__call"

#define EXPORT LUA_API

#define PRELOAD(name, function) \
  lua_getglobal(L, "package"); \
  lua_getfield(L, -1, "preload"); \
  lua_pushcfunction(L, function); \
  lua_setfield(L, -2, name); \
  lua_pop(L, 2);

EXPORT jclass throwable_class;
EXPORT jmethodID throwable_to_string_method;
EXPORT jmethodID throwable_get_message_method;

EXPORT jclass java_function_class;
EXPORT jmethodID java_function_method;

EXPORT jclass luajava_api_class;

EXPORT jclass java_lang_class;
EXPORT jmethodID for_name_method;

inline lua_Number getStateIndex(lua_State * L) {
  lua_pushstring(L, LUAJAVASTATEINDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);

  if (!lua_isnumber(L, -1)) {
    luaL_error(L, "Impossible to identify luaState id.");
  }

  lua_Number stateIndex = lua_tonumber(L, -1);
  lua_pop(L, 1);
  return stateIndex;
}

jobject luajava_open(JNIEnv * env, jint stateid);

int isJavaObject(lua_State * L, int idx);
int isJavaFunction(lua_State * L, int index);
lua_State * getStateFromCPtr(JNIEnv * env, jobject cptr);
JNIEnv * getEnvFromState(lua_State * L);
void pushJNIEnv(JNIEnv * env, lua_State * L);
int handleJavaException(JNIEnv * env, lua_State * L);

int pushJavaObject(lua_State * L, jobject javaobject);
int pushJavaFunction(lua_State * L, jobject javafunction);
int pushJavaClass(lua_State * L, jobject javaObject);
int pushJavaArray(lua_State * L, jobject javaarray);
int pushJavaObject(lua_State * L, jobject javaobject);

jclass findJavaClass(JNIEnv * env, lua_State * L, const char * className);

int luaJavaFunctionCall(lua_State * L);
int gc(lua_State * L);
int arrayNewIndex(lua_State * L);
int arrayIndex(lua_State * L);
int classIndex(lua_State * L);
int objectNewIndex(lua_State * L);
int objectIndex(lua_State * L);
int objectIndexReturn(lua_State * L);

extern jclass throwable_class;
extern jmethodID throwable_to_string_method;
extern jmethodID throwable_get_message_method;
extern jclass java_function_class;
extern jmethodID java_function_method;
extern jclass luajava_api_class;
extern jclass java_lang_class;
extern jmethodID for_name_method;

#endif /* LUAJAVA_H! */