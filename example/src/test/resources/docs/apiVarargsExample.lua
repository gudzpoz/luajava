String = java.import('java.lang.String')
-- We automatically convert lua tables into Object[]
assert(String:format('>>> %s', { 'content' }) == '>>> content')
