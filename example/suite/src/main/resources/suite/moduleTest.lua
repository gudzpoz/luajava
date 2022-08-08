assert(package ~= nil)

assert(package.loaders ~= nil or package.searchers ~= nil)

assert(0 ~= pcall(function() require('suite.not.a.module') end))

My = require('suite.MyModule')
assert(My ~= nil)

assert(My:get('key') == nil)
My:set('key', 'mystring')
assert(My:get('key') == 'mystring')
