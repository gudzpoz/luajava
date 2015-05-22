package io.nondev.nonlua;

import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.keplerproject.luajava.*;
import java.io.*;

public static class Lua {
    private final static String LUAJAVA_LIB = "luajava";
  
    final public static Integer GLOBALS  = LuaState.LUA_GLOBALSINDEX;
    final public static Integer REGISTRY = LuaState.LUA_REGISTRYINDEX;

    final public static Integer NONE          = LuaState.LUA_TNONE;
    final public static Integer NIL           = LuaState.LUA_TNIL;
    final public static Integer BOOLEAN       = LuaState.LUA_TBOOLEAN;
    final public static Integer LIGHTUSERDATA = LuaState.LUA_TLIGHTUSERDATA;
    final public static Integer NUMBER        = LuaState.LUA_TNUMBER;
    final public static Integer STRING        = LuaState.LUA_TSTRING;
    final public static Integer TABLE         = LuaState.LUA_TTABLE;
    final public static Integer FUNCTION      = LuaState.LUA_TFUNCTION;
    final public static Integer USERDATA      = LuaState.LUA_TUSERDATA;
    final public static Integer THREAD        = LuaState.LUA_TTHREAD;
    
    final public static Integer MULTIPLE_RETURN = LuaState.LUA_MULTRET;
    final public static Integer YIELD           = LuaState.LUA_YIELD;
    final public static Integer ERROR_RUNTIME   = LuaState.LUA_ERRRUN;
    final public static Integer ERROR_SYNTAX    = LuaState.LUA_ERRSYNTAX;
    final public static Integer ERROR_MEMORY    = LuaState.LUA_ERRMEM;
    final public static Integer ERROR_HANDLER   = LuaState.LUA_ERRERR;
    
    final public static Integer GC_STOP       = LuaState.LUA_GCSTOP;
    final public static Integer GC_RESTART    = LuaState.LUA_GCRESTART;
    final public static Integer GC_COLLECT    = LuaState.LUA_GCCOLLECT;
    final public static Integer GC_COUNT      = LuaState.LUA_GCCOUNT;
    final public static Integer GC_COUNTB     = LuaState.LUA_GCCOUNTB;
    final public static Integer GC_STEP       = LuaState.LUA_GCSTEP;
    final public static Integer GC_SETPAUSE   = LuaState.LUA_GCSETPAUSE;
    final public static Integer GC_SETSTEPMUL = LuaState.LUA_GCSETSTEPMUL;
    
    private static LuaState state;
    private static boolean isAndroid;
    
    public static void open() {
        open(false);
    }
    
    public static void open(boolean isAndroid) {
        new SharedLibraryLoader().load(LUAJAVA_LIB);
        state = LuaStateFactory.newLuaState();
        state.openLibs();
        Lua.isAndroid = isAndroid;
    }
    
    public static void close() {
        state.close();
        state = null;
    }
    
