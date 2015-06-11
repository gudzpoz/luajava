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

#include "nonlua.h"
#include "nonlualib.h"

extern "C" {
#include "luasocket.h"
#include "mime.h"
}

#define PRELOAD(name, function) \
  lua_getglobal(L, "package"); \
  lua_getfield(L, -1, "preload"); \
  lua_pushcfunction(L, function); \
  lua_setfield(L, -2, name); \
  lua_pop(L, 2);  

static int socket_socket (lua_State * L) {
  #include "socket.lua.h"
  return 1;
}

static int socket_headers(lua_State * L) {
  #include "headers.lua.h"
  return 1;
}

static int socket_ftp(lua_State * L) {
  #include "ftp.lua.h"
  return 1;
}

static int socket_http(lua_State * L) {
  #include "http.lua.h"
  return 1;
}

static int socket_ltn12(lua_State * L) {
  #include "ltn12.lua.h"
  return 1;
}

static int socket_mime(lua_State * L) {
  #include "mime.lua.h"
  return 1;
}

static int socket_smtp(lua_State * L) {
  #include "smtp.lua.h"
  return 1;
}

static int socket_tp(lua_State * L) {
  #include "tp.lua.h"
  return 1;
}

static int socket_url(lua_State * L) {
  #include "url.lua.h"
  return 1;
}

NONLUA_API int luaopen_luasocket(lua_State * L) {
  PRELOAD("socket.core",    luaopen_socket_core);
  PRELOAD("mime.core",      luaopen_mime_core);
  PRELOAD("socket",         socket_socket);
  PRELOAD("socket.headers", socket_headers);
  PRELOAD("socket.ftp",     socket_ftp)
  PRELOAD("socket.http",    socket_http);
  PRELOAD("ltn12",          socket_ltn12);
  PRELOAD("mime",           socket_mime)
  PRELOAD("socket.smtp",    socket_smtp);
  PRELOAD("socket.tp",      socket_tp)
  PRELOAD("socket.url",     socket_url)
  return 0;
}