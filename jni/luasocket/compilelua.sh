#!/bin/bash
luac -o ftp.lua.bin ftp.lua
luac -o headers.lua.bin headers.lua
luac -o http.lua.bin http.lua
luac -o ltn12.lua.bin ltn12.lua
luac -o mime.lua.bin mime.lua
luac -o smtp.lua.bin smtp.lua
luac -o socket.lua.bin socket.lua
luac -o tp.lua.bin tp.lua
luac -o url.lua.bin url.lua