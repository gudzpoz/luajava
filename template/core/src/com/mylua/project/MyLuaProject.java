package com.mylua.project;

import io.nondev.nonlua.Lua;

public class MyLuaProject {
    final Lua L;
    
    public MyLuaProject() {
        L = new Lua();
        L.push("Hello World from Lua!");
        L.set("message");
        L.run("print(message)");
        L.dispose();
    }
}