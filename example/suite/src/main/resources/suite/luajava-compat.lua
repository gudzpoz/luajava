local luajava = {
  newInstance = function (className, ...)
    return java.import(className)(...)
  end,

  bindClass = java.import,

  new = java.new,

  createProxy = java.proxy,

  loadLib = function (className, methodName)
    return java.loadlib(className, methodName)()
  end,
}

return luajava
