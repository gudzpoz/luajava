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

package io.nondev.nonlua;

import com.badlogic.gdx.jnigen.JniGenSharedLibraryLoader;
import java.io.*;

public class Lua {
    // @off
    /*JNI
    #include <jni.h>
    #include <lua/lua.h>
    #include <lua/lualib.h>
    #include <lua/lauxlib.h>

    #define LUAJAVAJNIENVTAG      "__JNIEnv"
    #define LUAJAVAOBJECTIND      "__IsJavaObject"
    #define LUAJAVASTATEINDEX     "LuaJavaStateIndex"
    #define LUAINDEXMETAMETHODTAG "__index"
    #define LUANEWINDEXMETAMETHODTAG "__newindex"
    #define LUAGCMETAMETHODTAG    "__gc"
    #define LUACALLMETAMETHODTAG  "__call"
    #define LUAJAVAOBJFUNCCALLED  "__FunctionCalled"

    static jclass    throwable_class      = NULL;
    static jmethodID get_message_method   = NULL;
    static jclass    java_function_class  = NULL;
    static jmethodID java_function_method = NULL;
    static jclass    luajava_api_class    = NULL;
    static jclass    java_lang_class      = NULL;

    int objectIndex( lua_State * L )
    {
       lua_Number stateIndex;
       const char * key;
       jmethodID method;
       jint checkField;
       jobject * obj;
       jstring str;
       jthrowable exp;
       JNIEnv * javaEnv;

       lua_pushstring( L , LUAJAVASTATEINDEX );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isnumber( L , -1 ) )
       {
          lua_pushstring( L , "Impossible to identify luaState id." );
          lua_error( L );
       }

       stateIndex = lua_tonumber( L , -1 );
       lua_pop( L , 1 );

       if ( !lua_isstring( L , -1 ) )
       {
          lua_pushstring( L , "Invalid object index. Must be string." );
          lua_error( L );
       }

       key = lua_tostring( L , -1 );

       if ( !isJavaObject( L , 1 ) )
       {
          lua_pushstring( L , "Not a valid Java Object." );
          lua_error( L );
       }

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       obj = ( jobject * ) lua_touserdata( L , 1 );

       method = ( *javaEnv )->GetStaticMethodID( javaEnv , luajava_api_class , "checkField" ,
                                                 "(ILjava/lang/Object;Ljava/lang/String;)I" );

       str = ( *javaEnv )->NewStringUTF( javaEnv , key );

       checkField = ( *javaEnv )->CallStaticIntMethod( javaEnv , luajava_api_class , method ,
                                                       (jint)stateIndex , *obj , str );

       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          ( *javaEnv )->DeleteLocalRef( javaEnv , str );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       ( *javaEnv )->DeleteLocalRef( javaEnv , str );

       if ( checkField != 0 )
       {
          return checkField;
       }

       lua_getmetatable( L , 1 );

       if ( !lua_istable( L , -1 ) )
       {
          lua_pushstring( L , "Invalid MetaTable." );
          lua_error( L );
       }

       lua_pushstring( L , LUAJAVAOBJFUNCCALLED );
       lua_pushstring( L , key );
       lua_rawset( L , -3 );

       lua_pop( L , 1 );

       lua_pushcfunction( L , &objectIndexReturn );

       return 1;
    }

    int objectIndexReturn( lua_State * L )
    {
       lua_Number stateIndex;
       jobject * pObject;
       jmethodID method;
       jthrowable exp;
       const char * methodName;
       jint ret;
       jstring str;
       JNIEnv * javaEnv;

       lua_pushstring( L , LUAJAVASTATEINDEX );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isnumber( L , -1 ) )
       {
          lua_pushstring( L , "Impossible to identify luaState id." );
          lua_error( L );
       }

       stateIndex = lua_tonumber( L , -1 );
       lua_pop( L , 1 );

       if ( !isJavaObject( L , 1 ) )
       {
          lua_pushstring( L , "Not a valid OO function call." );
          lua_error( L );
       }

       lua_getmetatable( L , 1 );
       if ( lua_type( L , -1 ) == LUA_TNIL )
       {
          lua_pushstring( L , "Not a valid java Object." );
          lua_error( L );
       }

       lua_pushstring( L , LUAJAVAOBJFUNCCALLED );
       lua_rawget( L , -2 );
       if ( lua_type( L , -1 ) == LUA_TNIL )
       {
          lua_pushstring( L , "Not a OO function call." );
          lua_error( L );
       }
       methodName = lua_tostring( L , -1 );

       lua_pop( L , 2 );

       pObject = ( jobject* ) lua_touserdata( L , 1 );

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }


       method = ( *javaEnv )->GetStaticMethodID( javaEnv , luajava_api_class , "objectIndex" ,
                                                 "(ILjava/lang/Object;Ljava/lang/String;)I" );

       str = ( *javaEnv )->NewStringUTF( javaEnv , methodName );

       ret = ( *javaEnv )->CallStaticIntMethod( javaEnv , luajava_api_class , method , (jint)stateIndex , 
                                                *pObject , str );

       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          ( *javaEnv )->DeleteLocalRef( javaEnv , str );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       ( *javaEnv )->DeleteLocalRef( javaEnv , str );

       return ret;
    }

    int objectNewIndex( lua_State * L  )
    {
       lua_Number stateIndex;
       jobject * obj;
       jmethodID method;
       const char * fieldName;
       jstring str;
       jint ret;
       jthrowable exp;
       JNIEnv * javaEnv;

       lua_pushstring( L , LUAJAVASTATEINDEX );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isnumber( L , -1 ) )
       {
          lua_pushstring( L , "Impossible to identify luaState id." );
          lua_error( L );
       }

       stateIndex = lua_tonumber( L , -1 );
       lua_pop( L , 1 );

       if ( !isJavaObject( L , 1 ) )
       {
          lua_pushstring( L , "Not a valid java class." );
          lua_error( L );
       }

       if ( !lua_isstring( L , 2 ) )
       {
          lua_pushstring( L , "Not a valid field call." );
          lua_error( L );
       }

       fieldName = lua_tostring( L , 2 );

       obj = ( jobject* ) lua_touserdata( L , 1 );

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       method = ( *javaEnv )->GetStaticMethodID( javaEnv , luajava_api_class , "objectNewIndex" ,
                                                 "(ILjava/lang/Object;Ljava/lang/String;)I" );

       str = ( *javaEnv )->NewStringUTF( javaEnv , fieldName );

       ret = ( *javaEnv )->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , 
                                                *obj , str );

       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          ( *javaEnv )->DeleteLocalRef( javaEnv , str );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       ( *javaEnv )->DeleteLocalRef( javaEnv , str );


       return ret;
    }

    int classIndex( lua_State * L )
    {
       lua_Number stateIndex;
       jobject * obj;
       jmethodID method;
       const char * fieldName;
       jstring str;
       jint ret;
       jthrowable exp;
       JNIEnv * javaEnv;

       lua_pushstring( L , LUAJAVASTATEINDEX );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isnumber( L , -1 ) )
       {
          lua_pushstring( L , "Impossible to identify luaState id." );
          lua_error( L );
       }

       stateIndex = lua_tonumber( L , -1 );
       lua_pop( L , 1 );

       if ( !isJavaObject( L , 1 ) )
       {
          lua_pushstring( L , "Not a valid java class." );
          lua_error( L );
       }

       if ( !lua_isstring( L , 2 ) )
       {
          lua_pushstring( L , "Not a valid field call." );
          lua_error( L );
       }

       fieldName = lua_tostring( L , 2 );

       obj = ( jobject* ) lua_touserdata( L , 1 );

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       method = ( *javaEnv )->GetStaticMethodID( javaEnv , luajava_api_class , "classIndex" ,
                                                 "(ILjava/lang/Class;Ljava/lang/String;)I" );

       str = ( *javaEnv )->NewStringUTF( javaEnv , fieldName );

       ret = ( *javaEnv )->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , 
                                                *obj , str );

       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          ( *javaEnv )->DeleteLocalRef( javaEnv , str );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       ( *javaEnv )->DeleteLocalRef( javaEnv , str );

       if ( ret == 0 )
       {
          lua_pushstring( L , "Name is not a static field or function." );
          lua_error( L );
       }

       if ( ret == 2 )
       {
          lua_getmetatable( L , 1 );
          lua_pushstring( L , LUAJAVAOBJFUNCCALLED );
          lua_pushstring( L , fieldName );
          lua_rawset( L , -3 );

          lua_pop( L , 1 );

          lua_pushcfunction( L , &objectIndexReturn );

          return 1;
       }

       return ret;
    }

    int arrayIndex( lua_State * L )
    {
       lua_Number stateIndex;
       lua_Integer key;
       jmethodID method;
       jint ret;
       jobject * obj;
       jthrowable exp;
       JNIEnv * javaEnv;

       if ( !lua_isnumber( L , -1 ) && !lua_isstring( L , -1 ) )
       {
          lua_pushstring( L , "Invalid object index. Must be integer or string." );
          lua_error( L );
       }

        if ( !lua_isnumber( L , -1 ) )
        {
            return objectIndex( L );
        }

       lua_pushstring( L , LUAJAVASTATEINDEX );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isnumber( L , -1 ) )
       {
          lua_pushstring( L , "Impossible to identify luaState id." );
          lua_error( L );
       }

       stateIndex = lua_tonumber( L , -1 );
       lua_pop( L , 1 );

        // Array index
       key = lua_tointeger( L , -1 );

       if ( !isJavaObject( L , 1 ) )
       {
          lua_pushstring( L , "Not a valid Java Object." );
          lua_error( L );
       }

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       obj = ( jobject * ) lua_touserdata( L , 1 );

       method = ( *javaEnv )->GetStaticMethodID( javaEnv , luajava_api_class , "arrayIndex" ,
                                                 "(ILjava/lang/Object;I)I" );

       ret = ( *javaEnv )->CallStaticIntMethod( javaEnv , luajava_api_class , method ,
                                                       (jint)stateIndex , *obj , (jlong)key );

       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       return ret;
    }

    int arrayNewIndex( lua_State * L )
    {
       lua_Number stateIndex;
       jobject * obj;
       jmethodID method;
       lua_Integer key;
       jint ret;
       jthrowable exp;
       JNIEnv * javaEnv;

       lua_pushstring( L , LUAJAVASTATEINDEX );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isnumber( L , -1 ) )
       {
          lua_pushstring( L , "Impossible to identify luaState id." );
          lua_error( L );
       }

       stateIndex = lua_tonumber( L , -1 );
       lua_pop( L , 1 );

       if ( !isJavaObject( L , 1 ) )
       {
          lua_pushstring( L , "Not a valid java class." );
          lua_error( L );
       }

       if ( !lua_isnumber( L , 2 ) )
       {
          lua_pushstring( L , "Not a valid array index." );
          lua_error( L );
       }

       key = lua_tointeger( L , 2 );

       obj = ( jobject* ) lua_touserdata( L , 1 );

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       method = ( *javaEnv )->GetStaticMethodID( javaEnv , luajava_api_class , "arrayNewIndex" ,
                                                 "(ILjava/lang/Object;I)I" );

       ret = ( *javaEnv )->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , 
                                                *obj , (jint)key );

       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }


       return ret;
    }

    int gc( lua_State * L )
    {
       jobject * pObj;
       JNIEnv * javaEnv;

       if ( !isJavaObject( L , 1 ) )
       {
          return 0;
       }

       pObj = ( jobject * ) lua_touserdata( L , 1 );

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       ( *javaEnv )->DeleteGlobalRef( javaEnv , *pObj );

       return 0;
    }

    int javaBindClass( lua_State * L )
    {
       int top;
       jmethodID method;
       const char * className;
       jstring javaClassName;
       jobject classInstance;
       jthrowable exp;
       JNIEnv * javaEnv;

       top = lua_gettop( L );

       if ( top != 1 )
       {
          luaL_error( L , "Error. Function javaBindClass received %d arguments, expected 1." , top );
       }

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       if ( !lua_isstring( L , 1 ) )
       {
          lua_pushstring( L , "Invalid parameter type. String expected." );
          lua_error( L );
       }
       className = lua_tostring( L , 1 );

       method = ( *javaEnv )->GetStaticMethodID( javaEnv , java_lang_class , "forName" , 
                                                 "(Ljava/lang/String;)Ljava/lang/Class;" );

       javaClassName = ( *javaEnv )->NewStringUTF( javaEnv , className );

       classInstance = ( *javaEnv )->CallStaticObjectMethod( javaEnv , java_lang_class ,
                                                             method , javaClassName );

       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          ( *javaEnv )->DeleteLocalRef( javaEnv , javaClassName );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       ( *javaEnv )->DeleteLocalRef( javaEnv , javaClassName );

       return pushJavaClass( L , classInstance );
    }


    int createProxy( lua_State * L )
    {
      jint ret;
      lua_Number stateIndex;
      const char * impl;
      jmethodID method;
      jthrowable exp;
      jstring str;
      JNIEnv * javaEnv;

      if ( lua_gettop( L ) != 2 )
      {
        lua_pushstring( L , "Error. Function createProxy expects 2 arguments." );
        lua_error( L );
      }

      lua_pushstring( L , LUAJAVASTATEINDEX );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isnumber( L , -1 ) )
       {
          lua_pushstring( L , "Impossible to identify luaState id." );
          lua_error( L );
       }

       stateIndex = lua_tonumber( L , -1 );
       lua_pop( L , 1 );

       if ( !lua_isstring( L , 1 ) || !lua_istable( L , 2 ) )
       {
          lua_pushstring( L , "Invalid Argument types. Expected (string, table)." );
          lua_error( L );
       }

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       method = ( *javaEnv )->GetStaticMethodID( javaEnv , luajava_api_class , "createProxyObject" ,
                                                 "(ILjava/lang/String;)I" );

       impl = lua_tostring( L , 1 );

       str = ( *javaEnv )->NewStringUTF( javaEnv , impl );

       ret = ( *javaEnv )->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , str );
       
       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          ( *javaEnv )->DeleteLocalRef( javaEnv , str );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       ( *javaEnv )->DeleteLocalRef( javaEnv , str );

       return ret;
    }

    int javaNew( lua_State * L )
    {
       int top;
       jint ret;
       jclass clazz;
       jmethodID method;
       jobject classInstance ;
       jthrowable exp;
       jobject * userData;
       lua_Number stateIndex;
       JNIEnv * javaEnv;

       top = lua_gettop( L );

       if ( top == 0 )
       {
          lua_pushstring( L , "Error. Invalid number of parameters." );
          lua_error( L );
       }

       lua_pushstring( L , LUAJAVASTATEINDEX );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isnumber( L , -1 ) )
       {
          lua_pushstring( L , "Impossible to identify luaState id." );
          lua_error( L );
       }

       stateIndex = lua_tonumber( L , -1 );
       lua_pop( L , 1 );

       if ( !isJavaObject( L , 1 ) )
       {
          lua_pushstring( L , "Argument not a valid Java Class." );
          lua_error( L );
       }

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       clazz = ( *javaEnv )->FindClass( javaEnv , "java/lang/Class" );

       userData = ( jobject * ) lua_touserdata( L , 1 );

       classInstance = ( jobject ) *userData;

       if ( ( *javaEnv )->IsInstanceOf( javaEnv , classInstance , clazz ) == JNI_FALSE )
       {
          lua_pushstring( L , "Argument not a valid Java Class." );
          lua_error( L );
       }

       method = ( *javaEnv )->GetStaticMethodID( javaEnv , luajava_api_class , "javaNew" , 
                                                 "(ILjava/lang/Class;)I" );

       if ( clazz == NULL || method == NULL )
       {
          lua_pushstring( L , "Invalid method org.keplerproject.luajava.LuaJavaAPI.javaNew." );
          lua_error( L );
       }

       ret = ( *javaEnv )->CallStaticIntMethod( javaEnv , clazz , method , (jint)stateIndex , classInstance );

       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * str;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          str = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , str );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, str );

          lua_error( L );
       }
      return ret;
    }


    int javaNewInstance( lua_State * L )
    {
       jint ret;
       jmethodID method;
       const char * className;
       jstring javaClassName;
       jthrowable exp;
       lua_Number stateIndex;
       JNIEnv * javaEnv;

       lua_pushstring( L , LUAJAVASTATEINDEX );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isnumber( L , -1 ) )
       {
          lua_pushstring( L , "Impossible to identify luaState id." );
          lua_error( L );
       }

       stateIndex = lua_tonumber( L , -1 );
       lua_pop( L , 1 );

       if ( !lua_isstring( L , 1 ) )
       {
          lua_pushstring( L , "Invalid parameter type. String expected as first parameter." );
          lua_error( L );
       }

       className = lua_tostring( L , 1 );

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       method = ( *javaEnv )->GetStaticMethodID( javaEnv , luajava_api_class , "javaNewInstance" ,
                                                 "(ILjava/lang/String;)I" );

       javaClassName = ( *javaEnv )->NewStringUTF( javaEnv , className );
       
       ret = ( *javaEnv )->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , 
                                                javaClassName );

       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * str;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          ( *javaEnv )->DeleteLocalRef( javaEnv , javaClassName );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          str = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , str );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, str );

          lua_error( L );
       }

       ( *javaEnv )->DeleteLocalRef( javaEnv , javaClassName );

       return ret;
    }

    int javaLoadLib( lua_State * L )
    {
       jint ret;
       int top;
       const char * className, * methodName;
       lua_Number stateIndex;
       jmethodID method;
       jthrowable exp;
       jstring javaClassName , javaMethodName;
       JNIEnv * javaEnv;

       top = lua_gettop( L );

       if ( top != 2 )
       {
          lua_pushstring( L , "Error. Invalid number of parameters." );
          lua_error( L );
       }

       lua_pushstring( L , LUAJAVASTATEINDEX );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isnumber( L , -1 ) )
       {
          lua_pushstring( L , "Impossible to identify luaState id." );
          lua_error( L );
       }

       stateIndex = lua_tonumber( L , -1 );
       lua_pop( L , 1 );


       if ( !lua_isstring( L , 1 ) || !lua_isstring( L , 2 ) )
       {
          lua_pushstring( L , "Invalid parameter. Strings expected." );
          lua_error( L );
       }

       className  = lua_tostring( L , 1 );
       methodName = lua_tostring( L , 2 );

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       method = ( *javaEnv )->GetStaticMethodID( javaEnv , luajava_api_class , "javaLoadLib" ,
                                                 "(ILjava/lang/String;Ljava/lang/String;)I" );

       javaClassName  = ( *javaEnv )->NewStringUTF( javaEnv , className );
       javaMethodName = ( *javaEnv )->NewStringUTF( javaEnv , methodName );
       
       ret = ( *javaEnv )->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , 
                                                javaClassName , javaMethodName );

       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * str;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          ( *javaEnv )->DeleteLocalRef( javaEnv , javaClassName );
          ( *javaEnv )->DeleteLocalRef( javaEnv , javaMethodName );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          str = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , str );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, str );

          lua_error( L );
       }

       ( *javaEnv )->DeleteLocalRef( javaEnv , javaClassName );
       ( *javaEnv )->DeleteLocalRef( javaEnv , javaMethodName );

       return ret;
    }

    int pushJavaClass( lua_State * L , jobject javaObject )
    {
       jobject * userData , globalRef;

       JNIEnv * javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       globalRef = ( *javaEnv )->NewGlobalRef( javaEnv , javaObject );

       userData = ( jobject * ) lua_newuserdata( L , sizeof( jobject ) );
       *userData = globalRef;

       lua_newtable( L );

       lua_pushstring( L , LUAINDEXMETAMETHODTAG );
       lua_pushcfunction( L , &classIndex );
       lua_rawset( L , -3 );

       lua_pushstring( L , LUANEWINDEXMETAMETHODTAG );
       lua_pushcfunction( L , &objectNewIndex );
       lua_rawset( L , -3 );

       lua_pushstring( L , LUAGCMETAMETHODTAG );
       lua_pushcfunction( L , &gc );
       lua_rawset( L , -3 );

       lua_pushstring( L , LUAJAVAOBJECTIND );
       lua_pushboolean( L , 1 );
       lua_rawset( L , -3 );

       if ( lua_setmetatable( L , -2 ) == 0 )
       {
            ( *javaEnv )->DeleteGlobalRef( javaEnv , globalRef );
          lua_pushstring( L , "Cannot create proxy to java class." );
          lua_error( L );
       }

       return 1;
    }

    int pushJavaObject( lua_State * L , jobject javaObject )
    {
       jobject * userData , globalRef;

       JNIEnv * javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       globalRef = ( *javaEnv )->NewGlobalRef( javaEnv , javaObject );

       userData = ( jobject * ) lua_newuserdata( L , sizeof( jobject ) );
       *userData = globalRef;

       lua_newtable( L );

       lua_pushstring( L , LUAINDEXMETAMETHODTAG );
       lua_pushcfunction( L , &objectIndex );
       lua_rawset( L , -3 );

       lua_pushstring( L , LUANEWINDEXMETAMETHODTAG );
       lua_pushcfunction( L , &objectNewIndex );
       lua_rawset( L , -3 );

       lua_pushstring( L , LUAGCMETAMETHODTAG );
       lua_pushcfunction( L , &gc );
       lua_rawset( L , -3 );

       lua_pushstring( L , LUAJAVAOBJECTIND );
       lua_pushboolean( L , 1 );
       lua_rawset( L , -3 );

       if ( lua_setmetatable( L , -2 ) == 0 )
       {
            ( *javaEnv )->DeleteGlobalRef( javaEnv , globalRef );
          lua_pushstring( L , "Cannot create proxy to java object." );
          lua_error( L );
       }

       return 1;
    }

    int pushJavaArray( lua_State * L , jobject javaObject )
    {
       jobject * userData , globalRef;

       JNIEnv * javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       globalRef = ( *javaEnv )->NewGlobalRef( javaEnv , javaObject );

       userData = ( jobject * ) lua_newuserdata( L , sizeof( jobject ) );
       *userData = globalRef;

       lua_newtable( L );

       lua_pushstring( L , LUAINDEXMETAMETHODTAG );
       lua_pushcfunction( L , &arrayIndex );
       lua_rawset( L , -3 );

       lua_pushstring( L , LUANEWINDEXMETAMETHODTAG );
       lua_pushcfunction( L , &arrayNewIndex );
       lua_rawset( L , -3 );

       lua_pushstring( L , LUAGCMETAMETHODTAG );
       lua_pushcfunction( L , &gc );
       lua_rawset( L , -3 );

       lua_pushstring( L , LUAJAVAOBJECTIND );
       lua_pushboolean( L , 1 );
       lua_rawset( L , -3 );

       if ( lua_setmetatable( L , -2 ) == 0 )
       {
            ( *javaEnv )->DeleteGlobalRef( javaEnv , globalRef );
          lua_pushstring( L , "Cannot create proxy to java object." );
          lua_error( L );
       }

       return 1;
    }

    int isJavaObject( lua_State * L , int idx )
    {
       if ( !lua_isuserdata( L , idx ) )
          return 0;

       if ( lua_getmetatable( L , idx ) == 0 )
          return 0;

       lua_pushstring( L , LUAJAVAOBJECTIND );
       lua_rawget( L , -2 );

       if (lua_isnil( L, -1 ))
       {
          lua_pop( L , 2 );
          return 0;
       }
       lua_pop( L , 2 );
       return 1;
    }

    lua_State * getStateFromCPtr( JNIEnv * env , jobject cptr )
    {
       lua_State * L;

       jclass classPtr       = ( *env )->GetObjectClass( env , cptr );
       jfieldID CPtr_peer_ID = ( *env )->GetFieldID( env , classPtr , "peer" , "J" );
       jbyte * peer          = ( jbyte * ) ( *env )->GetLongField( env , cptr , CPtr_peer_ID );

       L = ( lua_State * ) peer;

       pushJNIEnv( env ,  L );

       return L;
    }

    int luaJavaFunctionCall( lua_State * L )
    {
       jobject * obj;
       jthrowable exp;
       int ret;
       JNIEnv * javaEnv;
       
       if ( !isJavaObject( L , 1 ) )
       {
          lua_pushstring( L , "Not a java Function." );
          lua_error( L );
       }

       obj = lua_touserdata( L , 1 );

       javaEnv = getEnvFromState( L );
       if ( javaEnv == NULL )
       {
          lua_pushstring( L , "Invalid JNI Environment." );
          lua_error( L );
       }

       if ( ( *javaEnv )->IsInstanceOf( javaEnv , *obj , java_function_class ) ==
            JNI_FALSE )
       {
          fprintf( stderr , "Called Java object is not a JavaFunction\n");
          return 0;
       }

       ret = ( *javaEnv )->CallIntMethod( javaEnv , *obj , java_function_method );

       exp = ( *javaEnv )->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * str;
          
          ( *javaEnv )->ExceptionClear( javaEnv );
          jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , get_message_method );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = ( *javaEnv )->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = ( *javaEnv )->CallObjectMethod( javaEnv , exp , methodId );
          }

          str = ( *javaEnv )->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , str );

          ( *javaEnv )->ReleaseStringUTFChars( javaEnv , jstr, str );

          lua_error( L );
       }
       return ret;
    }

    JNIEnv * getEnvFromState( lua_State * L )
    {
       JNIEnv ** udEnv;

       lua_pushstring( L , LUAJAVAJNIENVTAG );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isuserdata( L , -1 ) )
       {
          lua_pop( L , 1 );
          return NULL;
       }

       udEnv = ( JNIEnv ** ) lua_touserdata( L , -1 );

       lua_pop( L , 1 );

       return * udEnv;
    }

    void pushJNIEnv( JNIEnv * env , lua_State * L )
    {
       JNIEnv ** udEnv;

       lua_pushstring( L , LUAJAVAJNIENVTAG );
       lua_rawget( L , LUA_REGISTRYINDEX );

       if ( !lua_isnil( L , -1 ) )
       {
          udEnv = ( JNIEnv ** ) lua_touserdata( L , -1 );
          *udEnv = env;
          lua_pop( L , 1 );
       }
       else
       {
          lua_pop( L , 1 );
          udEnv = ( JNIEnv ** ) lua_newuserdata( L , sizeof( JNIEnv * ) );
          *udEnv = env;

          lua_pushstring( L , LUAJAVAJNIENVTAG );
          lua_insert( L , -2 );
          lua_rawset( L , LUA_REGISTRYINDEX );
       }
    }
     */

