/************************************************************************
* Copyright (C) 2003-2007 Kepler Project.
*
* Permission is hereby granted, free of charge, to any person obtaining
* a copy of this software and associated documentation files (the
* "Software"), to deal in the Software without restriction, including
* without limitation the rights to use, copy, modify, merge, publish,
* distribute, sublicense, and/or sell copies of the Software, and to
* permit persons to whom the Software is furnished to do so, subject to
* the following conditions:
*
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
* MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
* IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
* CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
* SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
************************************************************************/

/***************************************************************************
*
*    This module is the implementation of luajava's dynamic library.
*    In this module functions in the LuaJava class that will be used
*    are exported to lua so that Java Objects' functions can be called.
*
*****************************************************************************/

#include "luajava.h"

jclass throwable_class;
jmethodID throwable_to_string_method;
jmethodID throwable_get_message_method;

jclass luajava_api_class;
jclass java_function_class;
jmethodID java_function_method;
jclass java_lang_class;
jmethodID for_name_method;

/**
 * Originally make of Java_org_keplerproject_luajava_LuaState_luajava_1open
 * and Java_org_keplerproject_luajava_LuaState__1open:
 * Initializes lua State to be used by luajava
 */
EXPORT jobject luajava_open(JNIEnv * env, jint stateid) {
 /* Original LuaState::_open() */
  lua_State * L = luaL_newstate();
  // TODO: this should be customizable as was in LuaJava
  // Complaint: why did anyone bother changing it?
  luaL_openlibs(L);

  lua_pushstring(L, LUAJAVASTATEINDEX);
  lua_pushnumber(L, (lua_Number)stateid);
  lua_settable(L, LUA_REGISTRYINDEX);

  jobject obj;
  jclass tempClass;

  tempClass = env->FindClass(CPTRCLASS);

  obj = env->AllocObject(tempClass);
  if (obj) {
    env->SetLongField(obj, env->GetFieldID(tempClass, "peer", "J"), (jlong) L);
  }

  /* Orginal LuaState::luajava_open */
  if (luajava_api_class == NULL) {
    tempClass = env->FindClass(LUAJAVACLASS);

    if (tempClass == NULL) {
      // TODO: throw
      fprintf(stderr, "Could not find LuaJava class");
      exit(1);
    }

    if ((luajava_api_class = (jclass)env->NewGlobalRef(tempClass)) == NULL) {
      fprintf(stderr, "Could not bind to LuaJavaAPI class");
      exit(1);
    }
  }

  if (java_function_class == NULL) {
    tempClass = env->FindClass(JAVAFUNCTIONCLASS);

    if (tempClass == NULL) {
      fprintf(stderr, "Could not find LuaFunction interface");
      exit(1);
    }

    if ((java_function_class = (jclass) env->NewGlobalRef(tempClass)) == NULL) {
      fprintf(stderr, "Could not bind to LuaFunction interface");
      exit(1);
    }
  }

  if (java_function_method == NULL) {
    java_function_method = env->GetMethodID(java_function_class, "call", "()I");

    if (!java_function_method) {
      fprintf(stderr, "Could not find <call> method in LuaFunction");
      exit(1);
    }
  }

  if (throwable_class == NULL) {
    tempClass = env->FindClass("java/lang/Throwable");

    if (tempClass == NULL) {
      fprintf(stderr, "Error. Couldn't bind java class java.lang.Throwable");
      exit(1);
    }

    throwable_class = (jclass) env->NewGlobalRef(tempClass);

    if (throwable_class == NULL) {
      fprintf(stderr, "Error. Couldn't bind java class java.lang.Throwable");
      exit(1);
    }
  }

  if (throwable_get_message_method == NULL) {
    throwable_get_message_method = env->GetMethodID(throwable_class, "getMessage", "()Ljava/lang/String;");

    if (throwable_get_message_method == NULL) {
      fprintf(stderr, "Could not find <getMessage> method in java.lang.Throwable");
      exit(1);
    }
  }

  if (throwable_to_string_method == NULL) {
    throwable_to_string_method = env->GetMethodID(throwable_class, "toString", "()Ljava/lang/String;");

    if (throwable_to_string_method == NULL) {
      fprintf(stderr, "Could not find <toString> method in java.lang.Throwable");
      exit(1);
    }
  }

  if (java_lang_class == NULL) {
    tempClass = env->FindClass("java/lang/Class");

    if (tempClass == NULL) {
      fprintf(stderr, "Error. Coundn't bind java class java.lang.Class\n");
      exit(1);
    }

    java_lang_class = (jclass) env->NewGlobalRef(tempClass);

    if (java_lang_class == NULL) {
      fprintf(stderr, "Error. Couldn't bind java class java.lang.Throwable\n");
      exit(1);
    }
  }

  if (for_name_method == NULL) {
    for_name_method = env->GetStaticMethodID(java_lang_class, "forName", "(Ljava/lang/String;)Ljava/lang/Class;");

    if (for_name_method == NULL) {
      fprintf(stderr, "Could not find <forName> method in java.lang.Class\n");
      exit(1);
    }
  }

  pushJNIEnv(env, L);

  return obj;
}

