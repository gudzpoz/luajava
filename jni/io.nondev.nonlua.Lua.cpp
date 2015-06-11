#include <io.nondev.nonlua.Lua.h>

//@line:32

    #include <nonlua.h>
     JNIEXPORT jobject JNICALL Java_io_nondev_nonlua_Lua_jniOpen(JNIEnv* env, jclass clazz, jint stateId) {


//@line:36

        return nonlua_open(env, stateId);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniClose(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:40

        lua_State * L = nonlua_getstate(env, cptr);

        lua_close(L);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenBase(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:46

        lua_State * L = nonlua_getstate(env, cptr);

        luaL_requiref(L, "_G", luaopen_base, 1);
        lua_pop(L, 1);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenCoroutine(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:53

        lua_State * L = nonlua_getstate(env, cptr);
        
        luaL_requiref(L, LUA_COLIBNAME, luaopen_coroutine, 1);
        lua_pop(L, 1);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenDebug(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:60

        lua_State * L = nonlua_getstate(env, cptr);
        
        luaL_requiref(L, LUA_DBLIBNAME, luaopen_debug, 1);
        lua_pop(L, 1);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenIo(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:67

        lua_State * L = nonlua_getstate(env, cptr);
        
        luaL_requiref(L, LUA_IOLIBNAME, luaopen_io, 1);
        lua_pop(L, 1);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenJava(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:74

        lua_State* L = nonlua_getstate(env, cptr);
        
        luaL_requiref(L, LUA_JAVALIBNAME, luaopen_java, 1);
        lua_pop(L, 1);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenMath(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:81

        lua_State * L = nonlua_getstate(env, cptr);
        
        luaL_requiref(L, LUA_MATHLIBNAME, luaopen_math, 1);
        lua_pop(L, 1);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenOs(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:88

        lua_State * L = nonlua_getstate(env, cptr);
        
        luaL_requiref(L, LUA_OSLIBNAME, luaopen_os, 1);
        lua_pop(L, 1);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenPackage(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:95

        lua_State * L = nonlua_getstate(env, cptr);
        
        luaL_requiref(L, LUA_LOADLIBNAME, luaopen_package, 1);
        lua_pop(L, 1);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenSocket(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:102

        lua_State * L = nonlua_getstate(env, cptr);

        luaopen_luasocket(L);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenString(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:108

        lua_State * L = nonlua_getstate(env, cptr);
        
        luaL_requiref(L, LUA_STRLIBNAME, luaopen_string, 1);
        lua_pop(L, 1);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenTable(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:115

        lua_State * L = nonlua_getstate(env, cptr);
        
        luaL_requiref(L, LUA_TABLIBNAME, luaopen_table, 1);
        lua_pop(L, 1);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniOpenUtf8(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:122

        lua_State * L = nonlua_getstate(env, cptr);
        
        luaL_requiref(L, LUA_UTF8LIBNAME, luaopen_utf8, 1);
        lua_pop(L, 1);
    

}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniLoadBuffer
(JNIEnv* env, jclass clazz, jobject cptr, jbyteArray obj_buff, jlong bsize, jstring obj_name, char* name, char* buff) {

//@line:129

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_loadbuffer(L, buff, (int) bsize, name);
    
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

//@line:135

        lua_State * L   = nonlua_getstate(env, cptr);

        return (jint) luaL_loadstring(L, str);
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniLoadString(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_str) {
	char* str = (char*)env->GetStringUTFChars(obj_str, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniLoadString(env, clazz, cptr, obj_str, str);

	env->ReleaseStringUTFChars(obj_str, str);

	return JNI_returnValue;
}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniRunBuffer
(JNIEnv* env, jclass clazz, jobject cptr, jbyteArray obj_buff, jlong bsize, jstring obj_name, char* name, char* buff) {

//@line:142

        lua_State * L = nonlua_getstate(env, cptr);
        
        int ret = luaL_loadbuffer(L, buff, (int) bsize, name);
        int secRet = lua_pcall(L, 0, LUA_MULTRET, 0);

        return (jint) (ret || secRet);
    
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

//@line:151

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_dostring(L, str);
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniRunString(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_str) {
	char* str = (char*)env->GetStringUTFChars(obj_str, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniRunString(env, clazz, cptr, obj_str, str);

	env->ReleaseStringUTFChars(obj_str, str);

	return JNI_returnValue;
}

JNIEXPORT jobject JNICALL Java_io_nondev_nonlua_Lua_jniNewThread(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:157

        lua_State * L = nonlua_getstate(env, cptr);
        lua_State * newThread = lua_newthread(L);
        
        jclass tempClass = env->FindClass("io/nondev/nonlua/CPtr");
        jobject obj = env->AllocObject(tempClass);

        if (obj)
        {
            env->SetLongField(obj, env->GetFieldID(tempClass, "peer", "J"), (jlong) newThread);
        }

        return obj;
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushNil(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:172

        lua_State * L = nonlua_getstate(env, cptr);

        lua_pushnil(L);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushNumber(JNIEnv* env, jclass clazz, jobject cptr, jdouble db) {


//@line:178

        lua_State * L = nonlua_getstate(env, cptr);

        lua_pushnumber(L, (lua_Number) db);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushString(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_str) {
	char* str = (char*)env->GetStringUTFChars(obj_str, 0);


//@line:184

        lua_State * L = nonlua_getstate(env, cptr);

        lua_pushstring(L, str);
    
	env->ReleaseStringUTFChars(obj_str, str);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint val) {


//@line:190

        lua_State * L = nonlua_getstate(env, cptr);

        lua_pushboolean(L, (int) val);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushFunction(JNIEnv* env, jclass clazz, jobject cptr, jobject func) {


//@line:196

        lua_State* L = nonlua_getstate(env, cptr);

        nonlua_pushfunction(L, func);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushObject(JNIEnv* env, jclass clazz, jobject cptr, jobject obj) {


//@line:202

        lua_State * L = nonlua_getstate(env, cptr);

        nonlua_pushobject(L, obj);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushArray(JNIEnv* env, jclass clazz, jobject cptr, jobject obj) {


//@line:208

        lua_State * L = nonlua_getstate(env, cptr);

        nonlua_pusharray(L, obj);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNumber(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:214

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isnumber(L, (int) index);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:220

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isboolean(L, (int) index);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsString(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:226

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isstring(L, (int) index);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsFunction(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:232

        lua_State * L = nonlua_getstate(env, cptr);

        int idx = (jint)index;

        return (jint)(
            lua_isfunction(L, idx) || 
            lua_iscfunction(L, idx) ||
            nonlua_isfunction(L, idx)
       );
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsObject(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:244

        lua_State * L = nonlua_getstate(env, cptr);
        
        return (jint) nonlua_isobject(L, index);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsTable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:250

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_istable(L, (int) index);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsUserdata(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:256

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isuserdata(L, (int) index);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNil(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:262

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isnil(L, (int) index);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniIsNone(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:268

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_isnone(L, (int) index);
    

}

JNIEXPORT jdouble JNICALL Java_io_nondev_nonlua_Lua_jniToNumber(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:274

        lua_State * L = nonlua_getstate(env, cptr);

        return (jdouble) lua_tonumber(L, index);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniToBoolean(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:280

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_toboolean(L, index);
    

}

JNIEXPORT jstring JNICALL Java_io_nondev_nonlua_Lua_jniToString(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:286

        lua_State * L = nonlua_getstate(env, cptr);

        return env->NewStringUTF(lua_tostring(L, index));
    

}

JNIEXPORT jobject JNICALL Java_io_nondev_nonlua_Lua_jniToObject(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:292

        lua_State * L = nonlua_getstate(env, cptr);

        int idx = (int)index;

        if (!nonlua_isobject(L, idx)) {
            return NULL;
        }
        
        jobject * obj = (jobject *)lua_touserdata(L, idx);
        return *obj;
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetGlobal(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:305

        lua_State * L = nonlua_getstate(env, cptr);

        lua_getglobal(L, key);
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetGlobal(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:311

        lua_State * L = nonlua_getstate(env, cptr);

        lua_setglobal(L, key);
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGet(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:317

        lua_State * L = nonlua_getstate(env, cptr);

        lua_getfield(L, (int) index, key);
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSet(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_key) {
	char* key = (char*)env->GetStringUTFChars(obj_key, 0);


//@line:323

        lua_State * L = nonlua_getstate(env, cptr);

        lua_setfield(L, (int) index, key);
    
	env->ReleaseStringUTFChars(obj_key, key);

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetI(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint key) {


//@line:329

        lua_State * L = nonlua_getstate(env, cptr);

        lua_geti(L, (int) index, (int) key);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetI(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint key) {


//@line:335

        lua_State * L = nonlua_getstate(env, cptr);

        lua_seti(L, (int) index, (int) key);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniGetTop(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:341

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_gettop(L);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetTop(JNIEnv* env, jclass clazz, jobject cptr, jint top) {


//@line:347

        lua_State * L = nonlua_getstate(env, cptr);

        lua_settop(L, (int) top);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPop(JNIEnv* env, jclass clazz, jobject cptr, jint num) {


//@line:353

        lua_State * L = nonlua_getstate(env, cptr);

        lua_pop(L, (int) num);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniPushValue(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:359

        lua_State * L = nonlua_getstate(env, cptr);

        lua_pushvalue(L, (int) index);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniCopy(JNIEnv* env, jclass clazz, jobject cptr, jint fromindex, jint toindex) {


//@line:365

        lua_State * L = nonlua_getstate(env, cptr);

        lua_copy(L, (int) fromindex, (int) toindex);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniRemove(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:371

        lua_State * L = nonlua_getstate(env, cptr);

        lua_remove(L, (int) index);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniInsert(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:377

        lua_State * L = nonlua_getstate(env, cptr);

        lua_insert(L, (int) index);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniReplace(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:383

        lua_State * L = nonlua_getstate(env, cptr);

        lua_replace(L, (int) index);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniConcat(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:389

        lua_State * L = nonlua_getstate(env, cptr);

        lua_concat(L, (int) index);
    

}

static inline jstring wrapped_Java_io_nondev_nonlua_Lua_jniGsub
(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_s, jstring obj_p, jstring obj_r, char* s, char* p, char* r) {

//@line:395

        lua_State * L = nonlua_getstate(env, cptr);

        const char * sub = luaL_gsub(L, s, p, r);
        return env->NewStringUTF(sub);
    
}

JNIEXPORT jstring JNICALL Java_io_nondev_nonlua_Lua_jniGsub(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_s, jstring obj_p, jstring obj_r) {
	char* s = (char*)env->GetStringUTFChars(obj_s, 0);
	char* p = (char*)env->GetStringUTFChars(obj_p, 0);
	char* r = (char*)env->GetStringUTFChars(obj_r, 0);

	jstring JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniGsub(env, clazz, cptr, obj_s, obj_p, obj_r, s, p, r);

	env->ReleaseStringUTFChars(obj_s, s);
	env->ReleaseStringUTFChars(obj_p, p);
	env->ReleaseStringUTFChars(obj_r, r);

	return JNI_returnValue;
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniLen(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:402

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_len(L, (int) index);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniCompare(JNIEnv* env, jclass clazz, jobject cptr, jint index1, jint index2, jint op) {


//@line:408

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_compare(L, (int) index1, (int) index2, (int) op);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniNext(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:414

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_next(L, (int) index);
    

}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniError
(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_msg, char* msg) {

//@line:420

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_error(L, msg);
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniError(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_msg) {
	char* msg = (char*)env->GetStringUTFChars(obj_msg, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniError(env, clazz, cptr, obj_msg, msg);

	env->ReleaseStringUTFChars(obj_msg, msg);

	return JNI_returnValue;
}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniWhere(JNIEnv* env, jclass clazz, jobject cptr, jint lvl) {


//@line:426

        lua_State * L = nonlua_getstate(env, cptr);

        luaL_where(L, (int) lvl);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniType(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:432

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_type(L, (int) index);
    

}

JNIEXPORT jstring JNICALL Java_io_nondev_nonlua_Lua_jniTypeName(JNIEnv* env, jclass clazz, jobject cptr, jint type) {


//@line:438

        lua_State * L = nonlua_getstate(env, cptr);
        
        return env->NewStringUTF(lua_typename(L, (int) type));
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniRef(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:444

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_ref(L, (int) index);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniUnRef(JNIEnv* env, jclass clazz, jobject cptr, jint index, jint ref) {


//@line:450

        lua_State * L = nonlua_getstate(env, cptr);

        luaL_unref(L, (int) index, (int) ref);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniCall(JNIEnv* env, jclass clazz, jobject cptr, jint nArgs, jint nResults) {


//@line:456

        lua_State * L = nonlua_getstate(env, cptr);

        lua_call(L, (int) nArgs, (int) nResults);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniPcall(JNIEnv* env, jclass clazz, jobject cptr, jint nArgs, jint nResults, jint errFunc) {


//@line:462

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_pcall(L, (int) nArgs, (int) nResults, (int) errFunc);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniNewTable(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:468

        lua_State * L = nonlua_getstate(env, cptr);

        lua_newtable(L);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetTable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:474

        lua_State * L = nonlua_getstate(env, cptr);

        lua_gettable(L, (int) index);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniSetTable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:480

        lua_State * L = nonlua_getstate(env, cptr);

        lua_settable(L, (int) index);
    

}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniNewMetatable
(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_name, char* name) {

//@line:486

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_newmetatable(L, name);
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniNewMetatable(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_name) {
	char* name = (char*)env->GetStringUTFChars(obj_name, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniNewMetatable(env, clazz, cptr, obj_name, name);

	env->ReleaseStringUTFChars(obj_name, name);

	return JNI_returnValue;
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniGetMetatable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:492

        lua_State * L = nonlua_getstate(env, cptr);
        
        return (jint) lua_getmetatable(L, (int) index);
    

}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniGetMetatableStr(JNIEnv* env, jclass clazz, jobject cptr, jstring obj_name) {
	char* name = (char*)env->GetStringUTFChars(obj_name, 0);


//@line:498

        lua_State * L = nonlua_getstate(env, cptr);
        
        luaL_getmetatable(L, name);
    
	env->ReleaseStringUTFChars(obj_name, name);

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniSetMetatable(JNIEnv* env, jclass clazz, jobject cptr, jint index) {


//@line:504

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_setmetatable(L, (int) index);
    

}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniCallmeta
(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_field, char* field) {

//@line:510

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_callmeta(L, (int) index, field);
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniCallmeta(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_field) {
	char* field = (char*)env->GetStringUTFChars(obj_field, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniCallmeta(env, clazz, cptr, index, obj_field, field);

	env->ReleaseStringUTFChars(obj_field, field);

	return JNI_returnValue;
}

static inline jint wrapped_Java_io_nondev_nonlua_Lua_jniGetmeta
(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_field, char* field) {

//@line:516

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) luaL_getmetafield(L, (int) index, field);
    
}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniGetmeta(JNIEnv* env, jclass clazz, jobject cptr, jint index, jstring obj_field) {
	char* field = (char*)env->GetStringUTFChars(obj_field, 0);

	jint JNI_returnValue = wrapped_Java_io_nondev_nonlua_Lua_jniGetmeta(env, clazz, cptr, index, obj_field, field);

	env->ReleaseStringUTFChars(obj_field, field);

	return JNI_returnValue;
}

JNIEXPORT void JNICALL Java_io_nondev_nonlua_Lua_jniMove(JNIEnv* env, jclass clazz, jobject cptr, jobject to, jint index) {


//@line:522

        lua_State * fr = nonlua_getstate(env, cptr);
        lua_State * t  = nonlua_getstate(env, to);

        lua_xmove(fr, t, (int) index);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniYield(JNIEnv* env, jclass clazz, jobject cptr, jint nResults) {


//@line:529

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_yield(L, (int) nResults);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniResume(JNIEnv* env, jclass clazz, jobject cptr, jobject from, jint nArgs) {


//@line:535

        lua_State * L = nonlua_getstate(env, cptr);
        lua_State * fr = nonlua_getstate(env, from);

        return (jint) lua_resume(L, fr, (int) nArgs);
    

}

JNIEXPORT jint JNICALL Java_io_nondev_nonlua_Lua_jniStatus(JNIEnv* env, jclass clazz, jobject cptr) {


//@line:542

        lua_State * L = nonlua_getstate(env, cptr);

        return (jint) lua_status(L);
    

}

