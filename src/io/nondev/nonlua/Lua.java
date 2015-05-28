package io.nondev.nonlua;

import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.keplerproject.luajava.*;
import java.io.*;

public class Lua {
    public interface Loader {
        String path();
    }

    public interface Logger {
        void log(String msg);
    }

    public interface Function {
        int run();
    }

    final private static String LUAJAVA_LIB = "luajava";
  
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

    private static Loader loader;
    private static Logger logger;

    static {
        new SharedLibraryLoader().load(LUAJAVA_LIB);

        loader = new Loader() {
            public String path() {
                return "";
            }
        };

        logger = new Logger() {
            public void log(String msg) {
                System.out.println(msg);
            }
        };
    }

    public static void setLoader(Loader loader) {
        Lua.loader = loader;
    }

    public static void setLogger(Logger logger) {
        Lua.logger = logger;
    }

    final protected LuaState state;
    
    public Lua() {
        state = LuaStateFactory.newLuaState();
        state.openLibs();
    }

    protected Lua(LuaState state) {
        this.state = state;
    }
    
    public void dispose() {
        state.close();
    }
    
    private String readFile(String path) throws IOException {
        File file = new File(loader.path(), path);
        InputStream in = null;

        if (file.exists()) {
            in = new FileInputStream(file);
        } else {
            in = Lua.class.getResourceAsStream("/" + file.getPath().replace('\\', '/'));
        }

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
            try {
                state.LloadBuffer(readFile(chunk).getBytes(), chunk);
                return pcall(0, MULTIPLE_RETURN, 0);
            } catch (IOException e) {
                return -1;
            }
        }
        
