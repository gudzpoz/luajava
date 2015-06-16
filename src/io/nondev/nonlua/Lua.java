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
import java.util.List;
import java.util.Map;
import com.badlogic.gdx.jnigen.JniGenSharedLibraryLoader;
import com.badlogic.gdx.utils.ResourcePathFinder;

public class Lua {
    // @off
    /*JNI
    #include <nonluaconf.h>
    #include <nonlua.h>
    #include <nonlualib.h>
     */

    private static native CPtr jniOpen(int stateId); /*
        return nonlua_open(env, stateId);
    */

    private static native void jniClose(CPtr cptr); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_close(L);
    */

    private static native void jniOpenJava(CPtr cptr); /*
        lua_State* L = nonlua_getstate(env, cptr);

        PRELOAD(LUA_JAVALIBNAME, luaopen_java);
    */

    private static native void jniOpenSocket(CPtr cptr); /*
        lua_State * L = nonlua_getstate(env, cptr);

        nonluaopen_socket(L);
    */

    private static native int jniLoadBuffer(CPtr cptr, byte[] buff, long bsize, String name); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_loadbuffer(L, buff, (int) bsize, name);
    */

    private static native int jniLoadString(CPtr cptr, String str); /*
        lua_State * L   = nonlua_getstate(env, cptr);

        return (jint) luaL_loadstring(L, str);
    */


    private static native int jniRunBuffer(CPtr cptr, byte[] buff, long bsize, String name); /*
        lua_State * L = nonlua_getstate(env, cptr);
        
        int ret = luaL_loadbuffer(L, buff, (int) bsize, name);
        int secRet = lua_pcall(L, 0, LUA_MULTRET, 0);

        return (jint) (ret || secRet);
    */

    private static native int jniRunString(CPtr cptr, String str); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_dostring(L, str);
    */

    private static native CPtr jniNewThread(CPtr cptr); /*
        lua_State * L = nonlua_getstate(env, cptr);
        lua_State * newThread = lua_newthread(L);
        
        jclass tempClass = env->FindClass("io/nondev/nonlua/CPtr");
        jobject obj = env->AllocObject(tempClass);

        if (obj)
        {
            env->SetLongField(obj, env->GetFieldID(tempClass, "peer", "J"), (jlong) newThread);
        }

        return obj;
    */

    private static native void jniPushNil(CPtr cptr); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_pushnil(L);
    */

    private static native void jniPushNumber(CPtr cptr, double db); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_pushnumber(L, (lua_Number) db);
    */

    private static native void jniPushString(CPtr cptr, String str); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_pushstring(L, str);
    */

    private static native void jniPushBoolean(CPtr cptr, int val); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_pushboolean(L, (int) val);
    */

    private static native void jniPushFunction(CPtr cptr, LuaFunction func); /*
        lua_State* L = nonlua_getstate(env, cptr);

        nonlua_pushfunction(L, func);
    */

    private static native void jniPushObject(CPtr cptr, Object obj); /*
        lua_State * L = nonlua_getstate(env, cptr);

        nonlua_pushobject(L, obj);
    */

    private static native void jniPushArray(CPtr cptr, Object obj); /*
        lua_State * L = nonlua_getstate(env, cptr);

        nonlua_pusharray(L, obj);
    */

