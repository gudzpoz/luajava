package io.nondev.nonlua;

public interface ExternalLoader {
    int load(String path, Lua L);
}