    private static native CPtr jniOpen(int stateId); /*
        lua_State * L = luaL_newstate();
        lua_pushstring( L , LUAJAVASTATEINDEX );
        lua_pushnumber( L , (lua_Number)stateId );
        lua_settable( L , LUA_REGISTRYINDEX );

        jobject obj;
        jclass tempClass;

        tempClass = ( *env )->FindClass( env , "io/nondev/nonlua/CPtr" );
            
        obj = ( *env )->AllocObject( env , tempClass );
        if ( obj )
        {
            ( *env )->SetLongField( env , obj , ( *env )->GetFieldID( env , tempClass , "peer", "J" ) , ( jlong ) L );
        }

        if ( luajava_api_class == NULL )
        {
            tempClass = ( *env )->FindClass( env , "io/nondev/nonlua/LuaJava" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Could not find LuaJava class\n" );
                exit( 1 );
            }

            if ( ( luajava_api_class = ( *env )->NewGlobalRef( env , tempClass ) ) == NULL )
            {
                fprintf( stderr , "Could not bind to LuaJavaAPI class\n" );
                exit( 1 );
            }
        }

        if ( java_function_class == NULL )
        {
            tempClass = ( *env )->FindClass( env , "io/nondev/nonlua/LuaFunction" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Could not find LuaFunction interface\n" );
                exit( 1 );
            }

            if ( ( java_function_class = ( *env )->NewGlobalRef( env , tempClass ) ) == NULL )
            {
                fprintf( stderr , "Could not bind to LuaFunction interface\n" );
                exit( 1 );
            }
        }

        if ( java_function_method == NULL )
        {
            java_function_method = ( *env )->GetMethodID( env , java_function_class , "call" , "()I");
            if ( !java_function_method )
            {
                fprintf( stderr , "Could not find <call> method in LuaFunction\n" );
                exit( 1 );
            }
        }

        if ( throwable_class == NULL )
        {
            tempClass = ( *env )->FindClass( env , "java/lang/Throwable" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Error. Couldn't bind java class java.lang.Throwable\n" );
                exit( 1 );
            }

            throwable_class = ( *env )->NewGlobalRef( env , tempClass );

            if ( throwable_class == NULL )
            {
                fprintf( stderr , "Error. Couldn't bind java class java.lang.Throwable\n" );
                exit( 1 );
            }
        }

        if ( get_message_method == NULL )
        {
            get_message_method = ( *env )->GetMethodID( env , throwable_class , "getMessage" ,
                                                        "()Ljava/lang/String;" );

            if ( get_message_method == NULL )
            {
                fprintf(stderr, "Could not find <getMessage> method in java.lang.Throwable\n");
                exit(1);
            }
        }

        if ( java_lang_class == NULL )
        {
            tempClass = ( *env )->FindClass( env , "java/lang/Class" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Error. Coundn't bind java class java.lang.Class\n" );
                exit( 1 );
            }

            java_lang_class = ( *env )->NewGlobalRef( env , tempClass );

            if ( java_lang_class == NULL )
            {
                fprintf( stderr , "Error. Couldn't bind java class java.lang.Throwable\n" );
                exit( 1 );
            }
        }

        pushJNIEnv( env , L );
        
        return obj;
    */

