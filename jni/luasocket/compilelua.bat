@if "%DEBUG%" == "" @echo off
luac -o ftp.lua.bin ftp.lua
luac -o headers.lua.bin headers.lua
luac -o http.lua.bin http.lua
luac -o ltn12.lua.bin ltn12.lua
luac -o mime.lua.bin mime.lua
luac -o smtp.lua.bin smtp.lua
luac -o socket.lua.bin socket.lua
luac -o tp.lua.bin tp.lua
luac -o url.lua.bin url.lua

bin2c -o ftp.lua.h ftp.lua.bin
bin2c -o headers.lua.h headers.lua.bin
bin2c -o http.lua.h http.lua.bin
bin2c -o ltn12.lua.h ltn12.lua.bin
bin2c -o mime.lua.h mime.lua.bin
bin2c -o smtp.lua.h smtp.lua.bin
bin2c -o socket.lua.h socket.lua.bin
bin2c -o tp.lua.h tp.lua.bin
bin2c -o url.lua.h url.lua.bin