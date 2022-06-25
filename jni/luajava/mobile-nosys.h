#ifndef MOBILE_NOSYS_H
#define MOBILE_NOSYS_H

/* Borrowing arch info from LuaJIT */
#include "lj/lj_arch.h"

#define LUA_USE_LONGJMP

#if LJ_NO_SYSTEM

extern "C" {

#include <stdlib.h>

#define system(cmd) (((cmd) == NULL) ? 0 : -1)

}

#endif

#endif /* !MOBILE_NOSYS_H */