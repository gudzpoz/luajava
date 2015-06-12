package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import io.nondev.nonlua.Lua;
import io.nondev.nonlua.LuaConfiguration;
import io.nondev.nonlua.LuaLoader;
import io.nondev.nonlua.LuaLogger;

public class MyGdxGame implements ApplicationListener {
	final Lua L;

	public MyGdxGame(LuaConfiguration cfg) {
		super();

		cfg.logger = new LuaLogger() {
			public void log(String msg) {
				Gdx.app.log("Lua", msg);
			}
		};

		L = new Lua(cfg);
		L.run("main.lua");
	}

	@Override
	public void create () {
		runCallback("create");
	}

	@Override
	public void resize (int width, int height) {
		runCallback("resize", Integer.valueOf(width), Integer.valueOf(height));
	}

	@Override
	public void render () {
		runCallback("render");
	}

	@Override
	public void pause () {
		runCallback("pause");
	}

	@Override
	public void resume () {
		runCallback("resume");
	}

	@Override
	public void dispose () {
		runCallback("dispose");
		L.dispose();
	}

	private void runCallback(String callback, Object... params) {
		L.get(callback);

		if (!L.isFunction(-1)) {
			L.pop(1);
			return;
		}

		for(Object param : params) {
			L.push(param);
		}
		
		if (L.pcall(params.length, 0) != 0) {
			Gdx.app.log("Lua", L.toString(-1));
			L.pop(1);
		}

		L.pop(params.length);
	}
}