/**
 * Originally part of javaBindClass:
 * Implementation of lua function luajava.BindClass
 */
EXPORT jclass findJavaClass(JNIEnv * env, lua_State * L, const char * className) {
  jstring javaClassName = env->NewStringUTF(className);
  jclass classInstance = (jclass)env->CallStaticObjectMethod(java_lang_class, for_name_method, javaClassName);
  env->DeleteLocalRef(javaClassName);
  return classInstance;
}

/**
 * Tells whether element at index on stack is a Java object
 *
 * @returns 1 if yes, 0 if no
 */
EXPORT int isJavaObject(lua_State * L, int index) {
  if (!lua_isuserdata(L, index)) {
    return 0;
  }
  if (lua_getmetatable(L, index) == 0) {
    return 0;
  }

  lua_pushstring(L, LUAJAVAOBJECTIND);
  lua_rawget(L, -2);

  if (lua_isnil(L, -1)) {
    lua_pop(L, 2);
    return 0;
  }

  lua_pop(L, 2);
  return 1;
}

/**
 * Tells whether element at index on stack is a Java function
 * wrapped in LuaFunction
 *
 * @returns 1 if yes, 0 if no
 */
EXPORT int isJavaFunction(lua_State * L, int index) {
  if (!isJavaObject(L, index)) {
    return 0;
  }

  JNIEnv * env = getEnvFromState(L);
  jobject * obj = (jobject *)lua_touserdata(L, index);
  return env->IsInstanceOf(*obj, java_function_class);
}

/**
 * Returns the lua_State from the CPtr Java Object
 * and performs pushJNIEnv by the way
 */
EXPORT lua_State * getStateFromCPtr(JNIEnv * env, jobject cptr) {
  jclass classPtr = env->GetObjectClass(cptr);
  jfieldID CPtr_peer_ID = env->GetFieldID(classPtr, "peer", "J");
  jbyte * peer = (jbyte *) env->GetLongField(cptr, CPtr_peer_ID);

  lua_State * L = (lua_State *)peer;

  pushJNIEnv(env, L);

  return L;
}

/**
 * function that pushes the jni environment into the lua state
 * When an old JnIEnv* is already in the registry, it updates
 * to the current one.
 */
EXPORT void pushJNIEnv(JNIEnv * env, lua_State * L) {
  JNIEnv ** udEnv;

  lua_pushstring(L, LUAJAVAJNIENVTAG);
  lua_rawget(L, LUA_REGISTRYINDEX);

  if (!lua_isnil(L, -1)) {
    udEnv = (JNIEnv **)lua_touserdata(L, -1);
    *udEnv = env;
    lua_pop(L, 1);
  } else {
    lua_pop(L, 1);
    udEnv = (JNIEnv **)lua_newuserdata(L, sizeof(JNIEnv *));
    *udEnv = env;
    lua_pushstring(L, LUAJAVAJNIENVTAG);
    lua_insert(L, -2);
    lua_rawset(L, LUA_REGISTRYINDEX);
  }
}