    private static native void jniOpenJava(CPtr cptr); /*
        lua_State* L = getStateFromCPtr( env , cptr );
        
        lua_newtable( L );
        lua_setglobal( L , "java" );
        lua_getglobal( L , "java" );
        
        lua_pushstring( L , "bindClass" );
        lua_pushcfunction( L , &javaBindClass );
        lua_settable( L , -3 );

        lua_pushstring( L , "new" );
        lua_pushcfunction( L , &javaNew );
        lua_settable( L , -3 );

        lua_pushstring( L , "newInstance" );
        lua_pushcfunction( L , &javaNewInstance );
        lua_settable( L , -3 );

        lua_pushstring( L , "loadLib" );
        lua_pushcfunction( L , &javaLoadLib );
        lua_settable( L , -3 );

        lua_pushstring( L , "createProxy" );
        lua_pushcfunction( L , &createProxy );
        lua_settable( L , -3 );

        lua_pop( L , 1 );
    */

    final private static String LIB = "nonlua";
  
    final public static int GLOBALS  = -10002;
    final public static int REGISTRY = -10000;

    final public static int NONE          = -1;
    final public static int NIL           = 0;
    final public static int BOOLEAN       = 1;
    final public static int LIGHTUSERDATA = 2;
    final public static int NUMBER        = 3;
    final public static int STRING        = 4;
    final public static int TABLE         = 5;
    final public static int FUNCTION      = 6;
    final public static int USERDATA      = 7;
    final public static int THREAD        = 8;
    
