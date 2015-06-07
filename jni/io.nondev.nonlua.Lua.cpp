#include <io.nondev.nonlua.Lua.h>

//@line:32

    #include <nonlua.h>
     JNIEXPORT jobject JNICALL Java_io_nondev_nonlua_Lua_jniOpen(JNIEnv* env, jclass clazz, jint stateId) {


//@line:36

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


//@line:152

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_close( L );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenBase(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:158

        lua_State * L = getStateFromCPtr( env , cptr );

        luaL_requiref( L , "_G" , luaopen_base , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenCoroutine(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:165

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_COLIBNAME , luaopen_coroutine , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenDebug(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:172

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_DBLIBNAME , luaopen_debug , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenIo(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:179

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_IOLIBNAME , luaopen_io , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenNet(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:186

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , "net" , luaopen_enet , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenJava(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:193

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


//@line:223

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_MATHLIBNAME , luaopen_math , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenOs(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:230

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_OSLIBNAME , luaopen_os , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenPackage(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:237

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_LOADLIBNAME , luaopen_package , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenSocket(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:244

        lua_State * L = getStateFromCPtr( env , cptr );
        open_luasocket( L );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenString(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:249

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_STRLIBNAME , luaopen_string , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenTable(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:256

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_TABLIBNAME , luaopen_table , 1 );
        lua_pop( L , 1 );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenUtf8(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:263

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_UTF8LIBNAME , luaopen_utf8 , 1 );
        lua_pop( L , 1 );
    

}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniLoadBuffer
(JNIEnv* env, jclass clazz, jobject cptr, jbyteArray obj_buff, jlong bsize, jstring obj_name, char* name, char* buff) {

//@line:270

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

//@line:276

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

//@line:283

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

//@line:292

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


//@line:298

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


//@line:313

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushnil( L );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushNumber(JNIEnv* env, jclass clazz, jobject cptr, jdouble db) {


//@line:319

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushnumber( L , ( lua_Number ) db );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushString(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_str) {
	char* str = (char*)env->GetStringUTFChars(obj_str, 0);


//@line:325

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushstring( L , str );
    
	env->ReleaseStringUTFChars(obj_str, str);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint val) {


//@line:331

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushboolean( L , ( int ) val );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushFunction(JNIEnv* env, jclass clazz, jobject cptr, jobject func) {


//@line:337

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


//@line:363

        lua_State * L = getStateFromCPtr( env , cptr );

        pushJavaObject( L , obj );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushArray(JNIEnv* env, jclass clazz, jobject cptr, jobject obj) {


//@line:369

        lua_State * L = getStateFromCPtr( env , cptr );

        pushJavaArray( L , obj );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNumber(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:375

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnumber( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:381

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isboolean( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsString(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:387

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isstring( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsFunction(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:393

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


//@line:411

        lua_State * L = getStateFromCPtr( env , cptr );
        
        return ( jint ) isJavaObject( L , index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsTable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:417

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_istable( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsUserdata(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:423

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isuserdata( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNil(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:429

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnil( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNone(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:435

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnone( L , ( int ) index );
    

}

JNIEXPORT jdouble JNICALL Java_io_nondev_nonlua_Lua_jniToNumber(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:441

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jdouble ) lua_tonumber( L , index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniToBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:447

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_toboolean( L , index );
    

}

JNIEXPORT jstring JNICALL Java_io_nondev_nonlua_Lua_jniToString(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:453

        lua_State * L = getStateFromCPtr( env , cptr );

        return env->NewStringUTF( lua_tostring( L , index ) );
    

}

JNIEXPORT jobject JNICALL Java_io_nondev_nonlua_Lua_jniToObject(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:459

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


//@line:471

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_getglobal( L , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetGlobal(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:477

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_setglobal( L , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGet(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:483

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_getfield( L , ( int ) index , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSet(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:489

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_setfield( L , ( int ) index , key );
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetI(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint key) {


//@line:495

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_geti( L , ( int ) index , ( int ) key );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetI(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint key) {


//@line:501

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_seti( L , ( int ) index , ( int ) key );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniGetTop(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:507

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_gettop( L );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetTop(JNIEnv* env, jclass clazz, jobject cptr, jint top) {


//@line:513

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_settop( L , ( int ) top );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPop(JNIEnv* env, jclass clazz, jobject cptr, jint num) {


//@line:519

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pop( L , ( int ) num );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushValue(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:525

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushvalue( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniCopy(JNIEnv* env, jclass clazz, jobject cptr, jint fromindex, jint toindex) {


//@line:531

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_copy( L , ( int ) fromindex, ( int ) toindex );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniRemove(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:537

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_remove( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniInsert(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:543

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_insert( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniReplace(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:549

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_replace( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniConcat(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:555

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_concat( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniLen(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:561

        lua_State * L = getStateFromCPtr( env , cptr );

        return (jint) luaL_len( L , ( int ) index );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniCompare(JNIEnv* env, jclass clazz, jobject cptr, jint index1, jint index2, jint op) {


//@line:567

        lua_State * L = getStateFromCPtr( env , cptr );

        return (jint) lua_compare( L , ( int ) index1 , ( int ) index2 , ( int ) op );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniType(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:573

        lua_State * L = getStateFromCPtr( env , cptr );

        return (jint) lua_type( L , ( int ) index );
    

}

JNIEXPORT jstring JNICALL Java_io_nondev_nonlua_Lua_jniTypeName(JNIEnv* env, jclass clazz, jobject cptr, jint type) {


//@line:579

        lua_State * L = getStateFromCPtr( env , cptr );
        
        return env->NewStringUTF( lua_typename( L , ( int ) type ) );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniRef(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:585

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_ref( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniUnRef(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint ref) {


//@line:591

        lua_State * L = getStateFromCPtr( env , cptr );

        luaL_unref( L , ( int ) index , ( int ) ref );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniCall(JNIEnv* env, jclass clazz, jobject cptr, jint nArgs, jint nResults) {


//@line:597

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_call( L , ( int ) nArgs , ( int ) nResults );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniPcall(JNIEnv* env, jclass clazz, jobject cptr, jint nArgs, jint nResults, jint errFunc) {


//@line:603

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_pcall( L , ( int ) nArgs , ( int ) nResults, ( int ) errFunc );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniNewTable(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:609

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_newtable( L );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetTable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:615

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_gettable( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetTable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:621

        lua_State * L = getStateFromCPtr( env , cptr );

        lua_settable( L , ( int ) index );
    

}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniNewMetatable
(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_name, char* name) {

//@line:627

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


//@line:633

        lua_State * L = getStateFromCPtr( env , cptr );
        
        return ( jint ) lua_getmetatable( L , ( int ) index );
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetMetatableStr(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_name) {
	char* name = (char*)env->GetStringUTFChars(obj_name, 0);


//@line:639

        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_getmetatable( L , name );
    
	env->ReleaseStringUTFChars(obj_name, name);

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniSetMetatable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:645

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_setmetatable( L , ( int ) index );
    

}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniCallmeta
(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_field, char* field) {

//@line:651

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

//@line:657

        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_getmetafield( L , ( int ) index , field );
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniGetmeta(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_field) {
	char* field = (char*)env->GetStringUTFChars(obj_field, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniGetmeta(env, clazz, cptr, index, obj_field, field);

	env->ReleaseStringUTFChars(obj_field, field);

	return JNI_returnValue;
}

