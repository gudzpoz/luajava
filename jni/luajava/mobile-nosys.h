#ifndef MOBILE_NOSYS_H
#define MOBILE_NOSYS_H

/* Workaround before LuaJIT supports RISC-V */
#if (defined(__riscv) || defined(__riscv__)) && __riscv_xlen == 64
#define LJ_TARGET_RISCV64 1
#else
/* Borrowing arch info from LuaJIT */
#include "lj/lj_arch.h"
#endif

/* Disabling exception usage */
#define LUA_USE_LONGJMP

/* Disabling "system" calls for iOS builds */
#if LJ_NO_SYSTEM
extern "C" {
#include <stdlib.h>

#define system(cmd) (((cmd) == NULL) ? 0 : -1)

}
#endif

/* Using versioned Glibc symbols on Linux */
#if LJ_TARGET_LINUX && !defined(LUAJIT_NO_LOG2) && !defined(LJ_NO_SYSTEM)

/*
 * The following symbols are extracted with the shell command:
 * nm --dynamic --with-symbol-versions lua5*\/libs/linux*\/*.so \
 * | grep GLIBC | grep --invert-match <common versions>
 *
 * This does not work for LuaJIT, which uses an independent build routine.
 */

#if LJ_TARGET_ARM64
__asm__(".symver exp,exp@GLIBC_2.17");
__asm__(".symver log,log@GLIBC_2.17");
__asm__(".symver log2,log2@GLIBC_2.17");
__asm__(".symver pow,pow@GLIBC_2.17");
#elif LJ_TARGET_ARM
__asm__(".symver exp,exp@GLIBC_2.4");
__asm__(".symver log,log@GLIBC_2.4");
__asm__(".symver log2,log2@GLIBC_2.4");
__asm__(".symver pow,pow@GLIBC_2.4");
#elif LJ_64
__asm__(".symver exp,exp@GLIBC_2.2.5");
__asm__(".symver log,log@GLIBC_2.2.5");
__asm__(".symver log2,log2@GLIBC_2.2.5");
__asm__(".symver pow,pow@GLIBC_2.2.5");
#elif LJ_32
__asm__(".symver exp,exp@GLIBC_2.0");
__asm__(".symver log,log@GLIBC_2.0");
__asm__(".symver log2,log2@GLIBC_2.1");
__asm__(".symver pow,pow@GLIBC_2.0");
#endif

#endif

#endif /* !MOBILE_NOSYS_H */
