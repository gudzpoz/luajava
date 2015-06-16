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

#ifndef nonlua_h
#define nonlua_h

#include <jni.h>
#include <cstdlib>

#include "lua.hpp"
#include "nonluaconf.h"

#define PRELOAD(name, function) \
  lua_getglobal(L, "package"); \
  lua_getfield(L, -1, "preload"); \
  lua_pushcfunction(L, function); \
  lua_setfield(L, -2, name); \
  lua_pop(L, 2);

NONLUA_API jclass throwable_class;
NONLUA_API jmethodID throwable_to_string_method;
NONLUA_API jmethodID throwable_get_message_method;
NONLUA_API jclass java_function_class;
NONLUA_API jmethodID java_function_method;
NONLUA_API jclass luajava_api_class;
NONLUA_API jclass java_lang_class;
NONLUA_API jmethodID for_name_method;

NONLUA_API jobject nonlua_open(JNIEnv * env, jint stateid);
NONLUA_API int nonlua_throw(JNIEnv * env, lua_State * L);

NONLUA_API int nonlua_pushobject(lua_State * L, jobject javaobject);
NONLUA_API int nonlua_pusharray(lua_State * L, jobject javaarray);
NONLUA_API int nonlua_pushclass(lua_State * L, jobject javaclass);
NONLUA_API int nonlua_pushfunction(lua_State * L, jobject javafunction);

NONLUA_API int nonlua_isobject(lua_State * L, int idx);
NONLUA_API int nonlua_isfunction(lua_State * L, int idx);
NONLUA_API jclass nonlua_findclass(JNIEnv * env, lua_State * L, const char * className);

NONLUA_API lua_State * nonlua_getstate(JNIEnv * env, jobject cptr);
NONLUA_API JNIEnv * nonlua_getenv(lua_State * L);
NONLUA_API void nonlua_pushenv(JNIEnv * env, lua_State * L);

#endif