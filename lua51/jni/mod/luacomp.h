#ifndef LUACOMP_H
#define LUACOMP_H

#include "lua.hpp"

#include <cstring>

/**
 * Opens individual libraries when one does not want them all
 */
static inline void luaJ_openlib(lua_State * L, const char *libName, lua_CFunction loader) {
  lua_pushcfunction(L, loader);
  lua_pushstring(L, libName);
  lua_call(L, 1, 0);
}

static void luaL_setmetatable(lua_State * L, const char * tname) {
    luaL_getmetatable(L, tname);
    lua_setmetatable(L, -2);
}

luaL_Reg allAvailableLibs[] = {
    { "",        luaopen_base },
    { "package", luaopen_package },
    { "string",  luaopen_string },
    { "table",   luaopen_table },
    { "math",    luaopen_math },
    { "io",      luaopen_io },
    { "os",      luaopen_os },
    { "debug",   luaopen_debug },
    { NULL,      NULL },
};

static void luaJ_openlib_comp(lua_State * L, const char * libName) {
    const luaL_Reg *lib = allAvailableLibs;
    for (; lib->func != NULL; lib++) {
        if (std::strcmp(lib->name, libName) == 0) {
            luaJ_openlib(L, lib->name, lib->func);
            return;
        }
    }
}

#endif /* !LUACOMP_H */