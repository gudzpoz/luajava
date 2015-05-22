package com.mylua.project;

import io.nondev.nonlua.Lua;

public class MyLuaProject {
    public MyLuaProject() {
        this(false);
    }
    
    public MyLuaProject(boolean isAndroid) {
        Lua.open(isAndroid);
        Lua.run("print(\"Hello World from Lua!\")");
        Lua.close();
    }
}