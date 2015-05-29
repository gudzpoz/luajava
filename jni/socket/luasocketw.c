#include "lua.h"
#include "luasocketw.h"

#define PRELOAD(name, function) \
	lua_getglobal(L, "package"); \
	lua_getfield(L, -1, "preload"); \
	lua_pushcfunction(L, function); \
	lua_setfield(L, -2, name); \
	lua_pop(L, 2);	

int luasocketw_open(lua_State * L)
{
	PRELOAD("socket.core", luaopen_socket_core);
	PRELOAD("mime.core", luaopen_mime_core);

	PRELOAD("socket", luasocketw_open_luasocket_socket);
	PRELOAD("socket.ftp", luasocketw_open_luasocket_ftp)
	PRELOAD("socket.http", luasocketw_open_luasocket_http);
	PRELOAD("ltn12", luasocketw_open_luasocket_ltn12);
	PRELOAD("mime", luasocketw_open_luasocket_mime)
	PRELOAD("socket.smtp", luasocketw_open_luasocket_smtp);
	PRELOAD("socket.tp", luasocketw_open_luasocket_tp)
	PRELOAD("socket.url", luasocketw_open_luasocket_url)
	return 0;
}

int luasocketw_open_luasocket_socket(lua_State * L)
{
	#include "socket.lua.h"
	lua_getglobal(L, "socket");
	return 1;
}

int luasocketw_open_luasocket_ftp(lua_State * L)
{
	#include "ftp.lua.h"
	lua_getglobal(L, "socket.ftp");
	return 1;
}

int luasocketw_open_luasocket_http(lua_State * L)
{
	#include "http.lua.h"
	lua_getglobal(L, "socket.http");
	return 1;
}

int luasocketw_open_luasocket_ltn12(lua_State * L)
{
	#include "ltn12.lua.h"
	lua_getglobal(L, "ltn12");
	return 1;
}

int luasocketw_open_luasocket_mime(lua_State * L)
{
	#include "mime.lua.h"
	lua_getglobal(L, "mime");
	return 1;
}

int luasocketw_open_luasocket_smtp(lua_State * L)
{
	#include "smtp.lua.h"
	lua_getglobal(L, "socket.smtp");
	return 1;
}

int luasocketw_open_luasocket_tp(lua_State * L)
{
	#include "tp.lua.h"
	lua_getglobal(L, "socket.tp");
	return 1;
}

int luasocketw_open_luasocket_url(lua_State * L)
{
	#include "url.lua.h"
	lua_getglobal(L, "socket.url");
	return 1;
}