#include "jni.h"
#include "lua.hpp"

#include "jua.h"
#include "juaapi.h"

// For template usage
const char JAVA_CLASS_META_REGISTRY[] = "__jclass__";
const char JAVA_OBJECT_META_REGISTRY[] = "__jobject__";
const char JAVA_ARRAY_META_REGISTRY[] =  "__jarray__";
const char JAVA_PACKAGE_META_REGISTRY[] =  "__jpackage__";

// Bindings
// java.lang.Class, Class::forName
jclass    java_lang_class_class   = NULL;
jmethodID java_lang_class_forname = NULL;
// party.iroiro.jua.JuaAPI
jclass    juaapi_class          = NULL;
jmethodID juaapi_classnew       = NULL;
jmethodID juaapi_classindex     = NULL;
jmethodID juaapi_classinvoke    = NULL;
jmethodID juaapi_classsiginvoke = NULL;
jmethodID juaapi_classnewindex  = NULL;
jmethodID juaapi_objectindex    = NULL;
jmethodID juaapi_objectinvoke   = NULL;
jmethodID juaapi_objsiginvoke   = NULL;
jmethodID juaapi_objectnewindex = NULL;
jmethodID juaapi_arraynew       = NULL;
jmethodID juaapi_arraylen       = NULL;
jmethodID juaapi_arrayindex     = NULL;
jmethodID juaapi_arraynewindex  = NULL;
jmethodID juaapi_threadnewid    = NULL;
jmethodID juaapi_luaify         = NULL;
jmethodID juaapi_import         = NULL;
jmethodID juaapi_proxy          = NULL;
jmethodID juaapi_load           = NULL;
// java.lang.Throwable
jclass java_lang_throwable_class = NULL;
jmethodID throwable_getmessage   = NULL;
jmethodID throwable_tostring     = NULL;

static const char * RETURN_IMPORT_WRAPPER = (
    "return function(self, name)\n"
    "  if type(self) ~= 'table' then\n"
    "    error(\"bad argument #1 to 'java.import.?': expecting a table\")\n"
    "  end\n"
    "  if type(name) ~= 'string' then\n"
    "    error(\"bad argument #2 to 'java.import.?': expecting a valid string\")\n"
    "  end\n"
    "\n"
    "  local value = rawget(self, name)\n"
    "  if value ~= nil then\n"
    "    return value\n"
    "  else\n"
    "    local depth = rawget(self, 1)\n"
    "    local current = rawget(self, 2)\n"
    "    local meta = getmetatable(self)\n"
    "    local v = nil\n"
    "    if depth == 1 then\n"
    "      v = rawget(meta, '__import')(current .. name)\n"
    "    else\n"
    "      v = {\n"
    "        [1]  = depth - 1,\n"
    "        [2] = current .. name .. '.'\n"
    "      }\n"
    "      setmetatable(v, meta)\n"
    "    end\n"
    "    rawset(self, name, v)\n"
    "    return v\n"
    "  end\n"
    "end\n"
);

int updateJNIEnv(JNIEnv * env);

/**
 * The new panic function that panics the JVM
 */
int fatalError(lua_State * L) {
  JNIEnv * env = getJNIEnv(L);
  env->FatalError(lua_tostring(L, -1));
  return 0;
}

/**
 * Returns a global reference to the class matching the name
 *
 * Exceptions on the Java side is not cleared (NoClassDefFoundError, for example).
 */
jclass bindJavaClass(JNIEnv * env, const char * name) {
  jclass tempClass;
  tempClass = env->FindClass(name);
  if (tempClass == NULL) {
    return NULL;
  } else {
    jclass classRef = (jclass) env->NewGlobalRef(tempClass);
    // https://stackoverflow.com/q/33481144/17780636
    // env->DeleteLocalRef(tempClass);
    if (classRef == NULL) {
      return NULL;
    } else {
      return classRef;
    }
  }
}

/**
 * Returns the methodID
 */
jmethodID bindJavaStaticMethod(JNIEnv * env, jclass c, const char * name, const char * sig) {
  jmethodID id = env->GetStaticMethodID(c, name, sig);
  if (id == NULL) {
    return NULL;
  }
  return id;
}

/**
 * Returns the methodID
 */
jmethodID bindJavaMethod(JNIEnv * env, jclass c, const char * name, const char * sig) {
  jmethodID id = env->GetMethodID(c, name, sig);
  if (id == NULL) {
    return NULL;
  }
  return id;
}