/**
 * Function to create a lua proxy to a java object
 */
EXPORT int pushJavaObject(lua_State * L, jobject javaobject) {
  jobject * userData, globalRef;

  /* Gets the JNI Environment */
  JNIEnv * env = getEnvFromState(L);
  globalRef = env->NewGlobalRef(javaobject);

  userData = (jobject *)lua_newuserdata(L, sizeof(jobject));
  *userData = globalRef;

  /* Creates metatable */
  lua_newtable(L);

  /* pushes the __index metamethod */
  lua_pushstring(L, LUA_INDEXTAG);
  lua_pushcfunction(L, &objectIndex);
  lua_rawset(L, -3);

  /* pushes the __newindex metamethod */
  lua_pushstring(L, LUA_NEWINDEXTAG);
  lua_pushcfunction(L, &objectNewIndex);
  lua_rawset(L, -3);

  /* pushes the __gc metamethod */
  lua_pushstring(L, LUA_GCTAG);
  lua_pushcfunction(L, &gc);
  lua_rawset(L, -3);

  /* Is Java Object boolean */
  lua_pushstring(L, LUAJAVAOBJECTIND);
  lua_pushboolean(L, 1);
  lua_rawset(L, -3);

  if (lua_setmetatable(L, -2) == 0) {
    env->DeleteGlobalRef(globalRef);
    luaL_error(L, "Cannot create proxy to java object.");
  }

  return 1;
}

/**
 * Function to create a lua proxy to a java array
 */
EXPORT int pushJavaArray(lua_State * L, jobject javaarray) {
  jobject * userData, globalRef;

  /* Gets the JNI Environment */
  JNIEnv * env = getEnvFromState(L);
  globalRef = env->NewGlobalRef(javaarray);
  userData = (jobject *)lua_newuserdata(L, sizeof(jobject));
  *userData = globalRef;

  /* Creates metatable */
  lua_newtable(L);

  /* pushes the __index metamethod */
  lua_pushstring(L, LUA_INDEXTAG);
  lua_pushcfunction(L, &arrayIndex);
  lua_rawset(L, -3);

  /* pushes the __newindex metamethod */
  lua_pushstring(L, LUA_NEWINDEXTAG);
  lua_pushcfunction(L, &arrayNewIndex);
  lua_rawset(L, -3);

  /* pushes the __gc metamethod */
  lua_pushstring(L, LUA_GCTAG);
  lua_pushcfunction(L, &gc);
  lua_rawset(L, -3);

  /* Is Java Object boolean */
  lua_pushstring(L, LUAJAVAOBJECTIND);
  lua_pushboolean(L, 1);
  lua_rawset(L, -3);

  if (lua_setmetatable(L, -2) == 0) {
    env->DeleteGlobalRef(globalRef);
    luaL_error(L, "Cannot create proxy to java object.");
  }

  return 1;
}

/**
 * Function to create a lua proxy to a java class
 */
