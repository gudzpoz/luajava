package com.mylua.project;

import io.nondev.nonlua.Lua;

public class MyLuaProject {
    final Lua L;
    
    public MyLuaProject() {
        L = new Lua();
        L.run("print(\"Hello World from Lua!\")");
        L.dispose();
    }
}