package com.mylua.project;

import com.badlogic.gdx.utils.SharedLibraryLoader;
import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

public class MyLuaProject {
    public MyLuaProject() {
        new SharedLibraryLoader().load("luajava");
        LuaState lua = LuaStateFactory.newLuaState();
        
        lua.openLibs();
        lua.LdoString("print(\"Hello World from Lua!\")");
        lua.close();
    }
}