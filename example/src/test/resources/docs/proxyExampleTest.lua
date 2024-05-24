r = java.proxy('java.lang.Runnable', {
  data = 'member data',
  run = function(this)
    assert(type(java.unwrap(this)) == 'table')
    assert(java.unwrap(this).data == 'member data')
    print('Hello')
  end
})
r:run()
