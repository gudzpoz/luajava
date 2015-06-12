package com.mygdx.game;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.foundation.NSBundle;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import io.nondev.nonlua.Lua;
import io.nondev.nonlua.LuaConfiguration;
import io.nondev.nonlua.LuaLoader;
import com.mygdx.game.MyGdxGame;

public class IOSLauncher extends IOSApplication.Delegate {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        LuaConfiguration cfg = new LuaConfiguration();

        cfg.loader = new LuaLoader() {
            public String path() {
                return NSBundle.getMainBundle().getBundlePath();
            }
        };

        return new IOSApplication(new MyGdxGame(cfg), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}