    private static native int jniIsNumber(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isnumber(L, (int) index);
    */

    private static native int jniIsBoolean(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isboolean(L, (int) index);
    */

    private static native int jniIsString(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isstring(L, (int) index);
    */

    private static native int jniIsFunction(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        int idx = (jint)index;

        return (jint)(
            lua_isfunction(L, idx) || 
            lua_iscfunction(L, idx) ||
            nonlua_isfunction(L, idx)
       );
    */

    private static native int jniIsObject(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);
        
        return (jint) nonlua_isobject(L, index);
    */

    private static native int jniIsTable(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_istable(L, (int) index);
    */

    private static native int jniIsUserdata(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isuserdata(L, (int) index);
    */

    private static native int jniIsNil(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isnil(L, (int) index);
    */

    private static native int jniIsNone(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isnone(L, (int) index);
    */

    private static native double jniToNumber(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jdouble) lua_tonumber(L, index);
    */

    private static native int jniToBoolean(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_toboolean(L, index);
    */

    private static native String jniToString(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return env->NewStringUTF(lua_tostring(L, index));
    */

    private static native Object jniToObject(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        int idx = (int)index;

        if (!nonlua_isobject(L, idx)) {
            return NULL;
        }
        
        jobject * obj = (jobject *)lua_touserdata(L, idx);
        return *obj;
    */

    private static native void jniGetGlobal(CPtr cptr, String key); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_getglobal(L, key);
    */

    private static native void jniSetGlobal(CPtr cptr, String key); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_setglobal(L, key);
    */

    private static native void jniGet(CPtr cptr, int index, String key); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_getfield(L, (int) index, key);
    */

    private static native void jniSet(CPtr cptr, int index, String key); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_setfield(L, (int) index, key);
    */

    private static native void jniGetI(CPtr cptr, int index, int key); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_rawgeti(L, (int) index, (int) key);
    */

    private static native void jniSetI(CPtr cptr, int index, int key); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_rawseti(L, (int) index, (int) key);
    */

    private static native int jniGetTop(CPtr cptr); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_gettop(L);
    */

    private static native void jniSetTop(CPtr cptr, int top); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_settop(L, (int) top);
    */

    private static native void jniPop(CPtr cptr, int num); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_pop(L, (int) num);
    */

    private static native void jniPushValue(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_pushvalue(L, (int) index);
    */

    private static native void jniRemove(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_remove(L, (int) index);
    */

    private static native void jniInsert(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_insert(L, (int) index);
    */

    private static native void jniReplace(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_replace(L, (int) index);
    */

    private static native void jniConcat(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_concat(L, (int) index);
    */

    private static native String jniGsub(CPtr cptr, String s, String p, String r); /*
        lua_State * L = nonlua_getstate(env, cptr);

        const char * sub = luaL_gsub(L, s, p, r);
        return env->NewStringUTF(sub);
    */

    private static native int jniLen(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_objlen(L, (int) index);
    */

    private static native int jniEqual(CPtr cptr, int index1, int index2); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_equal(L, (int) index1, (int) index2);
    */

    private static native int jniNext(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_next(L, (int) index);
    */

    private static native int jniError(CPtr cptr, String msg); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_error(L, msg);
    */

    private static native void jniWhere(CPtr cptr, int lvl); /*
        lua_State * L = nonlua_getstate(env, cptr);

        luaL_where(L, (int) lvl);
    */

    private static native int jniType(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_type(L, (int) index);
    */

    private static native String jniTypeName(CPtr cptr, int type); /*
        lua_State * L = nonlua_getstate(env, cptr);
        
        return env->NewStringUTF(lua_typename(L, (int) type));
    */

    private static native int jniRef(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_ref(L, (int) index);
    */

    private static native void jniUnRef(CPtr cptr, int index, int ref); /*
        lua_State * L = nonlua_getstate(env, cptr);

        luaL_unref(L, (int) index, (int) ref);
    */

    private static native void jniCall(CPtr cptr, int nArgs, int nResults); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_call(L, (int) nArgs, (int) nResults);
    */

    private static native int jniPcall(CPtr cptr, int nArgs, int nResults, int errFunc); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_pcall(L, (int) nArgs, (int) nResults, (int) errFunc);
    */

    private static native void jniNewTable(CPtr cptr); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_newtable(L);
    */

    private static native void jniGetTable(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_gettable(L, (int) index);
    */

    private static native void jniSetTable(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        lua_settable(L, (int) index);
    */

    private static native int jniNewMetatable(CPtr cptr, String name); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_newmetatable(L, name);
    */

    private static native int jniGetMetatable(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);
        
        return (jint) lua_getmetatable(L, (int) index);
    */

    private static native void jniGetMetatableStr(CPtr cptr, String name); /*
        lua_State * L = nonlua_getstate(env, cptr);
        
        luaL_getmetatable(L, name);
    */

    private static native int jniSetMetatable(CPtr cptr, int index); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_setmetatable(L, (int) index);
    */

    private static native int jniCallmeta(CPtr cptr, int index, String field); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_callmeta(L, (int) index, field);
    */

    private static native int jniGetmeta(CPtr cptr, int index, String field); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_getmetafield(L, (int) index, field);
    */

    private static native void jniMove(CPtr cptr, CPtr to, int index); /*
        lua_State * fr = nonlua_getstate(env, cptr);
        lua_State * t  = nonlua_getstate(env, to);

        lua_xmove(fr, t, (int) index);
    */

    private static native int jniYield(CPtr cptr, int nResults); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_yield(L, (int) nResults);
    */

    private static native int jniResume(CPtr cptr, int nArgs); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_resume(L, (int) nArgs);
    */

    private static native int jniStatus(CPtr cptr); /*
        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_status(L);
    */

    private static final String NONLUA_LIB = "nonlua";

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

    public static final int ERR_RUNTIME   = 2;
    public static final int ERR_SYNTAX    = 3;
    public static final int ERR_MEMORY    = 4;
    public static final int ERR_HANDLER   = 5;

    private ResourcePathFinder finder;
    private LuaConfiguration cfg;

    static {
        JniGenSharedLibraryLoader loader = new JniGenSharedLibraryLoader();
        loader.load("luajit");
        loader.load(NONLUA_LIB);
    }

    protected CPtr state;
    protected int stateId;

    public Lua() {
        this(new LuaConfiguration());
    }

    public Lua(LuaConfiguration cfg) {
        finder = new ResourcePathFinder();
        int stateId = LuaFactory.insert(this);
        open(cfg, jniOpen(stateId), stateId);
    }

    protected Lua(CPtr state) {
        int stateId = LuaFactory.insert(this);
        open(new LuaConfiguration(), state, stateId);
    }

    private void open(LuaConfiguration cfg, CPtr state, int stateId) {
        this.cfg = cfg;
        this.state = state;
        this.stateId = stateId;

        if (cfg.javaLib) jniOpenJava(state);
        if (cfg.socketLib) jniOpenSocket(state);

        push(new LuaFunction(this) {
            public int call() {
                int top = L.getTop();

                for (int i = 1; i < top; i++) {
                    String val = null;

                    if (L.isObject(i)) {
                        Object obj = L.toObject(i); 
                        if (obj != null) val = obj.toString();
                    } else if (L.isBoolean(i)) {
                        val = L.toBoolean(i) ? "true" : "false";
                    } else if (L.isNil(i)) {
                        val = "nil";
                    } else {
                        val = L.toString(i);
                    }

                    if (val == null) val = L.typeName(L.type(i));
                    Lua.this.cfg.logger.log(val + "\t");
                }

                Lua.this.cfg.logger.log("\n");
                return 0;
            }
        });

        set("print");

        push(new LuaFunction(this) {
            public int call() {
                if (!L.isString(1)) {
                    L.error("Wrong argument type, must be string.");
                }

                String path = L.toString(1);
                String fixedPath = finder.findResource(path);

                if (fixedPath != null) {
                    L.push(fixedPath);
                } else {
                    L.push(path);
                }

                return 1;
            }
        });

        set("topath");

        push(new LuaFunction(this) {
            public int call() {
                if (!L.isString(1)) {
                    L.error("Wrong argument type, must be string.");
                }

                String path = L.toString(1);
                String fixedPath = finder.findLibrary(path);

                if (fixedPath != null) {
                    L.push(fixedPath);
                } else {
                    L.push(path);
                }

                return 1;
            }
        });

        set("tolibpath");

        get("package");
        get(-1, "loaders");
        int count = len(-1);
        
        push(new LuaFunction(this) {
            public int call() {
                String name = L.toString(-1).replace(".", "/");

                if (L.load(name + ".lua") == -1)
                    L.push("Cannot load module " + name); 

                return 1;
            }
        });

        set(-2, count + 1);
        pop(1);
        get(-1, "path");
        push(";" + cfg.loader.path() + "/?.lua");
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
                byte[] buffer = LuaUtils.readStream(LuaUtils.getStream(cfg.loader, chunk)).getBytes();
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
                byte[] buffer = LuaUtils.readStream(LuaUtils.getStream(cfg.loader, chunk)).getBytes();
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

    public void push(Number num) {
        jniPushNumber(state, num.doubleValue());
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

    public void push(LuaValue obj) {
        obj.push();
    }

    public void push(List table) {
        newTable();
        int i = 1;
        for (Object field : table) {
            push(field);
            set(-2, i);
            pop(1);
            i++;
        }
    }

    public void push(Map table) {
        newTable();
        for (Object entry : table.entrySet()) {
            Map.Entry field = (Map.Entry)entry;
            push(field.getValue());
            set(-2, field.getKey().toString());
            pop(1);
        }
    }

    public void push(Object obj) {
        if (obj == null) {
            pushNil();
        } else if (obj instanceof Boolean) {
            push(((Boolean)obj));
        } else if (obj instanceof Number) {
            push(((Number)obj));
        } else if (obj instanceof String) {
            push((String) obj);
        } else if (obj instanceof LuaFunction) {
            push((LuaFunction)obj);
        } else if (obj instanceof LuaValue) {
            push((LuaValue)obj);
        } else if (obj instanceof List) {
            push((List)obj);
        } else if (obj instanceof Map) {
            push((Map)obj);
        } else if (obj.getClass().isArray()) {
            jniPushArray(state, obj);
        } else {
            jniPushObject(state, obj);
        }
    }

    public LuaValue pull(String key) {
        return new LuaValue(this, key);
    }
    
    public LuaValue pull(LuaValue parent, String key) {
        if (parent.getLua().getCPtrPeer() != state.getPeer()) return null;
        if (!parent.isTable() && !parent.isUserdata()) return null;
        return new LuaValue(parent, key);
    }
    
    public LuaValue pull(LuaValue parent, int key) {
        if (parent.getLua().getCPtrPeer() != state.getPeer()) return null;
        if (!parent.isTable() && !parent.isUserdata()) return null;
        return new LuaValue(parent, key);
    }
    
    public LuaValue pull(LuaValue parent, LuaValue key) {
        if (parent.getLua().getCPtrPeer() != state.getPeer() ||
            parent.getLua().getCPtrPeer() != key.getLua().getCPtrPeer())
            return null;

        if (parent.getLua() != key.getLua()) return null;
        if (!parent.isTable() && !parent.isUserdata()) return null;

        return new LuaValue(parent, key);
    }

    public LuaValue pull(int index) {
        return new LuaValue(this, index);
    }

    public boolean isNumber(int index) {
        return jniIsNumber(state, index) != 0;
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

    public Number toNumber(int index) {
        return jniToNumber(state, index);
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

    public String gsub(String s, String p, String r) {
        return jniGsub(state, s, p, r);
    }

    public int len(int index) {
        return jniLen(state, index);
    }

    public boolean equal(int index1, int index2) {
        return jniEqual(state, index1, index2) == 0;
    }

    public int next(int index) {
        return jniNext(state, index);
    }

    public int error(String msg) {
        return jniError(state, msg);
    }

    public void where(int lvl) {
        jniWhere(state, lvl);
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

    public void move(Lua to, int index) {
        jniMove(state, to.state, index);
    }

    public int yield(int nResults) {
        return jniYield(state, nResults);
    }

    public int resume(int nArgs) {
        return jniResume(state, nArgs);
    }
    
    public int status() {
        return jniStatus(state);
    }
}
