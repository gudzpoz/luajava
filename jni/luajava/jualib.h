#ifndef JUALIB_H
#define JUALIB_H

#define LUA_JAVALIBNAME "java"

int javaImport(lua_State * L);

extern const luaL_Reg javalib[];

#endif /* JUALIB_H! */