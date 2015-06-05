#include <nonlua.h>

jclass throwable_class = NULL;
jmethodID get_message_method = NULL;
jclass java_function_class = NULL;
jmethodID java_function_method = NULL;
jclass luajava_api_class = NULL;
jclass java_lang_class = NULL;

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

   method = javaEnv->GetStaticMethodID( luajava_api_class , "checkField" ,
                                             "(ILjava/lang/Object;Ljava/lang/String;)I" );

   str = javaEnv->NewStringUTF( key );

   checkField = javaEnv->CallStaticIntMethod( luajava_api_class , method ,
                                                   (jint)stateIndex , *obj , str );

   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * cStr;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      javaEnv->DeleteLocalRef( str );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      cStr = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , cStr );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, cStr );

      lua_error( L );
   }

   javaEnv->DeleteLocalRef( str );

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


   method = javaEnv->GetStaticMethodID( luajava_api_class , "objectIndex" ,
                                             "(ILjava/lang/Object;Ljava/lang/String;)I" );

   str = javaEnv->NewStringUTF( methodName );

   ret = javaEnv->CallStaticIntMethod( luajava_api_class , method , (jint)stateIndex , 
                                            *pObject , str );

   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * cStr;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      javaEnv->DeleteLocalRef( str );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      cStr = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , cStr );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, cStr );

      lua_error( L );
   }

   javaEnv->DeleteLocalRef( str );

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

   method = javaEnv->GetStaticMethodID( luajava_api_class , "objectNewIndex" ,
                                             "(ILjava/lang/Object;Ljava/lang/String;)I" );

   str = javaEnv->NewStringUTF( fieldName );

   ret = javaEnv->CallStaticIntMethod( luajava_api_class , method, (jint)stateIndex , 
                                            *obj , str );

   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * cStr;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      javaEnv->DeleteLocalRef( str );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      cStr = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , cStr );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, cStr );

      lua_error( L );
   }

   javaEnv->DeleteLocalRef( str );


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

   method = javaEnv->GetStaticMethodID( luajava_api_class , "classIndex" ,
                                             "(ILjava/lang/Class;Ljava/lang/String;)I" );

   str = javaEnv->NewStringUTF( fieldName );

   ret = javaEnv->CallStaticIntMethod( luajava_api_class , method, (jint)stateIndex , 
                                            *obj , str );

   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * cStr;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      javaEnv->DeleteLocalRef( str );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      cStr = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , cStr );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, cStr );

      lua_error( L );
   }

   javaEnv->DeleteLocalRef( str );

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

   method = javaEnv->GetStaticMethodID( luajava_api_class , "arrayIndex" ,
                                             "(ILjava/lang/Object;I)I" );

   ret = javaEnv->CallStaticIntMethod( luajava_api_class , method ,
                                                   (jint)stateIndex , *obj , (jlong)key );

   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * cStr;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      cStr = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , cStr );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, cStr );

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

   method = javaEnv->GetStaticMethodID( luajava_api_class , "arrayNewIndex" ,
                                             "(ILjava/lang/Object;I)I" );

   ret = javaEnv->CallStaticIntMethod( luajava_api_class , method, (jint)stateIndex , 
                                            *obj , (jint)key );

   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * cStr;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      cStr = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , cStr );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, cStr );

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

   javaEnv->DeleteGlobalRef( *pObj );

   return 0;
}

int javaRequire( lua_State * L )
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

   method = javaEnv->GetStaticMethodID( java_lang_class , "forName" , 
                                             "(Ljava/lang/String;)Ljava/lang/Class;" );

   javaClassName = javaEnv->NewStringUTF( className );

   classInstance = javaEnv->CallStaticObjectMethod( java_lang_class ,
                                                         method , javaClassName );

   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * cStr;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      javaEnv->DeleteLocalRef( javaClassName );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      cStr = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , cStr );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, cStr );

      lua_error( L );
   }

   javaEnv->DeleteLocalRef( javaClassName );

   return pushJavaClass( L , classInstance );
}

int javaInstanceOf( lua_State * L )
{
   jobject classInstance;
   jobject classInstance2;
   jobject * userData;
   jobject * userData2;
   JNIEnv * javaEnv;

   if ( lua_gettop( L ) < 2 )
   {
      lua_pushstring( L , "Error. Invalid number of parameters." );
      lua_error( L );
   }

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

   userData = ( jobject * ) lua_touserdata( L , 1 );

   classInstance = ( jobject ) *userData;

   if ( lua_isstring( L , 2 ) )
   {
      classInstance2 = javaEnv->FindClass( lua_tostring( L , 2 ) );
   }
   else if ( isJavaObject ( L , 2 ) )
   {
      userData2 = ( jobject * ) lua_touserdata( L , 2 );
      classInstance2 = ( jobject ) *userData2;
   }

   lua_pushboolean ( L , ( int ) javaEnv->IsInstanceOf( classInstance , ( jclass ) classInstance2 ) );
   return 1;
}

