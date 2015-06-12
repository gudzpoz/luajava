package com.mylua.project;

import io.nondev.nonlua.Lua;
import io.nondev.nonlua.LuaConfiguration;

public class MyLuaProject {
    final Lua L;
    
    public MyLuaProject(LuaConfiguration cfg) {
        L = new Lua(cfg);
        L.push("Hello World from Java!");
        L.set("message");
        L.run("main.lua");
        L.dispose();
    }
}