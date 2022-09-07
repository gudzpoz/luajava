assert(package ~= nil)

assert(package.loaders ~= nil or package.searchers ~= nil)

assert(0 ~= pcall(function() require('suite.not.a.module') end))

My = require('suite.MyModule')
assert(My ~= nil)

assert(My:get('key') == nil)
My:set('key', 'mystring')
assert(My:get('key') == 'mystring')

assert(1024 == require('party.iroiro.luajava.JavaLibTest.open').getNumber())
assertThrows('not found', require, 'javalangStringopen')
assertThrows('not found', require, 'java.lang.String.openNoMethod')
assertThrows('not found', require, 'java.lang.StringWhatever.open')
assertThrows('not found', require, 'party.iroiro.luajava.JavaLibTest.noInt')
assertThrows('IOException', require, 'party.iroiro.luajava.JavaLibTest.close')
assertThrows('IllegalAccessException', require, 'party.iroiro.luajava.JavaLibTest.no')

assert(java.loadlib('party.iroiro.luajava.JavaLibTest', 'open')().getNumber() == 1024)
