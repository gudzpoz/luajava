#include <io.nondev.nonlua.Lua.h>

//@line:30

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

    static int objectIndex( lua_State * L );
    static int objectIndexReturn( lua_State * L );
    static int objectNewIndex( lua_State * L );
    static int classIndex( lua_State * L );
    static int arrayIndex( lua_State * L );
    static int arrayNewIndex( lua_State * L );
    static int gc( lua_State * L );
    static int javaBindClass( lua_State * L );
    static int createProxy( lua_State * L );
    static int javaNew( lua_State * L );
    static int javaNewInstance( lua_State * L );
    static int javaLoadLib( lua_State * L );
    static int pushJavaObject( lua_State * L , jobject javaObject );
    static int pushJavaArray( lua_State * L , jobject javaObject );
    static int pushJavaClass( lua_State * L , jobject javaObject );
    static int isJavaObject( lua_State * L , int idx );
    static lua_State * getStateFromCPtr( JNIEnv * env , jobject cptr );
    static int luaJavaFunctionCall( lua_State * L );
    static void pushJNIEnv( JNIEnv * env , lua_State * L );
    static JNIEnv * getEnvFromState( lua_State * L );

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

       method = javaEnv->GetStaticMethodID( javaEnv , luajava_api_class , "checkField" ,
                                                 "(ILjava/lang/Object;Ljava/lang/String;)I" );

       str = javaEnv->NewStringUTF( javaEnv , key );

       checkField = javaEnv->CallStaticIntMethod( javaEnv , luajava_api_class , method ,
                                                       (jint)stateIndex , *obj , str );

       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          javaEnv->DeleteLocalRef( javaEnv , str );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       javaEnv->DeleteLocalRef( javaEnv , str );

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


       method = javaEnv->GetStaticMethodID( javaEnv , luajava_api_class , "objectIndex" ,
                                                 "(ILjava/lang/Object;Ljava/lang/String;)I" );

       str = javaEnv->NewStringUTF( javaEnv , methodName );

       ret = javaEnv->CallStaticIntMethod( javaEnv , luajava_api_class , method , (jint)stateIndex , 
                                                *pObject , str );

       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          javaEnv->DeleteLocalRef( javaEnv , str );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       javaEnv->DeleteLocalRef( javaEnv , str );

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

       method = javaEnv->GetStaticMethodID( javaEnv , luajava_api_class , "objectNewIndex" ,
                                                 "(ILjava/lang/Object;Ljava/lang/String;)I" );

       str = javaEnv->NewStringUTF( javaEnv , fieldName );

       ret = javaEnv->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , 
                                                *obj , str );

       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          javaEnv->DeleteLocalRef( javaEnv , str );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       javaEnv->DeleteLocalRef( javaEnv , str );


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

       method = javaEnv->GetStaticMethodID( javaEnv , luajava_api_class , "classIndex" ,
                                                 "(ILjava/lang/Class;Ljava/lang/String;)I" );

       str = javaEnv->NewStringUTF( javaEnv , fieldName );

       ret = javaEnv->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , 
                                                *obj , str );

       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          javaEnv->DeleteLocalRef( javaEnv , str );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       javaEnv->DeleteLocalRef( javaEnv , str );

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

       method = javaEnv->GetStaticMethodID( javaEnv , luajava_api_class , "arrayIndex" ,
                                                 "(ILjava/lang/Object;I)I" );

       ret = javaEnv->CallStaticIntMethod( javaEnv , luajava_api_class , method ,
                                                       (jint)stateIndex , *obj , (jlong)key );

       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, cStr );

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

       method = javaEnv->GetStaticMethodID( javaEnv , luajava_api_class , "arrayNewIndex" ,
                                                 "(ILjava/lang/Object;I)I" );

       ret = javaEnv->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , 
                                                *obj , (jint)key );

       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, cStr );

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

       javaEnv->DeleteGlobalRef( javaEnv , *pObj );

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

       method = javaEnv->GetStaticMethodID( javaEnv , java_lang_class , "forName" , 
                                                 "(Ljava/lang/String;)Ljava/lang/Class;" );

       javaClassName = javaEnv->NewStringUTF( javaEnv , className );

       classInstance = javaEnv->CallStaticObjectMethod( javaEnv , java_lang_class ,
                                                             method , javaClassName );

       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          javaEnv->DeleteLocalRef( javaEnv , javaClassName );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       javaEnv->DeleteLocalRef( javaEnv , javaClassName );

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

       method = javaEnv->GetStaticMethodID( javaEnv , luajava_api_class , "createProxyObject" ,
                                                 "(ILjava/lang/String;)I" );

       impl = lua_tostring( L , 1 );

       str = javaEnv->NewStringUTF( javaEnv , impl );

       ret = javaEnv->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , str );
       
       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * cStr;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          javaEnv->DeleteLocalRef( javaEnv , str );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          cStr = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , cStr );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, cStr );

          lua_error( L );
       }

       javaEnv->DeleteLocalRef( javaEnv , str );

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

       clazz = javaEnv->FindClass( javaEnv , "java/lang/Class" );

       userData = ( jobject * ) lua_touserdata( L , 1 );

       classInstance = ( jobject ) *userData;

       if ( javaEnv->IsInstanceOf( javaEnv , classInstance , clazz ) == JNI_FALSE )
       {
          lua_pushstring( L , "Argument not a valid Java Class." );
          lua_error( L );
       }

       method = javaEnv->GetStaticMethodID( javaEnv , luajava_api_class , "javaNew" , 
                                                 "(ILjava/lang/Class;)I" );

       if ( clazz == NULL || method == NULL )
       {
          lua_pushstring( L , "Invalid method org.keplerproject.luajava.LuaJavaAPI.javaNew." );
          lua_error( L );
       }

       ret = javaEnv->CallStaticIntMethod( javaEnv , clazz , method , (jint)stateIndex , classInstance );

       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * str;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          str = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , str );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, str );

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

       method = javaEnv->GetStaticMethodID( javaEnv , luajava_api_class , "javaNewInstance" ,
                                                 "(ILjava/lang/String;)I" );

       javaClassName = javaEnv->NewStringUTF( javaEnv , className );
       
       ret = javaEnv->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , 
                                                javaClassName );

       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * str;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          javaEnv->DeleteLocalRef( javaEnv , javaClassName );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          str = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , str );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, str );

          lua_error( L );
       }

       javaEnv->DeleteLocalRef( javaEnv , javaClassName );

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

       method = javaEnv->GetStaticMethodID( javaEnv , luajava_api_class , "javaLoadLib" ,
                                                 "(ILjava/lang/String;Ljava/lang/String;)I" );

       javaClassName  = javaEnv->NewStringUTF( javaEnv , className );
       javaMethodName = javaEnv->NewStringUTF( javaEnv , methodName );
       
       ret = javaEnv->CallStaticIntMethod( javaEnv , luajava_api_class , method, (jint)stateIndex , 
                                                javaClassName , javaMethodName );

       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * str;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          javaEnv->DeleteLocalRef( javaEnv , javaClassName );
          javaEnv->DeleteLocalRef( javaEnv , javaMethodName );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          str = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , str );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, str );

          lua_error( L );
       }

       javaEnv->DeleteLocalRef( javaEnv , javaClassName );
       javaEnv->DeleteLocalRef( javaEnv , javaMethodName );

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

       globalRef = javaEnv->NewGlobalRef( javaEnv , javaObject );

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
            javaEnv->DeleteGlobalRef( javaEnv , globalRef );
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

       globalRef = javaEnv->NewGlobalRef( javaEnv , javaObject );

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
            javaEnv->DeleteGlobalRef( javaEnv , globalRef );
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

       globalRef = javaEnv->NewGlobalRef( javaEnv , javaObject );

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
            javaEnv->DeleteGlobalRef( javaEnv , globalRef );
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

       jclass classPtr       = env->GetObjectClass( env , cptr );
       jfieldID CPtr_peer_ID = env->GetFieldID( env , classPtr , "peer" , "J" );
       jbyte * peer          = ( jbyte * ) env->GetLongField( env , cptr , CPtr_peer_ID );

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

       if ( javaEnv->IsInstanceOf( javaEnv , *obj , java_function_class ) ==
            JNI_FALSE )
       {
          fprintf( stderr , "Called Java object is not a JavaFunction\n");
          return 0;
       }

       ret = javaEnv->CallIntMethod( javaEnv , *obj , java_function_method );

       exp = javaEnv->ExceptionOccurred( javaEnv );

       if ( exp != NULL )
       {
          jobject jstr;
          const char * str;
          
          javaEnv->ExceptionClear( javaEnv );
          jstr = javaEnv->CallObjectMethod( javaEnv , exp , get_message_method );

          if ( jstr == NULL )
          {
             jmethodID methodId;

             methodId = javaEnv->GetMethodID( javaEnv , throwable_class , "toString" , "()Ljava/lang/String;" );
             jstr = javaEnv->CallObjectMethod( javaEnv , exp , methodId );
          }

          str = javaEnv->GetStringUTFChars( javaEnv , jstr , NULL );

          lua_pushstring( L , str );

          javaEnv->ReleaseStringUTFChars( javaEnv , jstr, str );

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
     JNIEXPORT jobject JNICALL Java_io_nondev_nonlua_Lua_jniOpen(JNIEnv* env, jclass clazz, jint stateId) {


//@line:1371

        lua_State * L = luaL_newstate();
        lua_pushstring( L , LUAJAVASTATEINDEX );
        lua_pushnumber( L , (lua_Number)stateId );
        lua_settable( L , LUA_REGISTRYINDEX );

        jobject obj;
        jclass tempClass;

        tempClass = env->FindClass( env , "io/nondev/nonlua/CPtr" );
            
        obj = env->AllocObject( env , tempClass );
        if ( obj )
        {
            env->SetLongField( env , obj , env->GetFieldID( env , tempClass , "peer", "J" ) , ( jlong ) L );
        }

        if ( luajava_api_class == NULL )
        {
            tempClass = env->FindClass( env , "io/nondev/nonlua/LuaJava" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Could not find LuaJava class\n" );
                exit( 1 );
            }

            if ( ( luajava_api_class = env->NewGlobalRef( env , tempClass ) ) == NULL )
            {
                fprintf( stderr , "Could not bind to LuaJavaAPI class\n" );
                exit( 1 );
            }
        }

        if ( java_function_class == NULL )
        {
            tempClass = env->FindClass( env , "io/nondev/nonlua/LuaFunction" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Could not find LuaFunction interface\n" );
                exit( 1 );
            }

            if ( ( java_function_class = env->NewGlobalRef( env , tempClass ) ) == NULL )
            {
                fprintf( stderr , "Could not bind to LuaFunction interface\n" );
                exit( 1 );
            }
        }

        if ( java_function_method == NULL )
        {
            java_function_method = env->GetMethodID( env , java_function_class , "call" , "()I");
            if ( !java_function_method )
            {
                fprintf( stderr , "Could not find <call> method in LuaFunction\n" );
                exit( 1 );
            }
        }

        if ( throwable_class == NULL )
        {
            tempClass = env->FindClass( env , "java/lang/Throwable" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Error. Couldn't bind java class java.lang.Throwable\n" );
                exit( 1 );
            }

            throwable_class = env->NewGlobalRef( env , tempClass );

            if ( throwable_class == NULL )
            {
                fprintf( stderr , "Error. Couldn't bind java class java.lang.Throwable\n" );
                exit( 1 );
            }
        }

        if ( get_message_method == NULL )
        {
            get_message_method = env->GetMethodID( env , throwable_class , "getMessage" ,
                                                        "()Ljava/lang/String;" );

            if ( get_message_method == NULL )
            {
                fprintf(stderr, "Could not find <getMessage> method in java.lang.Throwable\n");
                exit(1);
            }
        }

        if ( java_lang_class == NULL )
        {
            tempClass = env->FindClass( env , "java/lang/Class" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Error. Coundn't bind java class java.lang.Class\n" );
                exit( 1 );
            }

            java_lang_class = env->NewGlobalRef( env , tempClass );

            if ( java_lang_class == NULL )
            {
                fprintf( stderr , "Error. Couldn't bind java class java.lang.Throwable\n" );
                exit( 1 );
            }
        }

        pushJNIEnv( env , L );
        
        return obj;
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenJava(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:1487

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
    

}

