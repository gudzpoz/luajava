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

        lua_pushcfunction( L , luaopen_base );
        lua_pushstring( L , "" );
        lua_call( L , 1 , 0 );
    */

    private static native void jniOpenCoroutine(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushcfunction( L , luaopen_coroutine );
        lua_pushstring( L , LUA_COLIBNAME );
        lua_call( L , 1 , 0 );
    */

    private static native void jniOpenDebug(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushcfunction( L , luaopen_debug );
        lua_pushstring( L , LUA_DBLIBNAME );
        lua_call( L , 1 , 0 );
    */

    private static native void jniOpenIo(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushcfunction( L , luaopen_io );
        lua_pushstring( L , LUA_IOLIBNAME );
        lua_call( L , 1 , 0 );
    */

    private static native void jniOpenJava(CPtr cptr); /*
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
    */

    private static native void jniOpenMath(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushcfunction( L , luaopen_math );
        lua_pushstring( L , LUA_MATHLIBNAME );
        lua_call( L , 1 , 0 );
    */

    private static native void jniOpenOs(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushcfunction( L , luaopen_os );
        lua_pushstring( L , LUA_OSLIBNAME );
        lua_call( L , 1 , 0 );
    */

    private static native void jniOpenPackage(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushcfunction( L , luaopen_package );
        lua_pushstring( L , LUA_LOADLIBNAME );
        lua_call( L , 1 , 0 );
    */

    private static native void jniOpenString(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushcfunction( L , luaopen_string );
        lua_pushstring( L , LUA_STRLIBNAME );
        lua_call( L , 1 , 0 );
    */

    private static native void jniOpenTable(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushcfunction( L , luaopen_table );
        lua_pushstring( L , LUA_TABLIBNAME );
        lua_call( L , 1 , 0 );
    */

    private static native void jniOpenUtf8(CPtr cptr); /*
        lua_State * L = getStateFromCPtr( env , cptr );

        lua_pushcfunction( L , luaopen_utf8 );
        lua_pushstring( L , LUA_UTF8LIBNAME );
        lua_call( L , 1 , 0 );
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
    protected final int stateId;

    public Lua() {
        this(new LuaConfiguration());
    }
    
    public Lua(LuaConfiguration cfg) {
        stateId = LuaFactory.insert(this);
        state = jniOpen(stateId);

        if (cfg.baseLib) jniOpenBase(state);
        if (cfg.coroutineLib) jniOpenCoroutine(state);
        if (cfg.debugLib) jniOpenDebug(state);
        if (cfg.ioLib) jniOpenIo(state);
        if (cfg.javaLib) jniOpenJava(state);
        if (cfg.mathLib) jniOpenMath(state);
        if (cfg.osLib) jniOpenOs(state);
        if (cfg.packageLib) jniOpenPackage(state);
        if (cfg.stringLib) jniOpenString(state);
        if (cfg.tableLib) jniOpenTable(state);
        if (cfg.utf8Lib) jniOpenUtf8(state);

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