#ifndef LUA_CUSTOM_AMALG_H
#define LUA_CUSTOM_AMALG_H

#include "mobile-nosys.h"

extern "C" {

#define MAKE_LIB
#include "onelua.c"

}

#endif /* !LUA_CUSTOM_AMALG_H */