    final public static int MULTRET         = -1;
    final public static int YIELD           = 1;
    final public static int RUNTIME_ERROR   = 2;
    final public static int SYNTAX_ERROR    = 3;
    final public static int MEMORY_ERROR    = 4;
    final public static int HANDLER_ERROR   = 5;
    
    final public static int GC_STOP       = 0;
    final public static int GC_RESTART    = 1;
    final public static int GC_COLLECT    = 2;
    final public static int GC_COUNT      = 3;
    final public static int GC_COUNTB     = 4;
    final public static int GC_STEP       = 5;
    final public static int GC_SETPAUSE   = 6;
    final public static int GC_SETSTEPMUL = 7;

    private static LuaLoader loader;
    private static LuaLogger logger;

    static {
        new JniGenSharedLibraryLoader().load(LIB);

        loader = new LuaLoader() {
            public String path() {
                return "";
            }
        };

        logger = new LuaLogger() {
            public void log(String msg) {
                System.out.print(msg);
            }
        };
    }

    public static void setLoader(LuaLoader loader) {
        Lua.loader = loader;
    }

    public static void setLogger(LuaLogger logger) {
        Lua.logger = logger;
    }

    protected CPtr state;
    protected final int stateId;
    
    public Lua() {
        stateId = LuaFactory.insert(this);
        state = jniOpen(stateId);

        jniOpenJava(state);
        //jniOpenLibs();

        pushFunction(new LuaFunction(this) {
            public int call() {
                for (int i = 2; i <= L.getTop(); i++) {
                    int type = L.type(i);
                    String stype = L.typeName(type);
                    String val = null;
                    if (stype.equals("userdata")) {
                        Object obj = L.toObject(i); 
                        if (obj != null) val = obj.toString();
                    } else if (stype.equals("boolean")) {   
                        val = L.toBoolean(i) ? "true" : "false";
                    } else {
                        val = L.toString(i);
                    }

                    if (val == null) val = stype;
                    logger.log(val);
                    logger.log("\t");
                }

                logger.log("\n");
                return 0;
            }
        });

        setGlobal("print");

        getGlobal("package");
        getField(-1, "loaders");
        int nLoaders = len(-1);
        
        pushFunction(new LuaFunction(this) {
            public int call() {
                String name = L.toString(-1);

                if (L.load(name + ".lua") == -1)
                    L.pushString("Cannot load module " + name); 

                return 1;
            }
        });

        setI(-2, nLoaders + 1);
        pop(1);
        getField(-1, "path");
        pushString(";" + loader.path() + "/?.lua");
        concat(2);
        setField(-2, "path");
        pop(1);
    }

