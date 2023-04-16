local function pretty(v, i, printed)
  if not i then
    i = 1
  end
  if not printed then
    printed = {}
  end

  if type(v) == "table" and not printed[v] then
    printed[v] = true
    local s = "{\n"
    local indentation = string.rep(" ", 2 * i)
    for k, v in pairs(v) do
      if type(k) == "string" then
        k = "\"" .. k .. "\""
      end
      s = s .. indentation .. "[" .. k .. "] = " .. pretty(v, i + 1, printed) .. ",\n"
    end
    s = s .. string.rep(" ", 2 * (i - 1)) .. "}"
    return s
  else
    return tostring(v)
  end
end

return function(v) print(pretty(v)) end
