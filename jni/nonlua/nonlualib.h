/*******************************************************************************
 * Copyright (c) 2015 Thomas Slusny.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

#ifndef nonlualib_h
#define nonlualib_h

#include "nonlua.h"

#define LUA_JAVALIBNAME	"java"
 
NONLUA_API int luaopen_java (lua_State *L);

NONLUA_API int nonluaopen_socket (lua_State *L);

NONLUA_API int luaopen_socket (lua_State * L);

NONLUA_API int luaopen_socket_headers(lua_State * L);

NONLUA_API int luaopen_socket_ftp(lua_State * L);

NONLUA_API int luaopen_socket_http(lua_State * L);

NONLUA_API int luaopen_ltn12(lua_State * L);

NONLUA_API int luaopen_mime(lua_State * L);

NONLUA_API int luaopen_socket_smtp(lua_State * L);

NONLUA_API int luaopen_socket_tp(lua_State * L);

NONLUA_API int luaopen_socket_url(lua_State * L);

#endif