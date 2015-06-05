#include <stdlib.h>
#include <string.h>

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