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

static jclass throwable_class                 = NULL;
jmethodID throwable_to_string_method   = NULL;
jmethodID throwable_get_message_method = NULL;

staticjclass luajava_api_class               = NULL;
jmethodID object_index_method          = NULL;
jmethodID object_new_index_method      = NULL;
jmethodID array_index_method           = NULL;
jmethodID array_new_index_method       = NULL;
jmethodID class_index_method           = NULL;
jmethodID check_field_method           = NULL;

jclass luajava_cptr_class              = NULL;
jfieldID cptr_peer_fieldID             = NULL;

jclass java_function_class             = NULL;
jmethodID java_function_method         = NULL;

static jclass java_lang_class                 = NULL;
jmethodID for_name_method              = NULL;

int objectIndex(lua_State * L)
{
  lua_Number stateIndex = getStateIndex(L);
  const char * key;
  jobject * obj;
  jstring str;
  JNIEnv * env;

  if (!lua_isstring(L, -1))
  {
    luaL_error(L, "Invalid object index. Must be string.");
  }

  key = lua_tostring(L, -1);

  if (!isJavaObject(L, 1)) {
    luaL_error(L, "Not a valid Java Object.");
  }

  env = getEnvFromState(L);
  obj = (jobject *) lua_touserdata(L, 1);
  str = env->NewStringUTF(key);
  jint checkField = env->CallStaticIntMethod(luajava_api_class, check_field_method, (jint)stateIndex, *obj, str);
  handleJavaException(env, L);
  // TODO: So local ref will not be deleted when exceptions occur
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
  * Function returned by the metamethod __index of a java Object. It is
  * the actual function that is going to call the java method.
  *
  * @param L      lua State
  * @param Stack  Parameters will be received by the stack
  * @returns (int) Number of values to be returned by the function
  */
 int objectIndexReturn(lua_State * L)
 {
   lua_Number stateIndex;
   jobject * pObject;
   const char * methodName;
   stateIndex = getStateIndex(L);

   if (!isJavaObject(L, 1))
   {
     luaL_error(L, "Not a valid OO function call.");
   }

   lua_getmetatable(L, 1);

   if (lua_type(L, -1) == LUA_TNIL) {
     luaL_error(L, "Not a valid java Object.");
   }

   lua_pushstring(L, LUAJAVAOBJFUNCCALLED);
   lua_rawget(L, -2);

   if (lua_type(L, -1) == LUA_TNIL)
   {
     luaL_error(L, "Not a OO function call.");
   }

   methodName = lua_tostring(L, -1);

   lua_pop(L, 2);
   pObject = (jobject*) lua_touserdata(L, 1);
   JNIEnv * env = getEnvFromState(L);
   jstring str = env->NewStringUTF(methodName);
   jint ret = env->CallStaticIntMethod(luajava_api_class, object_index_method,
                                       (jint)stateIndex, *pObject, str);
   /* Handles exception */
   handleJavaException(env, L);
   env->DeleteLocalRef(str);

   /* pushes new object into lua stack */
   return ret;
 }

/**
 * Function to be called by the metamethod __newindex of the java object
 *
 * @param L     lua State
 * @param Stack Parameters will be received by the stack
 * @returns (int) Number of values to be returned by the function
 */
int objectNewIndex(lua_State * L)
{
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
  jstring str = env->NewStringUTF(fieldName);
  jint ret = env->CallStaticIntMethod(luajava_api_class, object_new_index_method, (jint)stateIndex, *obj, str);
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
int classIndex(lua_State * L)
{
  /* Gets the luaState index */
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
  jstring str = env->NewStringUTF(fieldName);
  jint ret = env->CallStaticIntMethod(luajava_api_class, class_index_method, (jint)stateIndex, *obj, str);
  handleJavaException(env, L);
  env->DeleteLocalRef(str);

  if (ret == 0)
  {
    luaL_error(L, "Name is not a static field or function.");
  }

  if (ret == 2) {
    lua_getmetatable(L, 1);
    lua_pushstring(L, LUAJAVAOBJFUNCCALLED);
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
int arrayIndex(lua_State * L)
{
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
  jint ret = env->CallStaticIntMethod(luajava_api_class, array_index_method, (jint)stateIndex, *obj, (jlong)key);
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
int arrayNewIndex(lua_State * L)
{
  lua_Number stateIndex = getStateIndex(L);

  if (!isJavaObject(L, 1))
  {
    luaL_error(L, "Not a valid java class.");
  }

  if (!lua_isnumber(L, 2))
  {
    luaL_error(L, "Not a valid array index.");
  }

  lua_Integer key = lua_tointeger(L, 2);
  /* Gets the object reference */
  jobject * obj = (jobject*) lua_touserdata(L, 1);
  /* Gets the JNI Environment */
  JNIEnv * env = getEnvFromState(L);
  jint ret = env->CallStaticIntMethod(luajava_api_class, array_new_index_method, (jint)stateIndex, *obj, (jint)key);
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
int gc(lua_State * L)
{
  if (!isJavaObject(L, 1))
  {
    return 0;
  }

  jobject *pObj = (jobject *)lua_touserdata(L, 1);

  JNIEnv * env = getEnvFromState(L);
  //env->DeleteGlobalRef(*pObj);

  return 0;
}

/**
 * Originally part of javaBindClass:
 * Implementation of lua function luajava.BindClass
 */
EXPORT jclass findJavaClass(JNIEnv * env, lua_State * L, const char * className) {
  jstring javaClassName = env->NewStringUTF(className);
  jclass \
  classInstance \
  = (jclass)env->CallStaticObjectMethod(java_lang_class, for_name_method, javaClassName);

  env->DeleteLocalRef(javaClassName);
  return classInstance;
}

static int java_require (lua_State * L) {
  int top = lua_gettop(L);

  if (top != 1) {
    luaL_error(L, "Function java.require received %d arguments, expected 1.", top);
  }

  JNIEnv * env = getEnvFromState(L);

  if (!lua_isstring(L, 1)) {
    luaL_error(L, "Invalid parameter type. String expected.");
  }

  const char * className = lua_tostring(L, 1);
  jclass classInstance = findJavaClass(env, L, className);
  handleJavaException(env, L);
/* pushes new object into lua stack */
  return pushJavaClass(L, classInstance);
}

/*
* Function: createProxy
*/
static int java_proxy(lua_State * L) {
  if (lua_gettop(L) != 2) {
    luaL_error(L, "Error. Function proxy expects 2 arguments.");
  }

  lua_pushstring(L, LUAJAVASTATEINDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);

  if (!lua_isnumber(L, -1)) {
    luaL_error(L, "Impossible to identify luaState id.");
  }

  lua_Number stateIndex = lua_tonumber(L, -1);
  lua_pop(L, 1);

  if (!lua_isstring(L, 1) || !lua_istable(L, 2)) {
    luaL_error(L, "Invalid Argument types. Expected (string, table).");
  }

  JNIEnv * env = getEnvFromState(L);
  jmethodID method = env->GetStaticMethodID(luajava_api_class, "createProxyObject" , "(ILjava/lang/String;)I");
  const char *impl = lua_tostring(L, 1);

  jstring str = env->NewStringUTF(impl);
  jint ret = env->CallStaticIntMethod(luajava_api_class, method, (jint)stateIndex, str);
  handleJavaException(env, L);
  env->DeleteLocalRef(str);

  return ret;
}
/***************************************************************************
*
*  Function: javaNew
*  ****/
static int java_new(lua_State * L) {
  int top = lua_gettop(L);

  if (top == 0) {
    luaL_error(L, "Error. Invalid number of parameters.");
  }

  lua_pushstring(L, LUAJAVASTATEINDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);

  if (!lua_isnumber(L, -1)) {
    luaL_error(L, "Impossible to identify luaState id.");
  }

  lua_Number stateIndex = lua_tonumber(L, -1);
  lua_pop(L, 1);
/* Gets the java Class reference */
  if (!isJavaObject(L, 1)) {
    luaL_error(L, "Argument not a valid Java Class.");
  }
/* Gets the JNI Environment */
  JNIEnv * env = getEnvFromState(L);
  jclass clazz = env->FindClass("java/lang/Class");
  jobject * userData = (jobject *) lua_touserdata(L, 1);
  jobject classInstance = (jobject) *userData;

  if (env->IsInstanceOf(classInstance, clazz) == JNI_FALSE) {
    luaL_error(L,
    "Argument not a valid Java Class.");
  }

  jmethodID method = env->GetStaticMethodID(luajava_api_class, "javaNew", "(ILjava/lang/Class;)I");

  if (clazz == NULL || method == NULL)
   {
    luaL_error(L, "Invalid method javaNew.");
  }

  jint ret = env->CallStaticIntMethod(
  clazz, method, (jint)stateIndex, classInstance);
  handleJavaException(env, L);

  return ret;
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
 * Function to create a lua proxy to a java class
 */
int pushJavaClass(lua_State * L, jobject javaObject)
{
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
  * Function to create a lua proxy to a java object
  */
int pushJavaObject(lua_State * L, jobject javaobject)
{
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
 * Returns the lua_State from the CPtr Java Object
 * and performs pushJNIEnv by the way
 */
lua_State * getStateFromCPtr(JNIEnv * env, jobject cptr)
{
  jbyte * peer = (jbyte *) env->GetLongField(cptr, cptr_peer_fieldID);

  lua_State * L = (lua_State *)peer;

  pushJNIEnv(env, L);

  return L;
}

/**
 * function called by metamethod __call of instances of JavaFunctionWrapper
 *
 * @param L     lua State
 * @param Stack Parameters will be received by the stack
 * @returns (int) Number of values to be returned by the function
 */
int luaJavaFunctionCall(lua_State * L)
{
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
 * Originally made of Java_org_keplerproject_luajava_LuaState_luajava_1open
 * and Java_org_keplerproject_luajava_LuaState__1open:
 * Initializes lua State to be used by luajava
 *
 * @throws LuaException when some Java exceptions occur
 */
EXPORT jobject luajava_open(JNIEnv * env, jint stateid) {
 /* Original LuaState::_open() */
  lua_State * L = luaL_newstate();
  jclass tempClass;
  // TODO: this should be customizable as was in LuaJava
  // Complaint: why did anyone bother changing it?
  luaL_openlibs(L);

  lua_pushstring(L, LUAJAVASTATEINDEX);
  lua_pushnumber(L, (lua_Number)stateid);
  lua_settable(L, LUA_REGISTRYINDEX);

  jobject obj;

  if (luajava_cptr_class == NULL) {
    tempClass = env->FindClass(CPTRCLASS);

    if (tempClass == NULL) {
      throw LuaException("Could not find CPtr class");
    }

    if ((luajava_cptr_class = (jclass)env->NewGlobalRef(tempClass)) == NULL) {
      throw LuaException("Could not bind to CPtr class");
    }
  }

  if (cptr_peer_fieldID == NULL) {
    cptr_peer_fieldID = env->GetFieldID(luajava_cptr_class, "peer", "J");

    if (cptr_peer_fieldID == NULL) {
      throw LuaException("Could not find <peer> field in CPtr");
    }
  }

  obj = env->AllocObject(luajava_cptr_class);
  if (obj) {
    env->SetLongField(obj, cptr_peer_fieldID, (jlong) L);
  } else {
    throw LuaException("Could not allocate a new CPtr object");
  }

  /* Orginal LuaState::luajava_open */
  if (luajava_api_class == NULL) {
    tempClass = env->FindClass(LUAJAVACLASS);

    if (tempClass == NULL) {
      throw LuaException(
      "Could not find LuaJavaAPI class"
      );
    }

    if ((luajava_api_class = (jclass)env->NewGlobalRef(tempClass)) == NULL) {
      throw LuaException(
      "Could not bind to LuaJavaAPI class"
      );
    }
  }

  if (object_index_method == NULL) {
    object_index_method = env->GetStaticMethodID(luajava_api_class, "objectIndex" , "(ILjava/lang/Object;Ljava/lang/String;)I");

    if (!object_index_method) {
      throw LuaException("Could not find <objectIndex> method in LuaJava");
    }
  }

  if (object_new_index_method == NULL) {
    object_new_index_method = env->GetStaticMethodID(luajava_api_class, "objectNewIndex" , "(ILjava/lang/Object;Ljava/lang/String;)I");

    if (!object_new_index_method) {
      throw LuaException("Could not find <objectNewIndex> method in LuaJava");
    }
  }

  if (array_index_method == NULL) {
    array_index_method = env->GetStaticMethodID(luajava_api_class, "arrayIndex", "(ILjava/lang/Object;I)I");

    if (!array_index_method) {
      throw LuaException("Could not find <arrayIndex> method in LuaJava");
    }
  }

  if (array_new_index_method == NULL) {
    array_new_index_method = env->GetStaticMethodID(luajava_api_class, "arrayNewIndex", "(ILjava/lang/Object;I)I");

    if (!array_new_index_method) {
      throw LuaException("Could not find <arrayNewIndex> method in LuaJava");
    }
  }

  if (check_field_method == NULL) {
    check_field_method = env->GetStaticMethodID(luajava_api_class, "checkField", "(ILjava/lang/Object;Ljava/lang/String;)I");

    if (!check_field_method) {
      throw LuaException("Could not find <checkField> method in LuaJava");
    }
  }

  if (class_index_method == NULL) {
    class_index_method = env->GetStaticMethodID(luajava_api_class, "classIndex" , "(ILjava/lang/Class;Ljava/lang/String;)I");

    if (!class_index_method) {
      throw LuaException("Could not find <classIndex> method in LuaJava");
    }
  }

  if (java_function_class == NULL)
  {
    tempClass = env->FindClass(JAVAFUNCTIONCLASS);

    if (tempClass == NULL) {
      throw LuaException("Could not find LuaFunction interface");
    }

    if ((java_function_class = (jclass) env->NewGlobalRef(tempClass)) == NULL) {
      throw LuaException("Could not bind to LuaFunction interface");
    }
  }

  if (java_function_method == NULL) {
    java_function_method = env->GetMethodID(java_function_class, "call", "()I");

    if (!java_function_method) {
      throw LuaException("Could not find <call> method in LuaFunction");
    }
  }

  if (throwable_class == NULL)
  {
    tempClass = env->FindClass("java/lang/Throwable");

    if (tempClass == NULL)
    {
      throw LuaException("Error. Couldn't bind java class java.lang.Throwable");
    }

    throwable_class = (jclass) env->NewGlobalRef(tempClass);

    if (throwable_class == NULL)
    {
      throw LuaException("Error. Couldn't bind java class java.lang.Throwable");
    }
  }

  if (throwable_get_message_method == NULL) {
    throwable_get_message_method = env->GetMethodID(throwable_class, "getMessage", "()Ljava/lang/String;");

    if (throwable_get_message_method == NULL) {
      throw LuaException("Could not find <getMessage> method in java.lang.Throwable");
    }
  }

  if (throwable_to_string_method == NULL) {
    throwable_to_string_method = env->GetMethodID(throwable_class, "toString", "()Ljava/lang/String;");

    if (throwable_to_string_method == NULL) {
      throw LuaException("Could not find <toString> method in java.lang.Throwable");
    }
  }

  if (java_lang_class == NULL)
  {
    tempClass = env->FindClass("java/lang/Class");

    if (tempClass == NULL) {
      throw LuaException("Error. Coundn't bind java class java.lang.Class");
    }

    java_lang_class = (jclass) env->NewGlobalRef(tempClass);

    if (java_lang_class == NULL) {
      throw LuaException("Error. Couldn't bind java class java.lang.Throwable");
    }
  }

  if (for_name_method == NULL) {
    for_name_method = env->GetStaticMethodID(java_lang_class, "forName", "(Ljava/lang/String;)Ljava/lang/Class;");

    if (for_name_method == NULL) {
      throw LuaException("Could not find <forName> method in java.lang.Class");
    }
  }

  pushJNIEnv(env, L);

  return obj;
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
