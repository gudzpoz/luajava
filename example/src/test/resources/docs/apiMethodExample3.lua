iter1 = java.proxy('java.util.Iterator', {})
-- Calls the default method
pcall(function() iter1:remove() end)

-- What if we want to access the default method from a overridden one?
iterImpl = {
  next = function()
    i = i - 1
    return i
  end,
  hasNext = function()
    return i > 0
  end,
  remove = function(this)
    -- Calls the default method from java.util.Iterator.
    java.method(this, 'java.util.Iterator:remove', '')()
    -- Equivalent to the following in Java
    --     Iterator.super.remove();
  end
}

iter = java.proxy('java.util.Iterator', iterImpl)
-- Calls the implemented `remove`, which then calls the default one
pcall(function() iter:remove() end)
-- Or explicitly calling `remove`
pcall(function() java.method(iter, 'java.util.Iterator:remove')() end)