    public long getCPtrPeer() {
        return (state != null)? state.getPeer() : 0;
    }
    
    public void dispose() {
    }
    
    private InputStream getFile(String path) throws IOException {
        File file = new File(loader.path(), path);

        if (file.exists()) {
            return new FileInputStream(file);
        }
        
        return Lua.class.getResourceAsStream("/" + file.getPath().replace('\\', '/'));
    }

    private String readFile(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        
        return out.toString();
    }
    
    public int run(String chunk) {
        if (chunk.endsWith(".lua")) {
            //try {
                //state.LloadBuffer(readFile(getFile(chunk)).getBytes(), chunk);
                //return pcall(0, MULTIPLE_RETURN, 0);
                return 0;
            //} catch (IOException e) {
                //return -1;
            //}
        }
        
        return 0; //state.LdoString(chunk);
    }
    
    public int load(String chunk) {
        if (chunk.endsWith(".lua")) {
            //try {
                return 0; //state.LloadBuffer(readFile(getFile(chunk)).getBytes(), chunk);
            //} catch (IOException e) {
            //    return -1;
            //}
        }
        
        return 0; //state.LloadString(chunk);
    }

    public Lua newThread() {
        return null;
    }

    public void get(int idx) {
    }

    public void set(int idx) {
    }

    public int getTop() {
        return 0;
    }

