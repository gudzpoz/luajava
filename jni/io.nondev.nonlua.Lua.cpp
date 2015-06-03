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

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenJava(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:184

        lua_State* L = getStateFromCPtr( env, cptr );
        
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

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenMath(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:214

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_MATHLIBNAME , luaopen_math , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenOs(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:221

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_OSLIBNAME , luaopen_os , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenPackage(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:228

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_LOADLIBNAME , luaopen_package , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenString(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:235

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_STRLIBNAME , luaopen_string , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenTable(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:242

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_TABLIBNAME , luaopen_table , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenUtf8(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:249

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_UTF8LIBNAME , luaopen_utf8 , 1 );
        lua_pop( L , 1 );
    

}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniLoadBuffer
(JNIEnv* env, jclass clazz, jobject cptr, jbyteArray obj_buff, jlong bsize, jstring obj_name, char* name, char* buff) {

//@line:256

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

//@line:262

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

//@line:269

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

//@line:278

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


//@line:284

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


//@line:299

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushnil( L );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushNumber(JNIEnv* env, jclass clazz, jobject cptr, jdouble db) {


//@line:305

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushnumber( L , ( lua_Number ) db );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushInteger(JNIEnv* env, jclass clazz, jobject cptr, jint integer) {


//@line:311

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushinteger( L, ( lua_Integer ) integer );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushString(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_str) {
	char* str = (char*)env->GetStringUTFChars(obj_str, 0);


//@line:317

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushstring( L , str );
    
	env->ReleaseStringUTFChars(obj_str, str);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint val) {


//@line:323

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushboolean( L , ( int ) val );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushFunction(JNIEnv* env, jclass clazz, jobject cptr, jobject func) {


//@line:329

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


//@line:355

        lua_State * L = getStateFromCPtr( env , cptr );

        pushJavaObject( L , obj );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushArray(JNIEnv* env, jclass clazz, jobject cptr, jobject obj) {


//@line:361

        lua_State * L = getStateFromCPtr( env , cptr );

        pushJavaArray( L , obj );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNumber(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:367

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnumber( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsInteger(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:373

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isinteger( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:379

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isboolean( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsString(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:385

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isstring( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsFunction(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:391

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


//@line:409

        lua_State * L = getStateFromCPtr( env , cptr );
        
        return ( jint ) isJavaObject( L , index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsTable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:415

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_istable( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsUserdata(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:421

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isuserdata( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNil(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:427

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnil( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNone(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:433

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnone( L , ( int ) index );
    

}

JNIEXPORT jdouble JNICALL Java_io_nondev_nonlua_Lua_jniToNumber(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:439

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jdouble ) lua_tonumber( L , index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniToInteger(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:445

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_tointeger( L , index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniToBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:451

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_toboolean( L , index );
    

}

JNIEXPORT jstring JNICALL Java_io_nondev_nonlua_Lua_jniToString(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:457

        lua_State * L = getStateFromCPtr( env , cptr );

        return env->NewStringUTF( lua_tostring( L , index ) );
    

}

JNIEXPORT jobject JNICALL Java_io_nondev_nonlua_Lua_jniToObject(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:463

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


//@line:475

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_getglobal( L , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetGlobal(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:481

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_setglobal( L , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGet(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:487

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_getfield( L , ( int ) index , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSet(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:493

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_setfield( L , ( int ) index , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetI(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint key) {


//@line:499

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_geti( L , ( int ) index , ( int ) key );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetI(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint key) {


//@line:505

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_seti( L , ( int ) index , ( int ) key );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetTable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:511

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_gettable( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniGetTop(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:517

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_gettop( L );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetTop(JNIEnv* env, jclass clazz, jobject cptr, jint top) {


//@line:523

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_settop( L , ( int ) top );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPop(JNIEnv* env, jclass clazz, jobject cptr, jint num) {


//@line:529

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pop( L , ( int ) num );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniCopy(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:535

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushvalue( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniRemove(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:541

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_remove( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniInsert(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:547

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_insert( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniReplace(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:553

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_replace( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniConcat(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:559

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_concat( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniLen(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:565

        lua_State * L = getStateFromCPtr( env , cptr );

        return (jint) luaL_len( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniType(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:571

        lua_State * L = getStateFromCPtr( env , cptr );

        return (jint) lua_type( L , ( int ) index );
    

}

JNIEXPORT jstring JNICALL Java_io_nondev_nonlua_Lua_jniTypeName(JNIEnv* env, jclass clazz, jobject cptr, jint type) {


//@line:577

        lua_State * L = getStateFromCPtr( env , cptr );
        
        return env->NewStringUTF( lua_typename( L , ( int ) type ) );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniRef(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:583

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_ref( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniUnRef(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint ref) {


//@line:589

        lua_State * L = getStateFromCPtr( env , cptr );

        luaL_unref( L , ( int ) index , ( int ) ref );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniCall(JNIEnv* env, jclass clazz, jobject cptr, jint nArgs, jint nResults) {


//@line:595

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_call( L , ( int ) nArgs , ( int ) nResults );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniPcall(JNIEnv* env, jclass clazz, jobject cptr, jint nArgs, jint nResults, jint errFunc) {


//@line:601

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_pcall( L , ( int ) nArgs , ( int ) nResults, ( int ) errFunc );
    

}

