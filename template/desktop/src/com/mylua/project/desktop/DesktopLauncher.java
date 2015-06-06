package com.mylua.project.desktop;

import io.nondev.nonlua.LuaConfiguration;
import com.mylua.project.MyLuaProject;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LuaConfiguration cfg = new LuaConfiguration();
        new MyLuaProject(cfg);
    }
}