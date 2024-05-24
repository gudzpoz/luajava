#include "lua.hpp"

/* Manually rewritten from the commented-out Lua code */
int jpackageImport(lua_State * L) {
  /*
  if type(self) ~= 'table' then
    error(\"bad argument #1 to 'java.import.?': expecting a table\")
  end
  if type(name) ~= 'string' then
    error(\"bad argument #2 to 'java.import.?': expecting a valid string\")
  end
   */
  // Stack pos: 1: self
  luaL_checktype(L, 1, LUA_TTABLE);
  // Stack pos: 2: name
  luaL_checktype(L, 2, LUA_TSTRING);
  lua_settop(L, 2);
  lua_checkstack(L, 4);

  /*
  local value = rawget(self, name)
  if value ~= nil then
    return value
   */
  lua_pushvalue(L, 2);
  // Stack pos: 3: rawget(self, name)
  lua_rawget(L, 1);
  if (!lua_isnil(L, 3)) {
    return 1;
  }

  lua_pop(L, 1);

  /*
  else
    local depth = rawget(self, 1)
    local meta = getmetatable(self)
    local current = rawget(self, 2)
    local v = nil
    local found = false
   */

  lua_rawgeti(L, 1, 1);
  int depth = lua_tointeger(L, 3);
  lua_pop(L, 1);

  // Stack pos: 3: meta = metatable(self)
  lua_getmetatable(L, 1);

  lua_rawgeti(L, 1, 2);
  lua_pushvalue(L, 2);
  // Stack pos: 4: current .. name
  lua_concat(L, 2);

  bool found = false;

  /*
    if depth == 1 then
      depth = 2
      found, v = pcall(rawget(meta, '__import'), current .. name)
    end
   */
  if (depth == 1) {
    depth = 2;
    // Stack pos: 5: '__import'
    lua_pushliteral(L, "__import");
    // Stack pos: 5: rawget(meta, '__import')
    lua_rawget(L, 3);
    // Stack pos: 6: current .. name
    lua_pushvalue(L, 4);
    // Stack pos: 5? v = pcall(rawget(meta, '__import'), current .. name)
    found = (lua_pcall(L, 1, 1, 0) == 0);
  }

  /*
    if not found then
      v = {
        [1] = depth - 1,
        [2] = current .. name .. '.'
      }
      setmetatable(v, meta)
    end
   */
  if (!found) {
    lua_settop(L, 4);
    
    // Stack pos: 4: current .. name .. '.'
    lua_pushliteral(L, ".");
    lua_concat(L, 2);
    
    // Stack pos: 3: v
    // Stack pos: 4: meta
    // Stack pos: 5: current .. name .. '.'
    lua_createtable(L, 2, 0);
    lua_insert(L, 3);
    
    lua_pushinteger(L, depth - 1);
    lua_rawseti(L, 3, 1);
    lua_rawseti(L, 3, 2);
    lua_setmetatable(L, 3);
    // Stack top: 3: v
  }
  lua_pushvalue(L, 2);
  lua_pushvalue(L, -2);
  // Stack pos: -3: v
  // Stack pos: -2: name
  // Stack pos: -1: v
  
  lua_rawset(L, 1);
  // Stack pos: -1: v
  return 1;
}
