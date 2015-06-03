#include <jni.h>
#include <cstdlib>

#ifdef __cplusplus
extern "C" {
#endif
#include <lua/lua.h>
#include <lua/lualib.h>
#include <lua/lauxlib.h>
#ifdef __cplusplus
}
#endif

#ifndef _Included_nonlua
#define _Included_nonlua

#define LUAJAVAJNIENVTAG "__JNIEnv"
#define LUAJAVAOBJECTIND "__IsJavaObject"
#define LUAJAVASTATEINDEX "LuaJavaStateIndex"
#define LUAINDEXMETAMETHODTAG "__index"
#define LUANEWINDEXMETAMETHODTAG "__newindex"
#define LUAGCMETAMETHODTAG "__gc"
#define LUACALLMETAMETHODTAG "__call"
#define LUAJAVAOBJFUNCCALLED "__FunctionCalled"

#ifdef __cplusplus
extern "C" {
#endif
extern jclass throwable_class;
extern jmethodID get_message_method;
extern jclass java_function_class;
extern jmethodID java_function_method;
extern jclass luajava_api_class;
extern jclass java_lang_class;
int objectIndex( lua_State * L );
int objectIndexReturn( lua_State * L );
int objectNewIndex( lua_State * L );
int classIndex( lua_State * L );
int arrayIndex( lua_State * L );
int arrayNewIndex( lua_State * L );
int gc( lua_State * L );
int javaBindClass( lua_State * L );
int createProxy( lua_State * L );
int javaNew( lua_State * L );
int javaNewInstance( lua_State * L );
int javaLoadLib( lua_State * L );
int pushJavaObject( lua_State * L , jobject javaObject );
int pushJavaArray( lua_State * L , jobject javaObject );
int pushJavaClass( lua_State * L , jobject javaObject );
int isJavaObject( lua_State * L , int idx );
lua_State * getStateFromCPtr( JNIEnv * env , jobject cptr );
int luaJavaFunctionCall( lua_State * L );
void pushJNIEnv( JNIEnv * env , lua_State * L );
JNIEnv * getEnvFromState( lua_State * L );
#ifdef __cplusplus
}
#endif
#endif