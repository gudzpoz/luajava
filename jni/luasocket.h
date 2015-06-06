#ifdef __cplusplus
extern "C" {
#endif
#include <lua/lua.h>
#include <lua/lualib.h>
#include <lua/lauxlib.h>
#ifdef __cplusplus
}
#endif

#ifndef LUASOCKET_LUASOCKET_H
#define LUASOCKET_LUASOCKET_H

int open_luasocket(lua_State * L);

// Loaders for all lua files. We want to be able
// to load these dynamically. (Identical in the LuaSocket 
// documentation. These functions wrap the parsing of code, etc).
int open_luasocket_socket(lua_State * L);
int open_luasocket_headers(lua_State * L);
int open_luasocket_ftp(lua_State * L);
int open_luasocket_http(lua_State * L);
int open_luasocket_ltn12(lua_State * L);
int open_luasocket_mime(lua_State * L);
int open_luasocket_smtp(lua_State * L);
int open_luasocket_tp(lua_State * L);
int open_luasocket_url(lua_State * L);

#endif