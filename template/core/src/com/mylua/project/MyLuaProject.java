package com.mylua.project;

import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

public class MyLuaProject {
    public MyLuaProject() {
        LuaState lua = LuaStateFactory.newLuaState();
        
        lua.openLibs();
        lua.LdoString("print(\"Hello World from Lua!\")");
        lua.close();
    }
}