int javaProxy( lua_State * L )
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
     lua_pushstring( L , "Error. Function proxy expects 2 arguments." );
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

   method = javaEnv->GetStaticMethodID( luajava_api_class , "createProxyObject" ,
                                             "(ILjava/lang/String;)I" );

   impl = lua_tostring( L , 1 );

   str = javaEnv->NewStringUTF( impl );

   ret = javaEnv->CallStaticIntMethod( luajava_api_class , method, (jint)stateIndex , str );
   
   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * cStr;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      javaEnv->DeleteLocalRef( str );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      cStr = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , cStr );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, cStr );

      lua_error( L );
   }

   javaEnv->DeleteLocalRef( str );

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

   clazz = javaEnv->FindClass( "java/lang/Class" );

   userData = ( jobject * ) lua_touserdata( L , 1 );

   classInstance = ( jobject ) *userData;

   if ( javaEnv->IsInstanceOf( classInstance , clazz ) == JNI_FALSE )
   {
      lua_pushstring( L , "Argument not a valid Java Class." );
      lua_error( L );
   }

   method = javaEnv->GetStaticMethodID( luajava_api_class , "javaNew" , 
                                             "(ILjava/lang/Class;)I" );

   if ( clazz == NULL || method == NULL )
   {
      lua_pushstring( L , "Invalid method org.keplerproject.luajava.LuaJavaAPI.javaNew." );
      lua_error( L );
   }

   ret = javaEnv->CallStaticIntMethod( clazz , method , (jint)stateIndex , classInstance );

   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * str;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      str = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , str );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, str );

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

   method = javaEnv->GetStaticMethodID( luajava_api_class , "javaNewInstance" ,
                                             "(ILjava/lang/String;)I" );

   javaClassName = javaEnv->NewStringUTF( className );
   
   ret = javaEnv->CallStaticIntMethod( luajava_api_class , method, (jint)stateIndex , 
                                            javaClassName );

   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * str;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      javaEnv->DeleteLocalRef( javaClassName );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      str = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , str );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, str );

      lua_error( L );
   }

   javaEnv->DeleteLocalRef( javaClassName );

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

   method = javaEnv->GetStaticMethodID( luajava_api_class , "javaLoadLib" ,
                                             "(ILjava/lang/String;Ljava/lang/String;)I" );

   javaClassName  = javaEnv->NewStringUTF( className );
   javaMethodName = javaEnv->NewStringUTF( methodName );
   
   ret = javaEnv->CallStaticIntMethod( luajava_api_class , method, (jint)stateIndex , 
                                            javaClassName , javaMethodName );

   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * str;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      javaEnv->DeleteLocalRef( javaClassName );
      javaEnv->DeleteLocalRef( javaMethodName );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      str = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , str );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, str );

      lua_error( L );
   }

   javaEnv->DeleteLocalRef( javaClassName );
   javaEnv->DeleteLocalRef( javaMethodName );

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

   globalRef = javaEnv->NewGlobalRef( javaObject );

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
        javaEnv->DeleteGlobalRef( globalRef );
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

   globalRef = javaEnv->NewGlobalRef( javaObject );

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
        javaEnv->DeleteGlobalRef( globalRef );
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

   globalRef = javaEnv->NewGlobalRef( javaObject );

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
        javaEnv->DeleteGlobalRef( globalRef );
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

   jclass classPtr       = env->GetObjectClass( cptr );
   jfieldID CPtr_peer_ID = env->GetFieldID( classPtr , "peer" , "J" );
   jbyte * peer          = ( jbyte * ) env->GetLongField( cptr , CPtr_peer_ID );

   L = ( lua_State * ) peer;

   pushJNIEnv( env , L );

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

   obj = ( jobject * ) lua_touserdata( L , 1 );

   javaEnv = getEnvFromState( L );
   if ( javaEnv == NULL )
   {
      lua_pushstring( L , "Invalid JNI Environment." );
      lua_error( L );
   }

   if ( javaEnv->IsInstanceOf( *obj , java_function_class ) ==
        JNI_FALSE )
   {
      fprintf( stderr , "Called Java object is not a JavaFunction\n");
      return 0;
   }

   ret = javaEnv->CallIntMethod( *obj , java_function_method );

   exp = javaEnv->ExceptionOccurred();

   if ( exp != NULL )
   {
      jobject jstr;
      const char * str;
      
      javaEnv->ExceptionClear();
      jstr = javaEnv->CallObjectMethod( exp , get_message_method );

      if ( jstr == NULL )
      {
         jmethodID methodId;

         methodId = javaEnv->GetMethodID( throwable_class , "toString" , "()Ljava/lang/String;" );
         jstr = javaEnv->CallObjectMethod( exp , methodId );
      }

      str = javaEnv->GetStringUTFChars( ( jstring ) jstr , NULL );

      lua_pushstring( L , str );

      javaEnv->ReleaseStringUTFChars( ( jstring ) jstr, str );

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