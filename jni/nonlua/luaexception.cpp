#include "luaexception.h"

LuaException::LuaException(const char * message) : std::runtime_error(message) {}
LuaException::LuaException(const std::string& message) : std::runtime_error(message) {};