    public void setTop(int idx) {
    }

    public void getI(int idx, int n) {
    }

    public void setI(int idx, int n) {
    }

    public void pushValue(int idx) {
    }
    
    public void remove(int idx) {
    }
    
    public void insert(int idx) {
    }
    
    public void replace(int idx) {
    }
    
    public int checkStack(int sz) {
        return 0;
    }

    public void move(Lua to, int n) {
    }
    
    public boolean isNumber(int idx) {
        return false;
    }

    public boolean isString(int idx) {
        return false;
    }

    public boolean isFunction(int idx) {
        return false;
    }

    public boolean isUserdata(int idx) {
        return false;
    }

    public boolean isTable(int idx) {
        return false;
    }

    public boolean isBoolean(int idx) {
        return false;
    }

    public boolean exists(int idx) {
        return false;
    }
    
    public boolean isNil(int idx) {
        return false;
    }
    
    public boolean isThread(int idx) {
        return false;
    }
    
    public boolean isNone(int idx) {
        return false;
    }

    public boolean isObject(int idx) {
        return false;
    }

    public int type(int idx) {
        return 0;
    }

    public String typeName(int tp) {
        return "";
    }

    public int compare(int idx1, int idx2, int op) {
        return 0;
    }

    public int len(int idx) {
        return 0;
    }

