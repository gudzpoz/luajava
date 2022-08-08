local MyModule = {}

function MyModule:get(name)
    return self[name]
end

function MyModule:set(name, value)
    self[name] = value
end

return MyModule