// TODO: switch to reinterpret_cast<jclass> etc.
/**
 * Init JNI cache bindings
 * See AbstractLua.java
 * Returns zero if completed without errors
 */
int initBindings(JNIEnv * env) {
  if (updateJNIEnv(env) != 0) {
    return -1;
  }

  java_lang_class_class = bindJavaClass(env, "java/lang/Class");
  java_lang_class_forname = bindJavaStaticMethod(env, java_lang_class_class,
          "forName", "(Ljava/lang/String;)Ljava/lang/Class;");

  java_lang_throwable_class = bindJavaClass(env, "java/lang/Throwable");
  throwable_getmessage = bindJavaMethod(env, java_lang_throwable_class,
          "getMessage", "()Ljava/lang/String;");
  throwable_tostring = bindJavaMethod(env, java_lang_throwable_class,
          "toString", "()Ljava/lang/String;");

  juaapi_class = bindJavaClass(env, "party/iroiro/luajava/JuaAPI");
  juaapi_classnew = bindJavaStaticMethod(env, juaapi_class,
          "classNew", "(ILjava/lang/Object;I)I");
  juaapi_classindex = bindJavaStaticMethod(env, juaapi_class,
          "classIndex", "(ILjava/lang/Class;Ljava/lang/String;)I");
  juaapi_classinvoke = bindJavaStaticMethod(env, juaapi_class,
          "classInvoke", "(ILjava/lang/Class;Ljava/lang/String;I)I");
  juaapi_classsiginvoke = bindJavaStaticMethod(env, juaapi_class,
          "classInvoke", "(ILjava/lang/Class;Ljava/lang/String;Ljava/lang/String;I)I");
  juaapi_classnewindex = bindJavaStaticMethod(env, juaapi_class,
          "classNewIndex", "(ILjava/lang/Class;Ljava/lang/String;)I");
  juaapi_objectindex = bindJavaStaticMethod(env, juaapi_class,
          "objectIndex", "(ILjava/lang/Object;Ljava/lang/String;)I");
  juaapi_objectinvoke = bindJavaStaticMethod(env, juaapi_class,
          "objectInvoke", "(ILjava/lang/Object;Ljava/lang/String;I)I");
  juaapi_objsiginvoke = bindJavaStaticMethod(env, juaapi_class,
          "objectInvoke", "(ILjava/lang/Object;Ljava/lang/String;Ljava/lang/String;I)I");
  juaapi_objectnewindex = bindJavaStaticMethod(env, juaapi_class,
          "objectNewIndex", "(ILjava/lang/Object;Ljava/lang/String;)I");
  juaapi_arraynew = bindJavaStaticMethod(env, juaapi_class,
          "arrayNew", "(ILjava/lang/Object;I)I");
  juaapi_arraylen = bindJavaStaticMethod(env, juaapi_class,
          "arrayLength", "(Ljava/lang/Object;)I");
  juaapi_arrayindex = bindJavaStaticMethod(env, juaapi_class,
          "arrayIndex", "(ILjava/lang/Object;I)I");
  juaapi_arraynewindex = bindJavaStaticMethod(env, juaapi_class,
          "arrayNewIndex", "(ILjava/lang/Object;I)I");
  juaapi_threadnewid = bindJavaStaticMethod(env, juaapi_class,
          "threadNewId", "(IJ)I");
  juaapi_luaify = bindJavaStaticMethod(env, juaapi_class,
          "luaify", "(I)I");
  juaapi_import = bindJavaStaticMethod(env, juaapi_class,
          "javaImport", "(ILjava/lang/String;)I");
  juaapi_proxy = bindJavaStaticMethod(env, juaapi_class,
          "proxy", "(I)I");
  juaapi_load = bindJavaStaticMethod(env, juaapi_class,
          "load", "(ILjava/lang/String;)I");
  if (java_lang_class_class == NULL
      || java_lang_class_forname == NULL
      || java_lang_throwable_class == NULL
      || throwable_getmessage == NULL
      || throwable_tostring == NULL
      || juaapi_class == NULL
      || juaapi_classnew == NULL
      || juaapi_classindex == NULL
      || juaapi_classinvoke == NULL
      || juaapi_classsiginvoke == NULL
      || juaapi_classnewindex == NULL
      || juaapi_objectindex == NULL
      || juaapi_objectinvoke == NULL
      || juaapi_objsiginvoke == NULL
      || juaapi_objectnewindex == NULL
      || juaapi_arraynew == NULL
      || juaapi_arraylen == NULL
      || juaapi_arrayindex == NULL
      || juaapi_arraynewindex == NULL
      || juaapi_threadnewid == NULL
      || juaapi_luaify == NULL
      || juaapi_import == NULL
      || juaapi_proxy == NULL
      || juaapi_load == NULL) {
    return -1;
  } else {
    return 0;
  }
}

