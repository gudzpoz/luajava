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

#include "socket.lua.h"
#include "headers.lua.h"
#include "ftp.lua.h"
#include "http.lua.h"
#include "ltn12.lua.h"
#include "mime.lua.h"
#include "smtp.lua.h"
#include "tp.lua.h"
#include "url.lua.h"

NONLUA_API int luaopen_socket (lua_State * L) {
  luaL_loadbuffer(L, (const char*)luaJIT_BC_socket, luaJIT_BC_socket_SIZE, "socket.lua");
  lua_call(L, 0, 1);
  return 1;
}

NONLUA_API int luaopen_socket_headers(lua_State * L) {
  luaL_loadbuffer(L, (const char*)luaJIT_BC_headers, luaJIT_BC_headers_SIZE, "headers.lua");
  lua_call(L, 0, 1);
  return 1;
}

NONLUA_API int luaopen_socket_ftp(lua_State * L) {
  luaL_loadbuffer(L, (const char*)luaJIT_BC_ftp, luaJIT_BC_ftp_SIZE, "ftp.lua");
  lua_call(L, 0, 1);
  return 1;
}

NONLUA_API int luaopen_socket_http(lua_State * L) {
  luaL_loadbuffer(L, (const char*)luaJIT_BC_http, luaJIT_BC_http_SIZE, "http.lua");
  lua_call(L, 0, 1);
  return 1;
}

NONLUA_API int luaopen_ltn12(lua_State * L) {
  luaL_loadbuffer(L, (const char*)luaJIT_BC_ltn12, luaJIT_BC_ltn12_SIZE, "ltn12.lua");
  lua_call(L, 0, 1);
  return 1;
}

NONLUA_API int luaopen_mime(lua_State * L) {
  luaL_loadbuffer(L, (const char*)luaJIT_BC_mime, luaJIT_BC_mime_SIZE, "mime.lua");
  lua_call(L, 0, 1);
  return 1;
}

NONLUA_API int luaopen_socket_smtp(lua_State * L) {
  luaL_loadbuffer(L, (const char*)luaJIT_BC_smtp, luaJIT_BC_smtp_SIZE, "smtp.lua");
  lua_call(L, 0, 1);
  return 1;
}

NONLUA_API int luaopen_socket_tp(lua_State * L) {
  luaL_loadbuffer(L, (const char*)luaJIT_BC_tp, luaJIT_BC_tp_SIZE, "tp.lua");
  lua_call(L, 0, 1);
  return 1;
}

NONLUA_API int luaopen_socket_url(lua_State * L) {
  luaL_loadbuffer(L, (const char*)luaJIT_BC_url, luaJIT_BC_url_SIZE, "url.lua");
  lua_call(L, 0, 1);
  return 1;
}

NONLUA_API int nonluaopen_socket(lua_State * L) {
  PRELOAD("socket.core", luaopen_socket_core);
  PRELOAD("mime.core", luaopen_mime_core);
  PRELOAD("socket", luaopen_socket);
  PRELOAD("socket.headers", luaopen_socket_headers);
  PRELOAD("socket.url", luaopen_socket_url);
  PRELOAD("ltn12", luaopen_ltn12);
  PRELOAD("socket.tp", luaopen_socket_tp);
  PRELOAD("socket.ftp", luaopen_socket_ftp);
  PRELOAD("mime", luaopen_mime);
  PRELOAD("socket.http", luaopen_socket_http);
  PRELOAD("socket.smtp", luaopen_socket_smtp);
  
  return 0;
}