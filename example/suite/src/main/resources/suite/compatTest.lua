local luajava = require('suite.luajava-compat')
assert(luajava ~= nil)

assert(
  'String content' == luajava.newInstance(
    'java.lang.String', 'String content'
  ):toString()
)

assert(
  'java.lang.String' == luajava.bindClass(
    'java.lang.String'
  ).class:getName()
)

assert(
  '' == luajava.new(
    luajava.bindClass('java.lang.String')
  ):toString()
)

assert(
  1234 == luajava.createProxy(
    'java.util.concurrent.Callable',
    {
      call = function() return 1234 end
    }
  ):call()
)

assert(luajava.loadLib('party.iroiro.luajava.JavaLibTest', 'open').getNumber() == 1024)
