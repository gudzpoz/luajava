-- Used with java.import('some.package.*')

return function(self, name)
  local value = rawget(self, name)
  if value ~= nil then
    return value
  else
    local j = rawget(getmetatable(self), '__jIndex')
    local v = j(self, name)
    rawset(self, name, v)
    return v
  end
end