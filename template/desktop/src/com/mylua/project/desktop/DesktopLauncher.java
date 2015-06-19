package com.mylua.project.desktop;

import io.nondev.nonlua.Lua;
import io.nondev.nonfilesystem.DesktopFileSystem;
import com.mylua.project.MyLuaProject;

public class DesktopLauncher {
    public static void main (String[] arg) {
        new MyLuaProject(new DesktopFileSystem());
    }
}