    public double toNumber(int idx) {
        return 0.0;
    }

    public int toInteger(int idx) {
        return 0;
    }
    
    public boolean toBoolean(int idx) {
        return false;
    }

    public String toString(int idx) {
        return "";
    }

    public Lua toThread(int idx) {
        return null;
    }

    public Object toObject(int idx) {
        return null;
    }

    public void pushNil() {
    }

    public void pushNumber(double db) {
    }
    
    public void pushInteger(int integer) {
    }

    public void pushString(String str) {
    }

    public void pushString(byte[] bytes) {
    }
    
    public void pushBoolean(boolean bool) {
    }

    public void pushObject(Object obj) {
    }

    public void pushFunction(LuaFunction func) {
    }

    public void getTable(int idx) {
    }

    public int getMetaTable(int idx) {
        return 0;
    }

    public void getMetaTable(String tName) {
    }
    
    public void getField(int idx, String k) {
    }

    public int getMetaField(int obj, String e) {
        return 0;
    }

    public LuaObject getObject(String globalName) {
        return null;
    }
    
    public LuaObject getObject(LuaObject parent, String name) {
        return null;
    }
    
    public LuaObject getObject(LuaObject parent, Number name) {
        return null;
    }
    
    public LuaObject getObject(LuaObject parent, LuaObject name) {
        return null;
    }

