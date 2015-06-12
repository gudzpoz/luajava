local Gdx = java.require("com.badlogic.gdx.Gdx")
local GL20 = java.require("com.badlogic.gdx.graphics.GL20")
local SpriteBatch = java.require("com.badlogic.gdx.graphics.g2d.SpriteBatch")
local Texture = java.require("com.badlogic.gdx.graphics.Texture")

local batch, img

function create()
	batch = java.new(SpriteBatch)
	img = java.new(Texture, "badlogic.jpg")
end

function render()
	Gdx.gl:glClearColor(1, 0, 0, 1)
	Gdx.gl:glClear(GL20.GL_COLOR_BUFFER_BIT)

	batch:begin()
	batch:draw(img, 0, 0)
	batch:end()
end

function dispose()
	batch:dispose()
	img:dispose()
end