#define LUA_METAFIELD_GC "__gc"
#define LUA_METAFIELD_LEN "__len"
#define LUA_METAFIELD_CALL "__call"
#define LUA_METAFIELD_INDEX "__index"
#define LUA_METAFIELD_NEWINDEX "__newindex"
/**
 * Inits JAVA_CLASS_META_REGISTRY, JAVA_OBJECT_META_REGISTRY
 */
void initMetaRegistry(lua_State * L) {
  if (luaL_newmetatable(L, JAVA_CLASS_META_REGISTRY) == 1) {
    lua_pushcfunction(L, &gc<JAVA_CLASS_META_REGISTRY>);
    lua_setfield(L, -2, LUA_METAFIELD_GC);
    lua_pushcfunction(L, &jclassIndex);
    lua_setfield(L, -2, LUA_METAFIELD_INDEX);
    lua_pushcfunction(L, &jclassNewIndex);
    lua_setfield(L, -2, LUA_METAFIELD_NEWINDEX);
    lua_pushcfunction(L, &jclassCall);
    lua_setfield(L, -2, LUA_METAFIELD_CALL);
  }
  lua_pop(L, 1);

  if (luaL_newmetatable(L, JAVA_OBJECT_META_REGISTRY) == 1) {
    lua_pushcfunction(L, &gc<JAVA_OBJECT_META_REGISTRY>);
    lua_setfield(L, -2, LUA_METAFIELD_GC);
    lua_pushcfunction(L, &jobjectIndex);
    lua_setfield(L, -2, LUA_METAFIELD_INDEX);
    lua_pushcfunction(L, &jobjectNewIndex);
    lua_setfield(L, -2, LUA_METAFIELD_NEWINDEX);
  }
  lua_pop(L, 1);

  if (luaL_newmetatable(L, JAVA_ARRAY_META_REGISTRY) == 1) {
    lua_pushcfunction(L, &gc<JAVA_ARRAY_META_REGISTRY>);
    lua_setfield(L, -2, LUA_METAFIELD_GC);
    lua_pushcfunction(L, &jarrayLength);
    lua_setfield(L, -2, LUA_METAFIELD_LEN);
    lua_pushcfunction(L, &jarrayIndex);
    lua_setfield(L, -2, LUA_METAFIELD_INDEX);
    lua_pushcfunction(L, &jarrayNewIndex);
    lua_setfield(L, -2, LUA_METAFIELD_NEWINDEX);
  }

  if (luaL_newmetatable(L, JAVA_PACKAGE_META_REGISTRY) == 1) {
    /* Lua:
     *   - self: A table, with self[1] pre-filled with an integer,
     *           self[2] pre-filled with a package / class name,
     *           Used cache results.
     *   - name: the inner package / class name
     */
     // TODO: Maybe compile Lua code into C code for better performance for non-JIT Lua
    luaL_dostring(L, RETURN_IMPORT_WRAPPER);
    lua_setfield(L, -2, LUA_METAFIELD_INDEX);
    lua_pushcfunction(L, &javaImport);
    lua_setfield(L, -2, "__import");
    /* jclassNewIndex will always produces an error:
     * we prevent direct writes to table fields
     */
    lua_pushcfunction(L, &jclassNewIndex);
    lua_setfield(L, -2, LUA_METAFIELD_NEWINDEX);
    lua_pushcfunction(L, &jclassNewIndex);
    lua_setfield(L, -2, LUA_METAFIELD_NEWINDEX);
  }

  lua_pop(L, 1);
}

int getMainThreadId(lua_State * L) {
  lua_pushstring(L, JAVA_STATE_INDEX);
  lua_rawget(L, LUA_REGISTRYINDEX);
  int i = lua_tointeger(L, -1);
  lua_pop(L, 1);
  return i;
}

