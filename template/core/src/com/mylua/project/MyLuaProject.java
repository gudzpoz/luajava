package com.mylua.project;

import io.nondev.nonlua.Lua;
import io.nondev.nonlua.LuaConfiguration;

public class MyLuaProject {
    final Lua L;

    public class Test {
    	public Object[] get(String a, String b) {
    		return new Object[] { a, b };
    	}
    }
    
    public MyLuaProject(LuaConfiguration cfg) {
        L = new Lua(cfg);
        L.push("Hello World from Java!");
        L.set("message");
        L.push(new Test());
        L.set("test");
        L.run("test.lua");
        L.dispose();
    }
}