    public LuaObject getObject(int index) {
        return null;
    }

    public Object getObjectFromUserdata(int idx) {
        return null;
    }
    
    public void createTable(int narr, int nrec) {
    }

    public void newTable() {
    }

    public int newMetaTable(String tName) {
        return 0;
    }

    public void setTable(int idx) {
    }
    
    public void setField(int idx, String k) {
    }

    public int setMetaTable(int idx) {
        return 0;
    }

    public void call(int nArgs, int nResults) {
    }

    public int callMeta(int obj, String e) {
        return 0;
    }

    public int pcall(int nArgs, int nResults, int errFunc) {
        return 0;
    }

    public int yield(int nResults) {
        return 0;
    }

    public int resume(int nArgs) {
        return 0;
    }
    
    public int status() {
        return 0;
    }
    
    public int gc(int what, int data) {
        return 0;
    }
    
    public int next(int idx) {
        return 0;
    }

    public int error() {
        return 0;
    }

    public void concat(int n) {
    }
    
    public int argError(int numArg, String extraMsg) {
        return 0;
    }
    
    public String checkString(int numArg) {
        return "";
    }
    
    public String optString(int numArg, String def) {
        return "";
    }
    
    public double checkNumber(int numArg) {
        return 0.0;
    }
    
    public double optNumber(int numArg, double def) {
        return 0.0;
    }
    
    public int checkInteger(int numArg) {
        return 0;
    }
    
    public int optInteger(int numArg, int def) {
        return 0;
    }
    
    public void checkStack(int sz, String msg) {
    }
    
    public void checkType(int nArg, int t) {
    }
    
    public void checkAny(int nArg) {
    }
    
    public void where(int lvl) {
    }
    
    public int ref(int t) {
        return 0;
    }
    
    public void unRef(int t, int ref) {
    }
    
    public String gsub(String s, String p, String r) {
        return "";
    }
    
    public void pop(int n)  {
    }

    public void getGlobal(String global) {
    }

    public void setGlobal(String name) {
    }
}