function main()
  local i = 1
  local j = 1
  while (true) do
    coroutine.yield(i)
    k = i + j
    i = j
    j = k
  end
end