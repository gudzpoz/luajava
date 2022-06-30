# AWT Example

::: tip
With [Interactive Console](../console.md), you may copy and paste the following Lua code to try it out.
:::

::: warning
Note that if you are using this in a normal Java application, you should take note of when you close the Lua state. You don't want to have the Lua state closed before the window is fully shutdown, which will definitely lead to problems.
:::

:::: code-group
::: code-group-item Lua 5.1
```lua
-- Lua 5.1: Use loadstring
frame = java.import("java.awt.Frame")("Lua Java Console")
console = java.import("java.awt.TextArea")()
buttonsPanel = java.import("java.awt.Panel")()
executeButton = java.import("java.awt.Button")("Execute")
clearButton = java.import("java.awt.Button")("Clear")
exitButton = java.import("java.awt.Button")("Exit")

frame:setSize(600, 300)

buttonsPanel:add(executeButton)
buttonsPanel:add(clearButton)
buttonsPanel:add(exitButton)

BorderLayout = java.import("java.awt.BorderLayout")

frame:add(BorderLayout.NORTH, console)
frame:add(BorderLayout.SOUTH, buttonsPanel)
frame:pack()
frame:show()

--
-- Listeners
--

executeCallback = {
  actionPerformed = function(this, ev)
    print("execute")
    pcall(loadstring(console:getText()))
  end
}

jproxy = java.proxy("java.awt.event.ActionListener", executeCallback)

executeButton:addActionListener(jproxy)

clearCallback = {
  actionPerformed = function (this, ev)
    print("clear");
    console:setText("");
  end
}

jproxy = java.proxy("java.awt.event.ActionListener", clearCallback)
clearButton:addActionListener(jproxy)

exitCallback = {
  actionPerformed = function (this, ev)
    print("exit")
    frame:setVisible(false)
    frame:dispose()
  end
}


jproxyb = java.proxy("java.awt.event.ActionListener", exitCallback)

exitButton:addActionListener(jproxyb)

closeCallback = {
  windowClosing = function (this, ev)
    print("close")
    frame:setVisible(false)
    frame:dispose()
  end
}

function closeCallback.windowActivated(this, ev)
  print("act")
end

jproxy = java.proxy("java.awt.event.WindowListener", closeCallback)
frame:addWindowListener(jproxy)
```
:::

::: code-group-item Lua 5.2 ~
```lua
-- Lua 5.2: Use load instead
frame = java.import("java.awt.Frame")("Lua Java Console")
console = java.import("java.awt.TextArea")()
buttonsPanel = java.import("java.awt.Panel")()
executeButton = java.import("java.awt.Button")("Execute")
clearButton = java.import("java.awt.Button")("Clear")
exitButton = java.import("java.awt.Button")("Exit")

frame:setSize(600, 300)

buttonsPanel:add(executeButton)
buttonsPanel:add(clearButton)
buttonsPanel:add(exitButton)

BorderLayout = java.import("java.awt.BorderLayout")

frame:add(BorderLayout.NORTH, console)
frame:add(BorderLayout.SOUTH, buttonsPanel)
frame:pack()
frame:show()

--
-- Listeners
--

executeCallback = {
  actionPerformed = function(this, ev)
    print("execute")
    pcall(load(console:getText()))
  end
}

jproxy = java.proxy("java.awt.event.ActionListener", executeCallback)

executeButton:addActionListener(jproxy)

clearCallback = {
  actionPerformed = function (this, ev)
    print("clear");
    console:setText("");
  end
}

jproxy = java.proxy("java.awt.event.ActionListener", clearCallback)
clearButton:addActionListener(jproxy)

exitCallback = {
  actionPerformed = function (this, ev)
    print("exit")
    frame:setVisible(false)
    frame:dispose()
  end
}


jproxyb = java.proxy("java.awt.event.ActionListener", exitCallback)

exitButton:addActionListener(jproxyb)

closeCallback = {
  windowClosing = function (this, ev)
    print("close")
    frame:setVisible(false)
    frame:dispose()
  end
}

function closeCallback.windowActivated(this, ev)
  print("act")
end

jproxy = java.proxy("java.awt.event.WindowListener", closeCallback)
frame:addWindowListener(jproxy)
```
:::
::::