int createNewId(lua_State * L) {
  int mainId = getMainThreadId(L);
  JNIEnv * env = getJNIEnv(L);
  int lid = env->CallStaticIntMethod(juaapi_class, juaapi_threadnewid, mainId, (jlong) L);
  lua_pushthread(L);
  lua_pushinteger(L, lid);
  lua_settable(L, LUA_REGISTRYINDEX);
  return lid;
}

int getStateIndex(lua_State * L) {
  lua_Integer stateIndex;
  if (lua_pushthread(L) == 1) {
    /* Main thread */
    lua_pop(L, 1);
    stateIndex = getMainThreadId(L);
  } else {
    /* Or else use the thread itself as key */
    lua_rawget(L, LUA_REGISTRYINDEX);
    if (lua_isnil(L, -1)) {
      /*
       * Wow, it is a thread created on the Lua side,
       * i.e., no id is assigned yet.
       */
      lua_pop(L, 1);
      stateIndex = createNewId(L);
    } else {
      stateIndex = lua_tointeger(L, -1);
      lua_pop(L, 1);
    }
  }
  return (int) stateIndex;
}

static JavaVM * javaVm = NULL;
static jint jniEnvVersion;

int updateJNIEnv(JNIEnv * env) {
  if (env->GetJavaVM(&javaVm) == 0) {
    jniEnvVersion = env->GetVersion();
    return 0;
  } else {
    return -1;
  }
}

JNIEnv * getJNIEnv(lua_State * L) {
  if (javaVm != NULL) {
    JNIEnv * env;
    int result = javaVm->GetEnv((void **) &env, jniEnvVersion);
    if (result == JNI_OK) {
      return env;
    } else {
      luaL_error(L, "Unable to get JNIEnv pointer: Code %d", result);
      return NULL;
    }
  } else {
    luaL_error(L, "Unable to get JavaVM pointer");
    return NULL;
  }
}

lua_State * luaJ_newthread(lua_State * L, int lid) {
    lua_State * K = lua_newthread(L);
    /* This also prevents the thread from being garbage collected */
    lua_pushthread(K);
    lua_pushinteger(K, lid);
    lua_settable(K, LUA_REGISTRYINDEX);
    return K;
}

void luaJ_pushobject(JNIEnv * env, lua_State * L, jobject obj) {
    jobject global = env->NewGlobalRef(obj);
    if (global != NULL) {
        pushJ<JAVA_OBJECT_META_REGISTRY>(L, global);
    }
}

void luaJ_pushclass(JNIEnv * env, lua_State * L, jobject clazz) {
    jobject global = env->NewGlobalRef(clazz);
    if (global != NULL) {
        pushJ<JAVA_CLASS_META_REGISTRY>(L, global);
    }
}

void luaJ_pusharray(JNIEnv * env, lua_State * L, jobject array) {
    jobject global = env->NewGlobalRef(array);
    if (global != NULL) {
        pushJ<JAVA_ARRAY_META_REGISTRY>(L, global);
    }
}

jobject luaJ_toobject(lua_State * L, int index) {
    void * p = luaL_testudata(L, index, JAVA_OBJECT_META_REGISTRY);
    if (p == NULL) {
        p = luaL_testudata(L, index, JAVA_CLASS_META_REGISTRY);
    }
    if (p == NULL) {
        p = luaL_testudata(L, index, JAVA_ARRAY_META_REGISTRY);
    }
    if (p == NULL) {
        return NULL;
    } else {
        return *((jobject *) p);
    }
}

int luaJ_isobject(lua_State * L, int index) {
    return luaJ_toobject(L, index) != NULL;
}

void luaJ_pushfunction(JNIEnv * env, lua_State * L, jobject func) {
  luaJ_pushobject(env, L, func);
  lua_pushcclosure(L, &jfunctionWrapper, 1);
}

int luaJ_insertloader(lua_State * L, const char * searchers) {
  lua_getglobal(L, "package");
  if (lua_isnil(L, -1)) {
    lua_pop(L, 1);
    return -1;
  }
  lua_getfield(L, -1, searchers);
  if (lua_istable(L, -1) == 0) {
    lua_pop(L, 2);
    return -1;
  }
  int next = luaJ_len(L, -1) + 1;
  lua_pushcfunction(L, &jmoduleLoad);
  lua_rawseti(L, -2, next);
  lua_pop(L, 2);
  return 0;
}