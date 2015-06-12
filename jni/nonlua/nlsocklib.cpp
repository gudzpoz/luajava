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

#define PRELOAD(name) \
  lua_getglobal(L, "package"); \
  lua_getfield(L, -1, "loaded"); \
  lua_pushvalue(L, -3); \
  lua_setfield(L, -2, name); \
  lua_pushvalue(L, -3); \
  lua_setglobal(L, name); \
  lua_pop(L, 2);

static int socket_socket (lua_State * L) {
  #include "socket.lua.h"
  luaL_loadbuffer(L, (const char*)socket_lua, socket_lua_len, "socket.lua");
  lua_call(L, 0, 1);
  return 1;
}

static int socket_headers(lua_State * L) {
  #include "headers.lua.h"
  luaL_loadbuffer(L, (const char*)headers_lua, headers_lua_len, "headers.lua");
  lua_call(L, 0, 1);
  return 1;
}

static int socket_ftp(lua_State * L) {
  #include "ftp.lua.h"
  luaL_loadbuffer(L, (const char*)ftp_lua, ftp_lua_len, "ftp.lua");
  lua_call(L, 0, 1);
  return 1;
}

static int socket_http(lua_State * L) {
  #include "http.lua.h"
  luaL_loadbuffer(L, (const char*)http_lua, http_lua_len, "http.lua");
  lua_call(L, 0, 1);
  return 1;
}

static int socket_ltn12(lua_State * L) {
  #include "ltn12.lua.h"
  luaL_loadbuffer(L, (const char*)ltn12_lua, ltn12_lua_len, "ltn12.lua");
  lua_call(L, 0, 1);
  return 1;
}

static int socket_mime(lua_State * L) {
  #include "mime.lua.h"
  luaL_loadbuffer(L, (const char*)mime_lua, mime_lua_len, "mime.lua");
  lua_call(L, 0, 1);
  return 1;
}

static int socket_smtp(lua_State * L) {
  #include "smtp.lua.h"
  luaL_loadbuffer(L, (const char*)smtp_lua, smtp_lua_len, "smtp.lua");
  lua_call(L, 0, 1);
  return 1;
}

static int socket_tp(lua_State * L) {
  #include "tp.lua.h"
  luaL_loadbuffer(L, (const char*)tp_lua, tp_lua_len, "tp.lua");
  lua_call(L, 0, 1);
  return 1;
}

static int socket_url(lua_State * L) {
  #include "url.lua.h"
  luaL_loadbuffer(L, (const char*)url_lua, url_lua_len, "url.lua");
  lua_call(L, 0, 1);
  return 1;
}

NONLUA_API int luaopen_luasocket(lua_State * L) {
  luaopen_socket_core(L);
  PRELOAD("socket.core");

  luaopen_mime_core(L);
  PRELOAD("mime.core");

  socket_socket(L);
  PRELOAD("socket");

  socket_headers(L);
  PRELOAD("socket.headers");

  socket_url(L);
  PRELOAD("socket.url");

  socket_ltn12(L);
  PRELOAD("ltn12");

  socket_tp(L);
  PRELOAD("socket.tp");

  socket_ftp(L);
  PRELOAD("socket.ftp");

  socket_mime(L);
  PRELOAD("mime");

  socket_http(L);
  PRELOAD("socket.http");

  socket_smtp(L);
  PRELOAD("socket.smtp");
  
  return 0;
}