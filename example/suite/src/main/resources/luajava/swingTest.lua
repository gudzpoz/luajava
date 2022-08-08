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
  local frame = java.import("javax.swing.JFrame")("Lua Java Console")
  local console = java.import("javax.swing.JTextArea")()
  local buttons_pn = java.import("javax.swing.JPanel")()
  local execute_bt = java.import("javax.swing.JButton")("Execute")
  local clear_bt = java.import("javax.swing.JButton")("Clear")
  local exit_bt = java.import("javax.swing.JButton")("Exit")

  console:setSize(600, 300)

  buttons_pn:add(execute_bt)
  buttons_pn:add(clear_bt)
  buttons_pn:add(exit_bt)

  local BorderLayout = java.import("java.awt.BorderLayout")

  frame:setDefaultCloseOperation(frame.EXIT_ON_CLOSE);

  frame:getContentPane():add(console, BorderLayout.NORTH);
  frame:getContentPane():add(buttons_pn, BorderLayout.SOUTH);
  frame:pack()
  frame:show()

  --
  -- Listeners
  --

  local luaload = nil
  if loadstring ~= nil then
    luaload = loadstring
  else
    luaload = load
  end

  execute_bt:addActionListener(function(ev)
    print("execute")
    pcall(luaload(console:getText()))
  end)

  clear_bt:addActionListener(function (ev)
    print("clear");
    console:setText("");
  end)

  exit_bt:addActionListener(function (ev)
    print("exit")
    frame:setVisible(false)
    frame:dispose()
  end)
end

return module
