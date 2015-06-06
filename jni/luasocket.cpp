#include <luasocket.h>
#include <iostream>

// LuaSocket
extern "C" {
#include <luasocket/luasocket.h>
#include <luasocket/mime.h>
}

// Quick macro for adding functions to 
// the preloder.
#define PRELOAD(name, function) \
	lua_getglobal(L, "package"); \
	lua_getfield(L, -1, "preload"); \
	lua_pushcfunction(L, function); \
	lua_setfield(L, -2, name); \
	lua_pop(L, 2);	

int open_luasocket(lua_State * L)
{
	PRELOAD("socket.core", luaopen_socket_core);
	PRELOAD("mime.core", luaopen_mime_core);
  
	PRELOAD("socket", open_luasocket_socket);
 PRELOAD("socket.headers", open_luasocket_headers);
	PRELOAD("socket.ftp", open_luasocket_ftp)
	PRELOAD("socket.http", open_luasocket_http);
	PRELOAD("ltn12", open_luasocket_ltn12);
	PRELOAD("mime", open_luasocket_mime)
	PRELOAD("socket.smtp", open_luasocket_smtp);
	PRELOAD("socket.tp", open_luasocket_tp)
	PRELOAD("socket.url", open_luasocket_url)
	return 0;
}

int open_luasocket_socket(lua_State * L)
{
	#include <luasocket/socket.lua.h>
	return 1;
}

int open_luasocket_headers(lua_State * L)
{
	#include <luasocket/headers.lua.h>
	return 1;
}

int open_luasocket_ftp(lua_State * L)
{
	#include <luasocket/ftp.lua.h>
	return 1;
}

int open_luasocket_http(lua_State * L)
{
	#include <luasocket/http.lua.h>
	return 1;
}

int open_luasocket_ltn12(lua_State * L)
{
	#include <luasocket/ltn12.lua.h>
	return 1;
}

int open_luasocket_mime(lua_State * L)
{
	#include <luasocket/mime.lua.h>
	return 1;
}

int open_luasocket_smtp(lua_State * L)
{
	#include <luasocket/smtp.lua.h>
	return 1;
}

int open_luasocket_tp(lua_State * L)
{
	#include <luasocket/tp.lua.h>
	return 1;
}

int open_luasocket_url(lua_State * L)
{
	#include <luasocket/url.lua.h>
	return 1;
}