EXPORT int pushJavaClass(lua_State * L, jobject javaObject) {
  jobject * userData, globalRef;

  /* Gets the JNI Environment */
  JNIEnv * env = getEnvFromState(L);
  globalRef = env->NewGlobalRef(javaObject);
  userData = (jobject *) lua_newuserdata(L, sizeof(jobject));
  *userData = globalRef;

  /* Creates metatable */
  lua_newtable(L);

  /* pushes the __index metamethod */
  lua_pushstring(L, LUA_INDEXTAG);
  lua_pushcfunction(L, &classIndex);
  lua_rawset(L, -3);

  /* pushes the __newindex metamethod */
  lua_pushstring(L, LUA_NEWINDEXTAG);
  lua_pushcfunction(L, &objectNewIndex);
  lua_rawset(L, -3);

  /* pushes the __gc metamethod */
  lua_pushstring(L, LUA_GCTAG);
  lua_pushcfunction(L, &gc);
  lua_rawset(L, -3);

  /* Is Java Object boolean */
  lua_pushstring(L, LUAJAVAOBJECTIND);
  lua_pushboolean(L, 1);
  lua_rawset(L, -3);

  if (lua_setmetatable(L, -2) == 0) {
    env->DeleteGlobalRef(globalRef);
    luaL_error(L, "Cannot create proxy to java class.");
  }

  return 1;
}

/**
 * Pushes a JavaFunction into the state stack
 */
EXPORT int pushJavaFunction(lua_State * L, jobject javafunction) {
  jobject * userData, globalRef;

  JNIEnv * env = getEnvFromState(L);
  globalRef = env->NewGlobalRef(javafunction);
  userData = (jobject *)lua_newuserdata(L, sizeof(jobject));
  *userData = globalRef;

  /* Creates metatable */
  lua_newtable(L);

  /* pushes the __index metamethod */
  lua_pushstring(L, LUA_CALLTAG);
  lua_pushcfunction(L, &luaJavaFunctionCall);
  lua_rawset(L, -3);

  /* pusher the __gc metamethod */
  lua_pushstring(L, LUA_GCTAG);
  lua_pushcfunction(L, &gc);
  lua_rawset(L, -3);

  /* Is Java Object boolean */
  lua_pushstring(L, LUAJAVAOBJECTIND);
  lua_pushboolean(L, 1);
  lua_rawset(L, -3);
  lua_setmetatable(L, -2);

  return 1;
}

/**
 * auxiliary function to get the JNIEnv from the lua state
 *
 * @returns not null (JNIEnv *)
 */
EXPORT JNIEnv * getEnvFromState(lua_State * L) {
  JNIEnv ** udEnv;

  lua_pushstring(L, LUAJAVAJNIENVTAG);
  lua_rawget(L, LUA_REGISTRYINDEX);

  if (!lua_isuserdata(L, -1)) {
    lua_pop(L, 1);
    luaL_error(L, "Invalid JNI Environment.");
  }

  udEnv = (JNIEnv **)lua_touserdata(L, -1);

  lua_pop(L, 1);

  JNIEnv * env = *udEnv;

  if (env == NULL) {
    luaL_error(L, "Invalid JNI Environment.");
  }

  return env;
}

/**
 * Handles exceptions from Java side by calling lua_error
 *
 * @returns 1
 */
EXPORT int handleJavaException(JNIEnv * env, lua_State * L) {
  jthrowable exp = env->ExceptionOccurred();

  if (exp != NULL) {
    env->ExceptionClear();

    jstring jstr = (jstring) env->CallObjectMethod(exp, throwable_get_message_method);

    if (jstr == NULL) {
      jstr = (jstring) env->CallObjectMethod(exp, throwable_to_string_method);
    }

    const char * str = env->GetStringUTFChars(jstr, NULL);
    luaL_error(L, str);
    env->ReleaseStringUTFChars(jstr, str);
  }

  return 1;
}

/**
 * Function returned by the metamethod __index of a java Object. It is
 * the actual function that is going to call the java method.
 *
 * @param L      lua State
 * @param Stack  Parameters will be received by the stack
 * @returns (int) Number of values to be returned by the function
 */