    private static String readFile(String filename) {
        InputStream in = 
                isAndroid ? 
                Lua.class.getResourceAsStream("/assets/" + filename) :
                Lua.class.getResourceAsStream("/" + filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        
        return out.toString();
    }
    
    public static int run(String chunk) {
        if (chunk.endsWith(".lua")) {
            loadBuffer(readFile(chunk).getBytes(), chunk);
            return pcall(0, MULTIPLE_RETURN, 0);
        }
        
        return doString(chunk);
    }
    
    public static int load(String chunk) {
        if (chunk.endsWith(".lua")) {
            return loadBuffer(readFile(chunk).getBytes(), chunk);
        }
        
        return loadString(chunk);
    }
    
    public static int getTop() {
        return state.getTop();
    }

    public static void setTop(int idx) {
        state.setTop(idx);
    }

    public static void pushValue(int idx) {
        state.pushValue(idx);
    }
    
    public static void remove(int idx) {
        state.remove(idx);
    }
    
    public static void insert(int idx) {
        state.insert(idx);
    }
    
    public static void replace(int idx) {
        state.replace(idx);
    }
    
    public static int checkStack(int sz) {
        return state.checkStack(sz);
    }
    
    public static boolean isNumber(int idx) {
        return state.isNumber(idx);
    }

    public static boolean isString(int idx) {
        return state.isString(idx);
    }

    public static boolean isFunction(int idx) {
        return state.isFunction(idx);
    }

    public static boolean isUserdata(int idx) {
        return state.isUserdata(idx);
    }

    public static boolean isTable(int idx) {
        return state.isTable(idx);
    }

    public static boolean isBoolean(int idx) {
        return state.isBoolean(idx);
    }
    
    public static boolean isNil(int idx) {
        return state.isNil(idx);
    }
    
    public static boolean isThread(int idx) {
        return state.isThread(idx);
    }
    
    public static boolean isNone(int idx) {
        return state.isNone(idx);
    }

    public static int type(int idx) {
        return state.type(idx);
    }

    public static String typeName(int tp) {
        return state.typeName(tp);
    }

    public static int compare(int idx1, int idx2, int op) {
        return state.compare(idx1, idx2, op);
    }

    public static double toDouble(int idx) {
        return state.toNumber(idx);
    }

    public static int toInteger(int idx) {
        return state.toInteger(idx);
    }
    
    public static boolean toBoolean(int idx) {
        return state.toBoolean(idx);
    }

    public static String toString(int idx) {
        return state.toString(idx);
    }

    public static void pushNil() {
        state.pushNil();
    }

    public static void pushDouble(double db) {
        state.pushNumber(db);
    }
    
    public static void pushInteger(int integer) {
        state.pushInteger(integer);
    }

    public static void pushString(String str) {
        state.pushString(str);
    }

    public static void pushString(byte[] bytes) {
        state.pushString(bytes);
    }
    
    public static void pushBoolean(boolean bool) {
        state.pushBoolean(bool);
    }

    public static void getTable(int idx) {
        state.getTable(idx);
    }
    
    public static void getField(int idx, String k) {
        state.getField(idx, k);
    }
    
    public static void createTable(int narr, int nrec) {
        state.createTable(narr, nrec);
    }

    public static void newTable() {
        state.newTable();
    }

    public static int getMetaTable(int idx) {
        return state.getMetaTable(idx);
    }

    public static void setTable(int idx) {
        state.setTable(idx);
    }
    
    public static void setField(int idx, String k) {
        state.setField(idx, k);
    }

    public static int setMetaTable(int idx) {
        return state.setMetaTable(idx);
    }

    public static void call(int nArgs, int nResults) {
        state.call(nArgs, nResults);
    }

    public static int pcall(int nArgs, int nResults, int errFunc) {
        state.pcall(nArgs, nResults, errFunc);
    }

    public static int yield(int nResults) {
        return state.yield(nResults);
    }

    public static int resume(int nArgs) {
        return state.resume(nArgs);
    }
    
    public static int status() {
        return state.status();
    }
    
    public static int gc(int what, int data) {
        return state.gc(what, data);
    }
    
    public static int next(int idx) {
        return state.next(idx);
    }

    public static int error() {
        return state.error();
    }

    public static void concat(int n) {
        state.concat(n);
    }
    
    public static int doFile(String fileName) {
        return state.LdoFile(fileName);
    }

    public static int doString(String str) {
        return state.LdoString(str);
    }
      
    public static int getMetaField(int obj, String e) {
        return state.LgetMetaField(obj, e);
    }
    
    public static int callMeta(int obj, String e) {
        return state.LcallMeta(obj, e);
    }
    
    public static int argError(int numArg, String extraMsg) {
        return state.LargError(numArg, extraMsg);
    }
    
    public static String checkString(int numArg) {
        return state.LcheckString(numArg);
    }
    
    public static String optString(int numArg, String def) {
        return state.LoptString(numArg, def);
    }
    
    public static double checkNumber(int numArg) {
        return state.LcheckNumber(numArg);
    }
    
    public static double optNumber(int numArg, double def) {
        return state.LoptNumber(numArg, def);
    }
    
    public static int checkInteger(int numArg) {
        return state.LcheckInteger(numArg);
    }
    
    public static int optInteger(int numArg, int def) {
        return state.LoptInteger(numArg, def);
    }
    
    public static void checkStack(int sz, String msg) {
        state.LcheckStack(sz, msg);
    }
    
    public static void checkType(int nArg, int t) {
        state.LcheckType(nArg, t);
    }
    
    public static void checkAny(int nArg) {
        state.LcheckAny(nArg);
    }
    
    public static int newMetatable(String tName) {
        return state.LnewMetatable(tName);
    }
    
    public static void getMetatable(String tName) {
        state.LgetMetatable(tName);
    }
    
    public static void where(int lvl) {
        state.Lwhere(lvl);
    }
    
    public static int ref(int t) {
        return state.Lref(t);
    }
    
    public static void unRef(int t, int ref) {
        state.LunRef(t, ref);
    }
    
    public static int loadFile(String fileName) {
        return state.LloadFile(fileName);
    }
    
    public static int loadString(String s) {
        return state.LloadString(s);
    }
    
    public static int loadBuffer(byte[] buff, String name) {
        return state.LloadBuffer(buff, name);
    }
    
    public static String gsub(String s, String p, String r) {
        return state.Lgsub(s, p, r);
    }
    
    public static void pop(int n)  {
        state.pop(n);
    }

    public static synchronized void getGlobal(String global) {
        state.getGlobal(global);
    }

    public static synchronized void setGlobal(String name) {
        state.setGlobal(name);
    }
    
    public static Object getObjectFromUserdata(int idx) throws LuaException {
        return state.getObjectFromUserdata(idx);
    }

    public static boolean isObject(int idx) {
        return state.isObject(idx);
    }

    public static void pushJavaObject(Object obj)  {
        state.pushJavaObject(obj);
    }
    
    public static void pushJavaArray(Object obj) throws LuaException {
        state.pushJavaArray(obj);
    }

    public static void pushJavaFunction(JavaFunction func) throws LuaException {
        state.pushJavaFunction(func);
    }

    public static boolean isJavaFunction(int idx) {
        return state.isJavaFunction(idx);
    }

    public static void pushObjectValue(Object obj) throws LuaException {
        state.pushObjectValue(obj);
    }
    
    public static Object toJavaObject(int idx) throws LuaException {
        return state.toJavaObject(idx);
    }
    
    public static LuaObject getLuaObject(String globalName) {
        return state.getLuaObject(globalName);
    }
    
    public static LuaObject getLuaObject(LuaObject parent, String name) {
        return state.getLuaObject(parent, name);
    }
    
    public static LuaObject getLuaObject(LuaObject parent, Number name) {
        return state.getLuaObject(parent, name);
    }
    
    public static LuaObject getLuaObject(LuaObject parent, LuaObject name) {
        return state.getLuaObject(parent, name);
    }

    public static LuaObject getLuaObject(int index) {
        return state.getLuaObject(index);
    }

    public static Number convertLuaNumber(Double db, Class retType) {
        return state.convertLuaNumber(db, retType);
    }
}