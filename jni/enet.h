#include <stdlib.h>
#include <string.h>

#define LUA_COMPAT_ALL
#define LUA_COMPAT_MODULE
#define HAS_SOCKLEN_T

#ifdef __cplusplus
extern "C" {
#endif
#include <lua/lua.h>
#include <lua/lualib.h>
#include <lua/lauxlib.h>
#include <enet/enet.h>
int luaopen_enet( lua_State * L );
#ifdef __cplusplus
}
#endif