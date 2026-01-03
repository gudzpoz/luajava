#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <lua.h>
#include <lauxlib.h>
#include <lualib.h>

#define ITERATIONS 1000000

// Get current time in nanoseconds
static long long now_ns() {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return (long long)ts.tv_sec * 1000000000LL + ts.tv_nsec;
}

int main() {
    lua_State *L = luaL_newstate();
    if (L == NULL) {
        fprintf(stderr, "Failed to create Lua state\n");
        return 1;
    }

    luaL_openlibs(L);

    // Define the empty function like in Java benchmark
    if (luaL_dostring(L, "function pure() end")) {
        fprintf(stderr, "Failed to define pure function: %s\n", lua_tostring(L, -1));
        lua_close(L);
        return 1;
    }

    printf("Running benchmark with %d iterations...\n", ITERATIONS);

    // Warmup
    for (int i = 0; i < 10000; i++) {
        lua_getglobal(L, "pure");
        lua_pcall(L, 0, 0, 0);
    }

    // Benchmark
    long long start = now_ns();
    for (int i = 0; i < ITERATIONS; i++) {
        lua_getglobal(L, "pure");
        lua_pcall(L, 0, 0, 0);
    }
    long long end = now_ns();

    long long total_ns = end - start;
    double ns_per_op = (double)total_ns / ITERATIONS;

    printf("\nResults:\n");
    printf("Total time: %.2f ms\n", total_ns / 1000000.0);
    printf("Time per operation: %.3f ns/op\n", ns_per_op);
    printf("Operations per second: %.2f M ops/sec\n", 1000.0 / ns_per_op);

    lua_close(L);
    return 0;
}
