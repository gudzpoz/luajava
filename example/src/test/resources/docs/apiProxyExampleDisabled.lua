button = java.new(java.import('java.awt.Button'), 'Execute')
callback = {}
function callback:actionPerformed(ev)
  -- do something
end

buttonProxy = java.proxy('java.awt.ActionListener', callback)
button:addActionListener(buttonProxy)
