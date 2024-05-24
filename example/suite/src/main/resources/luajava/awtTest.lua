--[[

Copyright (C) 2003-2007 Kepler Project.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

]]--

local module = function()
  local frame = java.import("java.awt.Frame")("Lua Java Console")
  local console = java.import("java.awt.TextArea")()
  local buttonsPanel = java.import("java.awt.Panel")()
  local executeButton = java.import("java.awt.Button")("Execute")
  local clearButton = java.import("java.awt.Button")("Clear")
  local exitButton = java.import("java.awt.Button")("Exit")

  frame:setSize(600, 300)

  buttonsPanel:add(executeButton)
  buttonsPanel:add(clearButton)
  buttonsPanel:add(exitButton)

  local BorderLayout = java.import("java.awt.BorderLayout")

  frame:add(BorderLayout.NORTH, console)
  frame:add(BorderLayout.SOUTH, buttonsPanel)
  frame:pack()
  frame:show()

  --
  -- Listeners
  --

  local luaload = nil
  if loadstring ~= nil then
    luaload = loadstring -- Lua 5.1
  else
    luaload = load -- Lua 5.2 and on
  end

  executeButton:addActionListener(function(this, ev)
    print("execute")
    pcall(luaload(console:getText()))
  end)

  clearButton:addActionListener(function (this, ev)
    print("clear");
    console:setText("");
  end)

  exitButton:addActionListener(function (this, ev)
    print("exit")
    frame:setVisible(false)
    frame:dispose()
  end)

  local closeCallback = {
    windowClosing = function (this, ev)
      print("close")
      frame:setVisible(false)
      frame:dispose()
    end,
    windowIconified = function (this, ev)
      print('iconified')
    end
  }

  setmetatable(closeCallback, {
    __index = function(t, name)
      local func = rawget(t, name)
      if func ~= nil then
        return func
      else
        return function() print(name) end
      end
    end
  })

  function closeCallback.windowActivated(this, ev)
    print("act")
  end

  frame:addWindowListener(closeCallback)
end

return module
