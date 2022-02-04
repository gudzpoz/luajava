#ifndef LUAEXCEPTION_H
#define LUAEXCEPTION_H

#include <stdexcept>
#include <string>

class LuaException: public std::runtime_error {
public:
  explicit LuaException(const char * message);
  explicit LuaException(const std::string& message);
};

#endif /* LUAEXCEPTION_H! */