EXPORT int objectIndexReturn(lua_State * L) {
  lua_Number stateIndex = getStateIndex(L);

  if (!isJavaObject(L, 1)) {
    luaL_error(L, "Not a valid OO function call.");
  }

  lua_getmetatable(L, 1);

  if (lua_type(L, -1) == LUA_TNIL) {
    luaL_error(L, "Not a valid java Object.");
  }

  lua_pushstring(L, LUAJAVAOBJFUNCCALLED);
  lua_rawget(L, -2);

  if (lua_type(L, -1) == LUA_TNIL) {
    luaL_error(L, "Not a OO function call.");
  }

  const char * methodName = lua_tostring(L, -1);

  lua_pop(L, 2);
  jobject * pObject = (jobject*) lua_touserdata(L, 1);
  JNIEnv * env = getEnvFromState(L);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "objectIndex" , "(ILjava/lang/Object;Ljava/lang/String;)I");
  jstring str = env->NewStringUTF(methodName);
  jint ret = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, *pObject, str);
  handleJavaException(env, L);
  env->DeleteLocalRef(str);

  return ret;
}

/**
 * Function to be called by the metamethod __index of the java object
 *
 * @param L     lua State
 * @param Stack Parameters will be received by the stack
 * @returns (int) Number of values to be returned by the function
*/
EXPORT int objectIndex(lua_State * L) {
  lua_Number stateIndex = getStateIndex(L);

  if (!lua_isstring(L, -1)) {
    luaL_error(L, "Invalid object index. Must be string.");
  }

  const char * key = lua_tostring(L, -1);

  if (!isJavaObject(L, 1)) {
    luaL_error(L, "Not a valid Java Object.");
  }

  JNIEnv * env = getEnvFromState(L);
  jobject * obj = (jobject *) lua_touserdata(L, 1);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "checkField", "(ILjava/lang/Object;Ljava/lang/String;)I");
  jstring str = env->NewStringUTF(key);
  jint checkField = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, *obj, str);
  handleJavaException(env, L);
  env->DeleteLocalRef(str);

  if (checkField != 0) {
    return checkField;
  }

  lua_getmetatable(L, 1);

  if (!lua_istable(L, -1)) {
    luaL_error(L, "Invalid Metatable.");
  }

  lua_pushstring(L, LUAJAVAOBJFUNCCALLED);
  lua_pushstring(L, key);
  lua_rawset(L, -3);
  lua_pop(L, 1);
  lua_pushcfunction(L, &objectIndexReturn);

  return 1;
}

/**
 * Function to be called by the metamethod __newindex of the java object
 *
 * @param L     lua State
 * @param Stack Parameters will be received by the stack
 * @returns (int) Number of values to be returned by the function
 */
EXPORT int objectNewIndex(lua_State * L) {
  lua_Number stateIndex = getStateIndex(L);

  if (!isJavaObject(L, 1)) {
    luaL_error(L, "Not a valid java class.");
  }

  if (!lua_isstring(L, 2)) {
    luaL_error(L, "Not a valid field call.");
  }

  const char * fieldName = lua_tostring(L, 2);
  jobject * obj = (jobject*)lua_touserdata(L, 1);
  JNIEnv * env = getEnvFromState(L);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "objectNewIndex" , "(ILjava/lang/Object;Ljava/lang/String;)I");
  jstring str = env->NewStringUTF(fieldName);
  jint ret = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, *obj, str);
  handleJavaException(env, L);
  env->DeleteLocalRef(str);

  return ret;
}

/**
 * Function to be called by the metamethod __index of the java class
 *
 * @param L     lua State
 * @param Stack Parameters will be received by the stack
 * @returns (int) Number of values to be returned by the function
 */
EXPORT int classIndex(lua_State * L) {
  lua_Number stateIndex = getStateIndex(L);

  if (!isJavaObject(L, 1)) {
    luaL_error(L, "Not a valid java class.");
  }

  if (!lua_isstring(L, 2)) {
    luaL_error(L, "Not a valid field call.");
  }

  const char * fieldName = lua_tostring(L, 2);
  jobject * obj = (jobject*) lua_touserdata(L, 1);
  JNIEnv * env = getEnvFromState(L);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "classIndex" , "(ILjava/lang/Class;Ljava/lang/String;)I");
  jstring str = env->NewStringUTF(fieldName);
  jint ret = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, *obj, str);
  handleJavaException(env, L);
  env->DeleteLocalRef(str);

  if (ret == 0) {
    luaL_error(L, "Name is not a static field or function.");
  }

  if (ret == 2) {
    lua_getmetatable(L, 1);
    lua_pushstring(L, LUAJAVAOBJECTIND);
    lua_pushstring(L, fieldName);
    lua_rawset(L, -3);
    lua_pop(L, 1);
    lua_pushcfunction(L, &objectIndexReturn);

    return 1;
  }

  return ret;
}

