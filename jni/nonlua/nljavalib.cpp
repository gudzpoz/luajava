/*******************************************************************************
 * Copyright (c) 2015 Thomas Slusny.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

#include "nonlua.h"
#include "nonlualib.h"

static int java_require (lua_State * L) {
  int top = lua_gettop(L);

  if (top != 1) {
    luaL_error(L, "Function java.require received %d arguments, expected 1.", top);
  }

  JNIEnv * env = nonlua_getenv(L);

  if (!lua_isstring(L, 1)) {
    luaL_error(L, "Invalid parameter type. String expected.");
  }

  const char * className = lua_tostring(L, 1);
  jclass classInstance = nonlua_findclass(env, L, className);
  nonlua_throw(env, L);

  return nonlua_pushclass(L, classInstance);
}

static int java_new(lua_State * L) {
  int top = lua_gettop(L);

  if (top == 0) {
    luaL_error(L, "Error. Invalid number of parameters.");
  }

  lua_pushstring(L, NONLUA_STATEINDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);

  if (!lua_isnumber(L, -1)) {
    luaL_error(L, "Impossible to identify luaState id.");
  }

  lua_Number stateIndex = lua_tonumber(L, -1);
  lua_pop(L, 1);

  if (!nonlua_isobject(L, 1)) {
    luaL_error(L, "Argument not a valid Java Class.");
  }

  JNIEnv * env = nonlua_getenv(L);
  jclass clazz = env->FindClass("java/lang/Class");
  jobject * userData = (jobject *) lua_touserdata(L, 1);
  jobject classInstance = (jobject) *userData;

  if (env->IsInstanceOf(classInstance, clazz) == JNI_FALSE) {
    luaL_error(L, "Argument not a valid Java Class.");
  }

  jmethodID method = env->GetStaticMethodID(luajava_api_class, "javaNew", "(ILjava/lang/Class;)I");

  if (clazz == NULL || method == NULL) {
    luaL_error(L, "Invalid method javaNew.");
  }

  jint ret = env->CallStaticIntMethod(clazz, method, (jint)stateIndex, classInstance);
  nonlua_throw(env, L);

  return ret;
}

static int java_proxy(lua_State * L) {
  if (lua_gettop(L) != 2) {
    luaL_error(L, "Error. Function proxy expects 2 arguments.");
  }

  lua_pushstring(L, NONLUA_STATEINDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);

  if (!lua_isnumber(L, -1)) {
    luaL_error(L, "Impossible to identify luaState id.");
  }

  lua_Number stateIndex = lua_tonumber(L, -1);
  lua_pop(L, 1);

  if (!lua_isstring(L, 1) || !lua_istable(L, 2)) {
    luaL_error(L, "Invalid Argument types. Expected (string, table).");
  }

  JNIEnv * env = nonlua_getenv(L);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "createProxyObject" , "(ILjava/lang/String;)I");
  const char *impl = lua_tostring(L, 1);

  jstring str = env->NewStringUTF(impl);
  jint ret = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, str);
  nonlua_throw(env, L);
  env->DeleteLocalRef(str);

  return ret;
}

static int java_loadlib(lua_State * L) {
  int top = lua_gettop(L);

  if (top != 2) {
    luaL_error(L, "Error. Invalid number of parameters.");
  }

  lua_pushstring(L, NONLUA_STATEINDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);

  if (!lua_isnumber(L, -1)) {
    luaL_error(L, "Impossible to identify luaState id.");
  }

  lua_Number stateIndex = lua_tonumber(L, -1);
  lua_pop(L, 1);

  if (!lua_isstring(L, 1) || !lua_isstring(L, 2)) {
    luaL_error(L, "Invalid parameter. Strings expected.");
  }

  const char * className  = lua_tostring(L, 1);
  const char * methodName = lua_tostring(L, 2);

  JNIEnv * env = nonlua_getenv(L);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "javaLoadLib" , "(ILjava/lang/String;Ljava/lang/String;)I");
  jstring javaClassName  = env->NewStringUTF(className);
  jstring javaMethodName = env->NewStringUTF(methodName);
  jint ret = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, javaClassName, javaMethodName);
  nonlua_throw(env, L);
  env->DeleteLocalRef(javaClassName);
  env->DeleteLocalRef(javaMethodName);

  return ret;
}

static int java_file(lua_State * L) {
  int top = lua_gettop(L);

  if (top == 0) {
    luaL_error(L, "Error. Invalid number of parameters.");
  }

  lua_pushstring(L, NONLUA_STATEINDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);

  if (!lua_isnumber(L, -1)) {
    luaL_error(L, "Impossible to identify luaState id.");
  }

  lua_Number stateIndex = lua_tonumber(L, -1);
  lua_pop(L, 1);

  if (!lua_isstring(L, 1)) {
    luaL_error(L, "Argument not a lua string.");
  }

  const char * filename  = lua_tostring(L, 1);

  JNIEnv * env = nonlua_getenv(L);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "javaFile", "(ILjava/lang/String;)I");
  jstring javaFilename  = env->NewStringUTF(filename);
  jint ret = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, javaFilename);
  nonlua_throw(env, L);
  env->DeleteLocalRef(javaFilename);

  return ret;
}

static int java_topath(lua_State * L) {
  int top = lua_gettop(L);

  if (top == 0) {
    luaL_error(L, "Error. Invalid number of parameters.");
  }

  lua_pushstring(L, NONLUA_STATEINDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);

  if (!lua_isnumber(L, -1)) {
    luaL_error(L, "Impossible to identify luaState id.");
  }

  lua_Number stateIndex = lua_tonumber(L, -1);
  lua_pop(L, 1);

  if (!lua_isstring(L, 1)) {
    luaL_error(L, "Argument not a lua string.");
  }

  const char * filename  = lua_tostring(L, 1);

  JNIEnv * env = nonlua_getenv(L);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "javaToPath", "(ILjava/lang/String;)I");
  jstring javaFilename  = env->NewStringUTF(filename);
  jint ret = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, javaFilename);
  nonlua_throw(env, L);
  env->DeleteLocalRef(javaFilename);

  return ret;
}

static int java_tolibpath(lua_State * L) {
  int top = lua_gettop(L);

  if (top == 0) {
    luaL_error(L, "Error. Invalid number of parameters.");
  }

  lua_pushstring(L, NONLUA_STATEINDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);

  if (!lua_isnumber(L, -1)) {
    luaL_error(L, "Impossible to identify luaState id.");
  }

  lua_Number stateIndex = lua_tonumber(L, -1);
  lua_pop(L, 1);

  if (!lua_isstring(L, 1)) {
    luaL_error(L, "Argument not a lua string.");
  }

  const char * filename  = lua_tostring(L, 1);

  JNIEnv * env = nonlua_getenv(L);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "javaToLibPath", "(ILjava/lang/String;)I");
  jstring javaFilename  = env->NewStringUTF(filename);
  jint ret = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, javaFilename);
  nonlua_throw(env, L);
  env->DeleteLocalRef(javaFilename);

  return ret;
}

static const luaL_Reg javalib[] = {
  {"require",   java_require},
  {"new",       java_new},
  {"proxy",     java_proxy},
  {"loadlib",   java_loadlib},
  {"file",      java_file},
  {"topath",    java_topath},
  {"tolibpath", java_tolibpath},
  {NULL, NULL}
};

NONLUA_API int luaopen_java (lua_State *L) {
  luaL_register(L, LUA_JAVALIBNAME, javalib);
  return 1;
}