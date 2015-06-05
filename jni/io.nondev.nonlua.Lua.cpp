#include <io.nondev.nonlua.Lua.h>

//@line:30

    #include <nonlua.h>
     JNIEXPORT jobject JNICALL Java_io_nondev_nonlua_Lua_jniOpen(JNIEnv* env, jclass clazz, jint stateId) {


//@line:34

        lua_State * L = luaL_newstate();
        lua_pushstring( L , LUAJAVASTATEINDEX );
        lua_pushnumber( L , (lua_Number)stateId );
        lua_settable( L , LUA_REGISTRYINDEX );

        jobject obj;
        jclass tempClass;

        tempClass = env->FindClass( "io/nondev/nonlua/CPtr" );
            
        obj = env->AllocObject( tempClass );
        if ( obj )
        {
            env->SetLongField( obj , env->GetFieldID( tempClass , "peer", "J" ) , ( jlong ) L );
        }

        if ( luajava_api_class == NULL )
        {
            tempClass = env->FindClass( "io/nondev/nonlua/LuaJava" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Could not find LuaJava class\n" );
                exit( 1 );
            }

            if ( ( luajava_api_class = ( jclass ) env->NewGlobalRef( tempClass ) ) == NULL )
            {
                fprintf( stderr , "Could not bind to LuaJavaAPI class\n" );
                exit( 1 );
            }
        }

        if ( java_function_class == NULL )
        {
            tempClass = env->FindClass( "io/nondev/nonlua/LuaFunction" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Could not find LuaFunction interface\n" );
                exit( 1 );
            }

            if ( ( java_function_class = ( jclass ) env->NewGlobalRef( tempClass ) ) == NULL )
            {
                fprintf( stderr , "Could not bind to LuaFunction interface\n" );
                exit( 1 );
            }
        }

        if ( java_function_method == NULL )
        {
            java_function_method = env->GetMethodID( java_function_class , "call" , "()I");
            if ( !java_function_method )
            {
                fprintf( stderr , "Could not find <call> method in LuaFunction\n" );
                exit( 1 );
            }
        }

        if ( throwable_class == NULL )
        {
            tempClass = env->FindClass( "java/lang/Throwable" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Error. Couldn't bind java class java.lang.Throwable\n" );
                exit( 1 );
            }

            throwable_class = ( jclass ) env->NewGlobalRef( tempClass );

            if ( throwable_class == NULL )
            {
                fprintf( stderr , "Error. Couldn't bind java class java.lang.Throwable\n" );
                exit( 1 );
            }
        }

        if ( get_message_method == NULL )
        {
            get_message_method = env->GetMethodID( throwable_class , "getMessage" ,
                                                        "()Ljava/lang/String;" );

            if ( get_message_method == NULL )
            {
                fprintf(stderr, "Could not find <getMessage> method in java.lang.Throwable\n");
                exit(1);
            }
        }

        if ( java_lang_class == NULL )
        {
            tempClass = env->FindClass( "java/lang/Class" );

            if ( tempClass == NULL )
            {
                fprintf( stderr , "Error. Coundn't bind java class java.lang.Class\n" );
                exit( 1 );
            }

            java_lang_class = ( jclass ) env->NewGlobalRef( tempClass );

            if ( java_lang_class == NULL )
            {
                fprintf( stderr , "Error. Couldn't bind java class java.lang.Throwable\n" );
                exit( 1 );
            }
        }

        pushJNIEnv( env, L );
        
        return obj;
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniClose(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:150

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_close( L );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenBase(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:156

        lua_State * L = getStateFromCPtr( env , cptr );

        luaL_requiref( L , "_G" , luaopen_base , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenCoroutine(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:163

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_COLIBNAME , luaopen_coroutine , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenDebug(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:170

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_DBLIBNAME , luaopen_debug , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenIo(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:177

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_IOLIBNAME , luaopen_io , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenNet(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:184

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , "net" , luaopen_enet , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenJava(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:191

        lua_State* L = getStateFromCPtr( env, cptr );
        
        lua_newtable( L );
        lua_setglobal( L , "java" );
        lua_getglobal( L , "java" );
        
        lua_pushstring( L , "require" );
        lua_pushcfunction( L , &javaRequire );
        lua_settable( L , -3 );

        lua_pushstring( L , "new" );
        lua_pushcfunction( L , &javaNew );
        lua_settable( L , -3 );

        lua_pushstring( L , "loadlib" );
        lua_pushcfunction( L , &javaLoadLib );
        lua_settable( L , -3 );

        lua_pushstring( L , "proxy" );
        lua_pushcfunction( L , &javaProxy );
        lua_settable( L , -3 );

        lua_pushstring( L , "instanceof" );
        lua_pushcfunction( L , &javaInstanceOf );
        lua_settable( L , -3 );

        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenMath(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:221

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_MATHLIBNAME , luaopen_math , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenOs(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:228

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_OSLIBNAME , luaopen_os , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenPackage(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:235

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_LOADLIBNAME , luaopen_package , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenSocket(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:242

        lua_State * L = getStateFromCPtr( env , cptr );
        
        lua_getglobal(L, "package");
        lua_getfield(L, -1, "preload");

        lua_pushcfunction( L , luaopen_socket_core );
        lua_setfield( L , -2 , "socket.core" );
        lua_pop( L , 1 );

        lua_pushcfunction( L , luaopen_mime_core );
        lua_setfield(L, -2, "mime.core");
        lua_pop( L , 3 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenString(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:257

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_STRLIBNAME , luaopen_string , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenTable(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:264

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_TABLIBNAME , luaopen_table , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenUtf8(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:271

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_UTF8LIBNAME , luaopen_utf8 , 1 );
        lua_pop( L , 1 );
    

}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniLoadBuffer
(JNIEnv* env, jclass clazz, jobject cptr, jbyteArray obj_buff, jlong bsize, jstring obj_name, char* name, char* buff) {

//@line:278

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_loadbuffer( L , buff , ( int ) bsize, name );
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniLoadBuffer(JNIEnv* env, jclass clazz, jobject cptr, jbyteArray obj_buff, jlong bsize, jstring obj_name) {
	char* name = (char*)env->GetStringUTFChars(obj_name, 0);
	char* buff = (char*)env->GetPrimitiveArrayCritical(obj_buff, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniLoadBuffer(env, clazz, cptr, obj_buff, bsize, obj_name, name, buff);

	env->ReleasePrimitiveArrayCritical(obj_buff, buff, 0);
	env->ReleaseStringUTFChars(obj_name, name);

	return JNI_returnValue;
}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniLoadString
(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_str, char* str) {

//@line:284

        lua_State * L   = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_loadstring( L , str );
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniLoadString(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_str) {
	char* str = (char*)env->GetStringUTFChars(obj_str, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniLoadString(env, clazz, cptr, obj_str, str);

	env->ReleaseStringUTFChars(obj_str, str);

	return JNI_returnValue;
}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniRunBuffer
(JNIEnv* env, jclass clazz, jobject cptr, jbyteArray obj_buff, jlong bsize, jstring obj_name, char* name, char* buff) {

//@line:291

        lua_State * L = getStateFromCPtr( env , cptr );
        
        int ret = luaL_loadbuffer( L , buff , ( int ) bsize, name );
        int secRet = lua_pcall(L, 0, LUA_MULTRET, 0);

        return ( jint ) ( ret || secRet );
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniRunBuffer(JNIEnv* env, jclass clazz, jobject cptr, jbyteArray obj_buff, jlong bsize, jstring obj_name) {
	char* name = (char*)env->GetStringUTFChars(obj_name, 0);
	char* buff = (char*)env->GetPrimitiveArrayCritical(obj_buff, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniRunBuffer(env, clazz, cptr, obj_buff, bsize, obj_name, name, buff);

	env->ReleasePrimitiveArrayCritical(obj_buff, buff, 0);
	env->ReleaseStringUTFChars(obj_name, name);

	return JNI_returnValue;
}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniRunString
(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_str, char* str) {

//@line:300

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_dostring( L , str );
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniRunString(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_str) {
	char* str = (char*)env->GetStringUTFChars(obj_str, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniRunString(env, clazz, cptr, obj_str, str);

	env->ReleaseStringUTFChars(obj_str, str);

	return JNI_returnValue;
}

JNIEXPORT jobject JNICALL Java_io_nondev_nonlua_Lua_jniNewThread(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:306

        lua_State * L = getStateFromCPtr( env , cptr );
        lua_State * newThread = lua_newthread( L );
        
        jclass tempClass = env->FindClass( "io/nondev/nonlua/CPtr" );
        jobject obj = env->AllocObject( tempClass );

        if ( obj )
        {
            env->SetLongField( obj , env->GetFieldID( tempClass , "peer" , "J" ), ( jlong ) newThread );
        }

        return obj;
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushNil(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:321

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushnil( L );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushNumber(JNIEnv* env, jclass clazz, jobject cptr, jdouble db) {


//@line:327

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushnumber( L , ( lua_Number ) db );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushInteger(JNIEnv* env, jclass clazz, jobject cptr, jint integer) {


//@line:333

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushinteger( L, ( lua_Integer ) integer );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushString(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_str) {
	char* str = (char*)env->GetStringUTFChars(obj_str, 0);


//@line:339

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushstring( L , str );
    
	env->ReleaseStringUTFChars(obj_str, str);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint val) {


//@line:345

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushboolean( L , ( int ) val );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushFunction(JNIEnv* env, jclass clazz, jobject cptr, jobject func) {


//@line:351

        lua_State* L = getStateFromCPtr( env , cptr );

        jobject * userData , globalRef;

        globalRef = env->NewGlobalRef( func );

        userData = ( jobject * ) lua_newuserdata( L , sizeof( jobject ) );
        *userData = globalRef;

        lua_newtable( L );

        lua_pushstring( L , LUACALLMETAMETHODTAG );
        lua_pushcfunction( L , &luaJavaFunctionCall );
        lua_rawset( L , -3 );

        lua_pushstring( L , LUAGCMETAMETHODTAG );
        lua_pushcfunction( L , &gc );
        lua_rawset( L , -3 );

        lua_pushstring( L , LUAJAVAOBJECTIND );
        lua_pushboolean( L , 1 );
        lua_rawset( L , -3 );
        lua_setmetatable( L , -2 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushObject(JNIEnv* env, jclass clazz, jobject cptr, jobject obj) {


//@line:377

        lua_State * L = getStateFromCPtr( env , cptr );

        pushJavaObject( L , obj );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushArray(JNIEnv* env, jclass clazz, jobject cptr, jobject obj) {


//@line:383

        lua_State * L = getStateFromCPtr( env , cptr );

        pushJavaArray( L , obj );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNumber(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:389

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnumber( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsInteger(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:395

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isinteger( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:401

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isboolean( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsString(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:407

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isstring( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsFunction(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:413

        lua_State * L = getStateFromCPtr( env , cptr );

        int isJavaFunction = 0;

        if ( isJavaObject( L , index ) )
        {
            jobject * obj = ( jobject * ) lua_touserdata( L , ( int ) index );
            isJavaFunction = env->IsInstanceOf( *obj , java_function_class );
        }

        return ( jint ) ( 
            lua_isfunction( L , ( int ) index ) || 
            lua_iscfunction( L , ( int ) index ) || 
            isJavaFunction
        );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsObject(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:431

        lua_State * L = getStateFromCPtr( env , cptr );
        
        return ( jint ) isJavaObject( L , index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsTable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:437

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_istable( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsUserdata(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:443

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isuserdata( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNil(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:449

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnil( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNone(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:455

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnone( L , ( int ) index );
    

}

JNIEXPORT jdouble JNICALL Java_io_nondev_nonlua_Lua_jniToNumber(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:461

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jdouble ) lua_tonumber( L , index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniToInteger(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:467

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_tointeger( L , index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniToBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:473

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_toboolean( L , index );
    

}

JNIEXPORT jstring JNICALL Java_io_nondev_nonlua_Lua_jniToString(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:479

        lua_State * L = getStateFromCPtr( env , cptr );

        return env->NewStringUTF( lua_tostring( L , index ) );
    

}

JNIEXPORT jobject JNICALL Java_io_nondev_nonlua_Lua_jniToObject(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:485

        lua_State * L = getStateFromCPtr( env , cptr );

        if ( !isJavaObject( L , index ) )
        {
            return NULL;
        }
        
        jobject * obj = ( jobject * ) lua_touserdata( L , ( int ) index );
        return *obj;
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetGlobal(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:497

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_getglobal( L , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetGlobal(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:503

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_setglobal( L , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGet(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:509

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_getfield( L , ( int ) index , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSet(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:515

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_setfield( L , ( int ) index , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetI(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint key) {


//@line:521

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_geti( L , ( int ) index , ( int ) key );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetI(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint key) {


//@line:527

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_seti( L , ( int ) index , ( int ) key );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniGetTop(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:533

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_gettop( L );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetTop(JNIEnv* env, jclass clazz, jobject cptr, jint top) {


//@line:539

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_settop( L , ( int ) top );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPop(JNIEnv* env, jclass clazz, jobject cptr, jint num) {


//@line:545

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pop( L , ( int ) num );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushValue(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:551

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushvalue( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniCopy(JNIEnv* env, jclass clazz, jobject cptr, jint fromindex, jint toindex) {


//@line:557

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_copy( L , ( int ) fromindex, ( int ) toindex );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniRemove(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:563

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_remove( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniInsert(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:569

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_insert( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniReplace(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:575

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_replace( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniConcat(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:581

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_concat( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniLen(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:587

        lua_State * L = getStateFromCPtr( env , cptr );

        return (jint) luaL_len( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniType(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:593

        lua_State * L = getStateFromCPtr( env , cptr );

        return (jint) lua_type( L , ( int ) index );
    

}

JNIEXPORT jstring JNICALL Java_io_nondev_nonlua_Lua_jniTypeName(JNIEnv* env, jclass clazz, jobject cptr, jint type) {


//@line:599

        lua_State * L = getStateFromCPtr( env , cptr );
        
        return env->NewStringUTF( lua_typename( L , ( int ) type ) );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniRef(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:605

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_ref( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniUnRef(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint ref) {


//@line:611

        lua_State * L = getStateFromCPtr( env , cptr );

        luaL_unref( L , ( int ) index , ( int ) ref );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniCall(JNIEnv* env, jclass clazz, jobject cptr, jint nArgs, jint nResults) {


//@line:617

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_call( L , ( int ) nArgs , ( int ) nResults );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniPcall(JNIEnv* env, jclass clazz, jobject cptr, jint nArgs, jint nResults, jint errFunc) {


//@line:623

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_pcall( L , ( int ) nArgs , ( int ) nResults, ( int ) errFunc );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniNewTable(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:629

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_newtable( L );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetTable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:635

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_gettable( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetTable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:641

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_settable( L , ( int ) index );
    

}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniNewMetatable
(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_name, char* name) {

//@line:647

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_newmetatable( L , name );
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniNewMetatable(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_name) {
	char* name = (char*)env->GetStringUTFChars(obj_name, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniNewMetatable(env, clazz, cptr, obj_name, name);

	env->ReleaseStringUTFChars(obj_name, name);

	return JNI_returnValue;
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniGetMetatable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:653

        lua_State * L = getStateFromCPtr( env , cptr );
        
        return ( jint ) lua_getmetatable( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetMetatableStr(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_name) {
	char* name = (char*)env->GetStringUTFChars(obj_name, 0);


//@line:659

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_getmetatable( L , name );
    
	env->ReleaseStringUTFChars(obj_name, name);

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniSetMetatable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:665

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_setmetatable( L , ( int ) index );
    

}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniCallmeta
(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_field, char* field) {

//@line:671

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_callmeta( L , ( int ) index , field );
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniCallmeta(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_field) {
	char* field = (char*)env->GetStringUTFChars(obj_field, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniCallmeta(env, clazz, cptr, index, obj_field, field);

	env->ReleaseStringUTFChars(obj_field, field);

	return JNI_returnValue;
}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniGetmeta
(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_field, char* field) {

//@line:677

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_getmetafield( L , ( int ) index , field );
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniGetmeta(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_field) {
	char* field = (char*)env->GetStringUTFChars(obj_field, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniGetmeta(env, clazz, cptr, index, obj_field, field);

	env->ReleaseStringUTFChars(obj_field, field);

	return JNI_returnValue;
}

