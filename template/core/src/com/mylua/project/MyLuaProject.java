package com.mylua.project;

import io.nondev.nonlua.Lua;

public class MyLuaProject {
    final Lua L;
    
    public MyLuaProject() {
        L = new Lua();
        L.push("Hello World from Java!");
        L.set("message");
        L.run("main.lua");
        L.dispose();
    }
}