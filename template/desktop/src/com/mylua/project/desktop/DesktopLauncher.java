package com.mylua.project.desktop;

import io.nondev.nonlua.Lua;
import io.nondev.nonlua.thirdparty.DesktopFiles;
import com.mylua.project.MyLuaProject;

public class DesktopLauncher {
    public static void main (String[] arg) {
    	Lua.files = new DesktopFiles();
        new MyLuaProject();
    }
}