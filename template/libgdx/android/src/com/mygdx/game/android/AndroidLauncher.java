package com.mygdx.game.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import io.nondev.nonlua.Lua;
import io.nondev.nonlua.LuaConfiguration;
import io.nondev.nonlua.LuaLoader;
import com.mygdx.game.MyGdxGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		LuaConfiguration cfg = new LuaConfiguration();

        cfg.loader = new LuaLoader() {
            public String path() {
                return "assets";
            }
        };

		initialize(new MyGdxGame(cfg), config);
	}
}
