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

import java.io.IOException;
import com.badlogic.gdx.jnigen.JniGenSharedLibraryLoader;

public class Lua {
    // @off
    /*JNI
    #include <nonlua.h>
     */

    private static native CPtr jniOpen(int stateId); /*
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
    */

    private static native void jniClose(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_close( L );
    */

    private static native void jniOpenBase(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        luaL_requiref( L , "_G" , luaopen_base , 1 );
        lua_pop( L , 1 );
    */

    private static native void jniOpenCoroutine(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_COLIBNAME , luaopen_coroutine , 1 );
        lua_pop( L , 1 );
    */

    private static native void jniOpenDebug(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_DBLIBNAME , luaopen_debug , 1 );
        lua_pop( L , 1 );
    */

    private static native void jniOpenIo(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_IOLIBNAME , luaopen_io , 1 );
        lua_pop( L , 1 );
    */

    private static native void jniOpenNet(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , "net" , luaopen_enet , 1 );
        lua_pop( L , 1 );
    */

    private static native void jniOpenJava(CPtr cptr); /*
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
    */

    private static native void jniOpenMath(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_MATHLIBNAME , luaopen_math , 1 );
        lua_pop( L , 1 );
    */

    private static native void jniOpenOs(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_OSLIBNAME , luaopen_os , 1 );
        lua_pop( L , 1 );
    */

    private static native void jniOpenPackage(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_LOADLIBNAME , luaopen_package , 1 );
        lua_pop( L , 1 );
    */

    private static native void jniOpenSocket(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        open_luasocket( L );
    */

    private static native void jniOpenString(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_STRLIBNAME , luaopen_string , 1 );
        lua_pop( L , 1 );
    */

    private static native void jniOpenTable(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_TABLIBNAME , luaopen_table , 1 );
        lua_pop( L , 1 );
    */

    private static native void jniOpenUtf8(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_requiref( L , LUA_UTF8LIBNAME , luaopen_utf8 , 1 );
        lua_pop( L , 1 );
    */

    private static native int jniLoadBuffer(CPtr cptr, byte[] buff, long bsize, String name); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_loadbuffer( L , buff , ( int ) bsize, name );
    */

    private static native int jniLoadString(CPtr cptr, String str); /*
        lua_State * L   = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_loadstring( L , str );
    */


    private static native int jniRunBuffer(CPtr cptr, byte[] buff, long bsize, String name); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        int ret = luaL_loadbuffer( L , buff , ( int ) bsize, name );
        int secRet = lua_pcall(L, 0, LUA_MULTRET, 0);

        return ( jint ) ( ret || secRet );
    */

    private static native int jniRunString(CPtr cptr, String str); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_dostring( L , str );
    */

    private static native CPtr jniNewThread(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        lua_State * newThread = lua_newthread( L );
        
        jclass tempClass = env->FindClass( "io/nondev/nonlua/CPtr" );
        jobject obj = env->AllocObject( tempClass );

        if ( obj )
        {
            env->SetLongField( obj , env->GetFieldID( tempClass , "peer" , "J" ), ( jlong ) newThread );
        }

        return obj;
    */

    private static native void jniPushNil(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushnil( L );
    */

    private static native void jniPushNumber(CPtr cptr, double db); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushnumber( L , ( lua_Number ) db );
    */

    private static native void jniPushInteger(CPtr cptr, int integer); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushinteger( L, ( lua_Integer ) integer );
    */

    private static native void jniPushString(CPtr cptr, String str); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushstring( L , str );
    */

    private static native void jniPushBoolean(CPtr cptr, int val); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushboolean( L , ( int ) val );
    */

    private static native void jniPushFunction(CPtr cptr, LuaFunction func); /*
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
    */

    private static native void jniPushObject(CPtr cptr, Object obj); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        pushJavaObject( L , obj );
    */

    private static native void jniPushArray(CPtr cptr, Object obj); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        pushJavaArray( L , obj );
    */

    private static native int jniIsNumber(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnumber( L , ( int ) index );
    */

    private static native int jniIsInteger(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isinteger( L , ( int ) index );
    */

    private static native int jniIsBoolean(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isboolean( L , ( int ) index );
    */

    private static native int jniIsString(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isstring( L , ( int ) index );
    */

    private static native int jniIsFunction(CPtr cptr, int index); /*
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
    */

    private static native int jniIsObject(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        return ( jint ) isJavaObject( L , index );
    */

    private static native int jniIsTable(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_istable( L , ( int ) index );
    */

    private static native int jniIsUserdata(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isuserdata( L , ( int ) index );
    */

    private static native int jniIsNil(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnil( L , ( int ) index );
    */

    private static native int jniIsNone(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_isnone( L , ( int ) index );
    */

    private static native double jniToNumber(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jdouble ) lua_tonumber( L , index );
    */

    private static native int jniToInteger(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_tointeger( L , index );
    */

    private static native int jniToBoolean(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_toboolean( L , index );
    */

    private static native String jniToString(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return env->NewStringUTF( lua_tostring( L , index ) );
    */

    private static native Object jniToObject(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        if ( !isJavaObject( L , index ) )
        {
            return NULL;
        }
        
        jobject * obj = ( jobject * ) lua_touserdata( L , ( int ) index );
        return *obj;
    */

    private static native void jniGetGlobal(CPtr cptr, String key); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_getglobal( L , key );
    */

    private static native void jniSetGlobal(CPtr cptr, String key); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_setglobal( L , key );
    */

    private static native void jniGet(CPtr cptr, int index, String key); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_getfield( L , ( int ) index , key );
    */

    private static native void jniSet(CPtr cptr, int index, String key); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_setfield( L , ( int ) index , key );
    */

    private static native void jniGetI(CPtr cptr, int index, int key); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_geti( L , ( int ) index , ( int ) key );
    */

    private static native void jniSetI(CPtr cptr, int index, int key); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_seti( L , ( int ) index , ( int ) key );
    */

    private static native int jniGetTop(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_gettop( L );
    */

    private static native void jniSetTop(CPtr cptr, int top); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_settop( L , ( int ) top );
    */

    private static native void jniPop(CPtr cptr, int num); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pop( L , ( int ) num );
    */

    private static native void jniPushValue(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushvalue( L , ( int ) index );
    */

    private static native void jniCopy(CPtr cptr, int fromindex, int toindex); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_copy( L , ( int ) fromindex, ( int ) toindex );
    */

    private static native void jniRemove(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_remove( L , ( int ) index );
    */

    private static native void jniInsert(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_insert( L , ( int ) index );
    */

    private static native void jniReplace(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_replace( L , ( int ) index );
    */

    private static native void jniConcat(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_concat( L , ( int ) index );
    */

    private static native int jniLen(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return (jint) luaL_len( L , ( int ) index );
    */

    private static native int jniType(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return (jint) lua_type( L , ( int ) index );
    */

    private static native String jniTypeName(CPtr cptr, int type); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        return env->NewStringUTF( lua_typename( L , ( int ) type ) );
    */

    private static native int jniRef(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_ref( L , ( int ) index );
    */

    private static native void jniUnRef(CPtr cptr, int index, int ref); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        luaL_unref( L , ( int ) index , ( int ) ref );
    */

    private static native void jniCall(CPtr cptr, int nArgs, int nResults); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_call( L , ( int ) nArgs , ( int ) nResults );
    */

    private static native int jniPcall(CPtr cptr, int nArgs, int nResults, int errFunc); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_pcall( L , ( int ) nArgs , ( int ) nResults, ( int ) errFunc );
    */

    private static native void jniNewTable(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_newtable( L );
    */

    private static native void jniGetTable(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_gettable( L , ( int ) index );
    */

    private static native void jniSetTable(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_settable( L , ( int ) index );
    */

    private static native int jniNewMetatable(CPtr cptr, String name); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_newmetatable( L , name );
    */

    private static native int jniGetMetatable(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        return ( jint ) lua_getmetatable( L , ( int ) index );
    */

    private static native void jniGetMetatableStr(CPtr cptr, String name); /*
        lua_State * L = getStateFromCPtr( env , cptr );
        
        luaL_getmetatable( L , name );
    */

    private static native int jniSetMetatable(CPtr cptr, int index); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) lua_setmetatable( L , ( int ) index );
    */

    private static native int jniCallmeta(CPtr cptr, int index, String field); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_callmeta( L , ( int ) index , field );
    */

    private static native int jniGetmeta(CPtr cptr, int index, String field); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        return ( jint ) luaL_getmetafield( L , ( int ) index , field );
    */

    private static final String LIB = "nonlua";

    public static final int GLOBALS       = -10002;
    public static final int REGISTRY      = -10000;
    public static final int NONE          = -1;
    public static final int NIL           = 0;
    public static final int BOOLEAN       = 1;
    public static final int LIGHTUSERDATA = 2;
    public static final int NUMBER        = 3;
    public static final int STRING        = 4;
    public static final int TABLE         = 5;
    public static final int FUNCTION      = 6;
    public static final int USERDATA      = 7;
    public static final int THREAD        = 8;
    public static final int MULTRET       = -1;
    public static final int YIELD         = 1;
    public static final int RUNTIME_ERROR = 2;
    public static final int SYNTAX_ERROR  = 3;
    public static final int MEMORY_ERROR  = 4;
    public static final int HANDLER_ERROR = 5;
    public static final int GC_STOP       = 0;
    public static final int GC_RESTART    = 1;
    public static final int GC_COLLECT    = 2;
    public static final int GC_COUNT      = 3;
    public static final int GC_COUNTB     = 4;
    public static final int GC_STEP       = 5;
    public static final int GC_SETPAUSE   = 6;
    public static final int GC_SETSTEPMUL = 7;

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
    protected int stateId;

    public Lua() {
        this(new LuaConfiguration());
    }

    public Lua(LuaConfiguration cfg) {
        int stateId = LuaFactory.insert(this);
        open(cfg, jniOpen(stateId), stateId);
    }

    protected Lua(CPtr state) {
        int stateId = LuaFactory.insert(this);
        open(new LuaConfiguration(), state, stateId);
    }

    private void open(LuaConfiguration cfg, CPtr state, int stateId) {
        this.state = state;
        this.stateId = stateId;

        if (cfg.baseLib) jniOpenBase(state);
        if (cfg.coroutineLib) jniOpenCoroutine(state);
        if (cfg.debugLib) jniOpenDebug(state);
        if (cfg.ioLib) jniOpenIo(state);
        if (cfg.netLib) jniOpenNet(state);
        if (cfg.javaLib) jniOpenJava(state);
        if (cfg.mathLib) jniOpenMath(state);
        if (cfg.osLib) jniOpenOs(state);
        if (cfg.packageLib) jniOpenPackage(state);
        if (cfg.socketLib) jniOpenSocket(state);
        if (cfg.stringLib) jniOpenString(state);
        if (cfg.tableLib) jniOpenTable(state);
        if (cfg.utf8Lib) jniOpenUtf8(state);

        push(new LuaFunction(this) {
            public int call() {
                for (int i = 2; i <= L.getTop(); i++) {
                    if (L.isNil(i) || L.isNone(i)) {
                        logger.log("nil");
                        logger.log("\t");
                    }
                    
                    String type = L.typeName(L.type(i));
                    String val = null;

                    if (type.equals("userdata")) {
                        Object obj = L.toObject(i); 
                        if (obj != null) val = obj.toString();
                    } else if (type.equals("boolean")) {  
                        val = L.toBoolean(i) ? "true" : "false";
                    } else {
                        val = L.toString(i);
                    }

                    if (val == null) val = type;
                    logger.log(val);
                    logger.log("\t");
                }

                logger.log("\n");
                return 0;
            }
        });

        set("print");

        get("package");
        get(-1, "searchers");
        int count = len(-1);
        
        push(new LuaFunction(this) {
            public int call() {
                String name = L.toString(-1);

                if (L.load(name + ".lua") == -1)
                    L.push("Cannot load module " + name); 

                return 1;
            }
        });

        set(-2, count + 1);
        pop(1);
        get(-1, "path");
        push(";" + loader.path() + "/?.lua");
        concat(2);
        set(-2, "path");
        pop(1);
    }

    protected long getCPtrPeer() {
        return (state != null) ? state.getPeer() : 0;
    }
    
    public void dispose() {
        LuaFactory.remove(stateId);
        jniClose(state);
        state = null;
    }
    
    public int run(String chunk) {
        if (chunk.endsWith(".lua")) {
            try {
                byte[] buffer = LuaUtils.readStream(LuaUtils.getStream(loader, chunk)).getBytes();
                return jniRunBuffer(state, buffer, buffer.length, chunk);
            } catch (IOException e) {
                return -1;
            }
        }
        
        return jniRunString(state, chunk);
    }
    
    public int load(String chunk) {
        if (chunk.endsWith(".lua")) {
            try {
                byte[] buffer = LuaUtils.readStream(LuaUtils.getStream(loader, chunk)).getBytes();
                return jniLoadBuffer(state, buffer, buffer.length, chunk);
            } catch (IOException e) {
                return -1;
            }
        }
        
        return jniLoadString(state, chunk);
    }

    public Lua newThread() {
        return new Lua(jniNewThread(state));
    }

    public void pushNil() {
        jniPushNil(state);
    }

    public void push(double db) {
        jniPushNumber(state, db);
    }
    
    public void push(int integer) {
        jniPushInteger(state, integer);
    }

    public void push(String str) {
        jniPushString(state, str);
    }
    
    public void push(boolean bool) {
        jniPushBoolean(state, bool ? 1 : 0);
    }

    public void push(LuaFunction func) {
        jniPushFunction(state, func);
    }

    public void push(LuaObject obj) {
        obj.push();
    }

    public void push(Object obj) {
        if (obj == null) {
            pushNil();
        } else if (obj instanceof Boolean) {
            push(((Boolean)obj).booleanValue());
        } else if (obj instanceof Number) {
            push(((Number)obj).doubleValue());
        } else if (obj instanceof String) {
            push((String) obj);
        } else if (obj instanceof LuaFunction) {
            push((LuaFunction)obj);
        } else if (obj instanceof LuaObject) {
            push((LuaObject)obj);
        // else if (obj instanceof byte[]) { pushString((byte[]) obj); }
        } else if (obj.getClass().isArray()) {
            jniPushArray(state, obj);
        } else {
            jniPushObject(state, obj);
        }
    }

    public LuaObject pull(String globalName) {
        return new LuaObject(this, globalName);
    }
    
    public LuaObject pull(LuaObject parent, String name) {
        if (parent.getLua().getCPtrPeer() != state.getPeer()) return null;
        if (!parent.isTable() && !parent.isUserdata()) return null;
        return new LuaObject(parent, name);
    }
    
    public LuaObject pull(LuaObject parent, Number name) {
        if (parent.getLua().getCPtrPeer() != state.getPeer()) return null;
        if (!parent.isTable() && !parent.isUserdata()) return null;
        return new LuaObject(parent, name);
    }
    
    public LuaObject pull(LuaObject parent, LuaObject name) {
        if (parent.getLua().getCPtrPeer() != state.getPeer() ||
            parent.getLua().getCPtrPeer() != name.getLua().getCPtrPeer())
            return null;

        if (parent.getLua() != name.getLua()) return null;
        if (!parent.isTable() && !parent.isUserdata()) return null;

        return new LuaObject(parent, name);
    }

    public LuaObject pull(int index) {
        return new LuaObject(this, index);
    }

    public boolean isNumber(int index) {
        return jniIsNumber(state, index) != 0;
    }

    public boolean isInteger(int index) {
        return jniIsInteger(state, index) != 0;
    }

    public boolean isBoolean(int index) {
        return jniIsBoolean(state, index) != 0;
    }

    public boolean isString(int index) {
        return jniIsString(state, index) != 0;
    }

    public boolean isFunction(int index) {
        return jniIsFunction(state, index) != 0;
    }

    public boolean isTable(int index) {
        return jniIsTable(state, index) != 0;
    }

    public boolean isUserdata(int index) {
        return jniIsUserdata(state, index) != 0;
    }

    public boolean isObject(int index) {
        return jniIsObject(state, index) != 0;
    }
    
    public boolean isNil(int index) {
        return jniIsNil(state, index) != 0;
    }

    public boolean isNone(int index) {
        return jniIsNone(state, index) != 0;
    }

    public double toNumber(int index) {
        return jniToNumber(state, index);
    }

    public int toInteger(int index) {
        return jniToInteger(state, index);
    }
    
    public boolean toBoolean(int index) {
        return jniToBoolean(state, index) != 0;
    }

    public String toString(int index) {
        return jniToString(state, index);
    }

    public Object toObject(int index) {
        return jniToObject(state, index);
    }

    public void get(String key) {
        jniGetGlobal(state, key);
    }

    public void set(String key) {
        jniSetGlobal(state, key);
    }

    public void get(int index, String key) {
        jniGet(state, index, key);
    }

    public void set(int index, String key) {
        jniSet(state, index, key);
    }

    public void get(int index, int key) {
        jniGetI(state, index, key);
    }

    public void set(int index, int key) {
        jniSetI(state, index, key);
    }

    public int getTop() {
        return jniGetTop(state);
    }

    public void setTop(int top) {
        jniSetTop(state, top);
    }

    public void pop(int num)  {
        jniPop(state, num);
    }

    public void copy(int index)  {
        jniPushValue(state, index);
    }

    public void copy(int fromindex, int toindex)  {
        jniCopy(state, fromindex, toindex);
    }

    public void remove(int index) {
        jniRemove(state, index);
    }
    
    public void insert(int index) {
        jniInsert(state, index);
    }
    
    public void replace(int index) {
        jniReplace(state, index);
    }

    public void concat(int index) {
        jniConcat(state, index);
    }

    public int len(int index) {
        return jniLen(state, index);
    }

    public int type(int index) {
        return jniType(state, index);
    }

    public String typeName(int type) {
        return jniTypeName(state, type);
    }

    public int ref(int index) {
        return jniRef(state, index);
    }
    
    public void unRef(int index, int ref) {
        jniUnRef(state, index, ref);
    }

    public void call(int nArgs, int nResults) {
        jniCall(state, nArgs, nResults);
    }

    public int pcall(int nArgs, int nResults) {
        return pcall(nArgs, nResults, 0);
    }

    public int pcall(int nArgs, int nResults, int errFunc) {
        return jniPcall(state, nArgs, nResults, errFunc);
    }

    public void newTable() {
        jniNewTable(state);
    }

    public void getTable(int index) {
        jniGetTable(state, index);
    }

    public void setTable(int index) {
        jniSetTable(state, index);
    }

    public int newMetatable(String name) {
        return jniNewMetatable(state, name);
    }

    public int getMetatable(int index) {
        return jniGetMetatable(state, index);
    }

    public void getMetatable(String name) {
        jniGetMetatableStr(state, name);
    }

    public int setMetatable(int index) {
        return jniSetMetatable(state, index);
    }

    public int callmeta(int index, String field) {
        return jniCallmeta(state, index, field);
    }

    public int getmeta(int index, String field) {
        return jniGetmeta(state, index, field);
    }

    // ************************************************************************************************
    // TODO: Unfinished API is below
    // ************************************************************************************************
    
    /*
    
    public int checkStack(int sz) {
        return 0;
    }

    public void move(Lua to, int n) {
    }

    public int compare(int index1, int index2, int op) {
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
    
    public int next(int index) {
        return 0;
    }

    public int error() {
        return 0;
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
    
    public String gsub(String s, String p, String r) {
        return "";
    }

    */
}