/**
 * Function to be called by the metamethod __index of a java array
 *
 * @param L     lua State
 * @param Stack Parameters will be received by the stack
 * @returns (int) Number of values to be returned by the function
 */
EXPORT int arrayIndex(lua_State * L) {
  /* Can index as number or string */
  if (!lua_isnumber(L, -1) && !lua_isstring(L, -1)) {
    luaL_error(L, "Invalid object index. Must be integer or string.");
  }

  /* Important! If the index is not a number, behave as normal Java object */
  if (!lua_isnumber(L, -1)) {
    return objectIndex(L);
  }
  /* Index is number */
  /* Gets the luaState index */
  lua_Number stateIndex = getStateIndex(L);

  // Array index
  lua_Integer key = lua_tointeger(L, -1);

  if (!isJavaObject(L, 1)) {
    luaL_error(L, "Not a valid Java Object.");
  }

  JNIEnv * env = getEnvFromState(L);
  jobject * obj = (jobject *) lua_touserdata(L, 1);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "arrayIndex", "(ILjava/lang/Object;I)I");
  jint ret = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, *obj, (jlong)key);
  handleJavaException(env, L);

  return ret;
}

/**
 * Function to be called by the metamethod __newindex of a java array
 *
 * @param L     lua State
 * @param Stack Parameters will be received by the stack
 * @returns (int) Number of values to be returned by the function
 */
EXPORT int arrayNewIndex(lua_State * L) {
  lua_Number stateIndex = getStateIndex(L);

  if (!isJavaObject(L, 1)) {
    luaL_error(L, "Not a valid java class.");
  }

  if (!lua_isnumber(L, 2)) {
    luaL_error(L, "Not a valid array index.");
  }

  lua_Integer key = lua_tointeger(L, 2);
  jobject * obj = (jobject*) lua_touserdata(L, 1);
  JNIEnv * env = getEnvFromState(L);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "arrayNewIndex", "(ILjava/lang/Object;I)I");
  jint ret = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, *obj, (jint)key);
  handleJavaException(env, L);

  return ret;
}

/**
 * Function to be called by the metamethod __gc of the java object
 *
 * @param L     lua State
 * @param Stack Parameters will be received by the stack
 * @returns (int) Number of values to be returned by the function
 */
EXPORT int gc(lua_State * L) {
  if (!isJavaObject(L, 1)) {
    return 0;
  }

  jobject *pObj = (jobject *)lua_touserdata(L, 1);

  JNIEnv * env = getEnvFromState(L);
  env->DeleteGlobalRef(*pObj);

  return 0;
}

/**
 * function called by metamethod __call of instances of JavaFunctionWrapper
 *
 * @param L     lua State
 * @param Stack Parameters will be received by the stack
 * @returns (int) Number of values to be returned by the function
 */
EXPORT int luaJavaFunctionCall(lua_State * L) {
  if (!isJavaObject(L, 1)) {
    luaL_error(L, "Not a java Function.");
  }

  jobject * obj = (jobject *)lua_touserdata(L, 1);
  JNIEnv * env = getEnvFromState(L);

  if (env->IsInstanceOf(*obj, java_function_class) == JNI_FALSE) {
    luaL_error(L, "Called Java object is not a JavaFunction");
  }

  int ret = env->CallIntMethod(*obj, java_function_method);
  handleJavaException(env, L);

  return ret;
}