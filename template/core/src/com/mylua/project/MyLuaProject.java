package com.mylua.project;

import io.nondev.nonlua.Lua;
import io.nondev.nonfilesystem.FileSystem;

public class MyLuaProject {
    final Lua L;
    
    public MyLuaProject(FileSystem fs) {
        L = new Lua(fs);
        L.push("Hello World from Java!");
        L.set("message");
        L.run("main.lua");
        L.dispose();
    }
}