        return state.LdoString(chunk);
    }
    
    public int load(String chunk) {
        if (chunk.endsWith(".lua")) {
            try {
                return state.LloadBuffer(readFile(chunk).getBytes(), chunk);
            } catch (IOException e) {
                return -1;
            }
        }
        
        return state.LloadString(chunk);
    }

    public Lua newThread() {
        return new Lua(state.newThread());
    }

    public int getTop() {
        return state.getTop();
    }

    public void setTop(int idx) {
        state.setTop(idx);
    }

    public void pushValue(int idx) {
        state.pushValue(idx);
    }
    
    public void remove(int idx) {
        state.remove(idx);
    }
    
    public void insert(int idx) {
        state.insert(idx);
    }
    
    public void replace(int idx) {
        state.replace(idx);
    }
    
    public int checkStack(int sz) {
        return state.checkStack(sz);
    }

    public void xmove(Lua to, int n) {
        state.xmove(to.state, n);
    }
    
    public boolean isNumber(int idx) {
        return state.isNumber(idx);
    }

    public boolean isString(int idx) {
        return state.isString(idx);
    }

    public boolean isFunction(int idx) {
        return state.isFunction(idx) || state.isJavaFunction(idx);
    }

    public boolean isUserdata(int idx) {
        return state.isUserdata(idx);
    }

    public boolean isTable(int idx) {
        return state.isTable(idx);
    }

    public boolean isBoolean(int idx) {
        return state.isBoolean(idx);
    }
    
    public boolean isNil(int idx) {
        return state.isNil(idx);
    }
    
    public boolean isThread(int idx) {
        return state.isThread(idx);
    }
    
    public boolean isNone(int idx) {
        return state.isNone(idx);
    }

    public boolean isObject(int idx) {
        return state.isObject(idx);
    }

    public int type(int idx) {
        return state.type(idx);
    }

    public String typeName(int tp) {
        return state.typeName(tp);
    }

    public int compare(int idx1, int idx2, int op) {
        return state.compare(idx1, idx2, op);
    }

    public double toDouble(int idx) {
        return state.toNumber(idx);
    }

    public int toInteger(int idx) {
        return state.toInteger(idx);
    }
    
    public boolean toBoolean(int idx) {
        return state.toBoolean(idx);
    }

    public String toString(int idx) {
        return state.toString(idx);
    }

    public Lua toThread(int idx) {
        return new Lua(state.toThread(idx));
    }

    public Object toObject(int idx) throws LuaException {
        return state.toJavaObject(idx);
    }

    public void pushNil() {
        state.pushNil();
    }

    public void pushDouble(double db) {
        state.pushNumber(db);
    }
    
    public void pushInteger(int integer) {
        state.pushInteger(integer);
    }

    public void pushString(String str) {
        state.pushString(str);
    }

    public void pushString(byte[] bytes) {
        state.pushString(bytes);
    }
    
    public void pushBoolean(boolean bool) {
        state.pushBoolean(bool);
    }

    public void pushObject(Object obj)  {
        state.pushJavaObject(obj);
    }
    
    public void pushArray(Object obj) throws LuaException {
        state.pushJavaArray(obj);
    }

    public void pushFunction(JavaFunction func) throws LuaException {
        state.pushJavaFunction(func);
    }

    public void pushObjectValue(Object obj) throws LuaException {
        state.pushObjectValue(obj);
    }

    public void getTable(int idx) {
        state.getTable(idx);
    }

    public int getMetaTable(int idx) {
        return state.getMetaTable(idx);
    }

    public void getMetaTable(String tName) {
        state.LgetMetatable(tName);
    }
    
    public void getField(int idx, String k) {
        state.getField(idx, k);
    }

    public int getMetaField(int obj, String e) {
        return state.LgetMetaField(obj, e);
    }

    public LuaObject getObject(String globalName) throws LuaException {
        return state.getLuaObject(globalName);
    }
    
    public LuaObject getObject(LuaObject parent, String name) throws LuaException {
        return state.getLuaObject(parent, name);
    }
    
    public LuaObject getObject(LuaObject parent, Number name) throws LuaException {
        return state.getLuaObject(parent, name);
    }
    
    public LuaObject getObject(LuaObject parent, LuaObject name) throws LuaException {
        return state.getLuaObject(parent, name);
    }

    public LuaObject getObject(int index) throws LuaException {
        return state.getLuaObject(index);
    }

    public Object getObjectFromUserdata(int idx) throws LuaException {
        return state.getObjectFromUserdata(idx);
    }
    
    public void createTable(int narr, int nrec) {
        state.createTable(narr, nrec);
    }

    public void newTable() {
        state.newTable();
    }

    public int newMetaTable(String tName) {
        return state.LnewMetatable(tName);
    }

    public void setTable(int idx) {
        state.setTable(idx);
    }
    
    public void setField(int idx, String k) {
        state.setField(idx, k);
    }

    public int setMetaTable(int idx) {
        return state.setMetaTable(idx);
    }

    public void call(int nArgs, int nResults) {
        state.call(nArgs, nResults);
    }

    public int callMeta(int obj, String e) {
        return state.LcallMeta(obj, e);
    }

    public int pcall(int nArgs, int nResults, int errFunc) {
        return state.pcall(nArgs, nResults, errFunc);
    }

    public int yield(int nResults) {
        return state.yield(nResults);
    }

    public int resume(int nArgs) {
        return state.resume(nArgs);
    }
    
    public int status() {
        return state.status();
    }
    
    public int gc(int what, int data) {
        return state.gc(what, data);
    }
    
    public int next(int idx) {
        return state.next(idx);
    }

    public int error() {
        return state.error();
    }

    public void concat(int n) {
        state.concat(n);
    }
    
    public int argError(int numArg, String extraMsg) {
        return state.LargError(numArg, extraMsg);
    }
    
    public String checkString(int numArg) {
        return state.LcheckString(numArg);
    }
    
    public String optString(int numArg, String def) {
        return state.LoptString(numArg, def);
    }
    
    public double checkNumber(int numArg) {
        return state.LcheckNumber(numArg);
    }
    
    public double optNumber(int numArg, double def) {
        return state.LoptNumber(numArg, def);
    }
    
    public int checkInteger(int numArg) {
        return state.LcheckInteger(numArg);
    }
    
    public int optInteger(int numArg, int def) {
        return state.LoptInteger(numArg, def);
    }
    
    public void checkStack(int sz, String msg) {
        state.LcheckStack(sz, msg);
    }
    
    public void checkType(int nArg, int t) {
        state.LcheckType(nArg, t);
    }
    
    public void checkAny(int nArg) {
        state.LcheckAny(nArg);
    }
    
    public void where(int lvl) {
        state.Lwhere(lvl);
    }
    
    public int ref(int t) {
        return state.Lref(t);
    }
    
    public void unRef(int t, int ref) {
        state.LunRef(t, ref);
    }
    
    public String gsub(String s, String p, String r) {
        return state.Lgsub(s, p, r);
    }
    
    public void pop(int n)  {
        state.pop(n);
    }

    public synchronized void getGlobal(String global) {
        state.getGlobal(global);
    }

    public synchronized void setGlobal(String name) {
        state.setGlobal(name);
    }

    public Number convertNumber(Double db, Class retType) {
        return state.convertLuaNumber(db, retType);
    }
}