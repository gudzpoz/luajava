#define h_addr h_addr_list[0]
#include "lua.h"
#include "lualib.h"
#include "lauxlib.h"
#include "luasocket.h"
#include "mime.h"

int luasocketw_open(lua_State * L);
int luasocketw_open_luasocket_socket(lua_State * L);
int luasocketw_open_luasocket_ftp(lua_State * L);
int luasocketw_open_luasocket_http(lua_State * L);
int luasocketw_open_luasocket_ltn12(lua_State * L);
int luasocketw_open_luasocket_mime(lua_State * L);
int luasocketw_open_luasocket_smtp(lua_State * L);
int luasocketw_open_luasocket_tp(lua_State * L);
int luasocketw_open_luasocket_url(lua_State * L);