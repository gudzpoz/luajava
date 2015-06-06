{
static const char* F =
"-----------------------------------------------------------------------------     \n"
"-- SMTP client support for the Lua language.                                      \n"
"-- LuaSocket toolkit.                                                             \n"
"-- Author: Diego Nehab                                                            \n"
"-----------------------------------------------------------------------------     \n"
"                                                                                  \n"
"-----------------------------------------------------------------------------     \n"
"-- Declare module and import dependencies                                         \n"
"-----------------------------------------------------------------------------     \n"
"local base = _G                                                                   \n"
"local coroutine = require('coroutine')                                            \n"
"local string = require('string')                                                  \n"
"local math = require('math')                                                      \n"
"local os = require('os')                                                          \n"
"local socket = require('socket')                                                  \n"
"local tp = require('socket.tp')                                                   \n"
"local ltn12 = require('ltn12')                                                    \n"
"local headers = require('socket.headers')                                         \n"
"local mime = require('mime')                                                      \n"
"                                                                                  \n"
"socket.smtp = {}                                                                  \n"
"local _M = socket.smtp                                                            \n"
"                                                                                  \n"
"-----------------------------------------------------------------------------     \n"
"-- Program constants                                                              \n"
"-----------------------------------------------------------------------------     \n"
"-- timeout for connection                                                         \n"
"_M.TIMEOUT = 60                                                                   \n"
"-- default server used to send e-mails                                            \n"
"_M.SERVER = 'localhost'                                                           \n"
"-- default port                                                                   \n"
"_M.PORT = 25                                                                      \n"
"-- domain used in HELO command and default sendmail                               \n"
"-- If we are under a CGI, try to get from environment                             \n"
"_M.DOMAIN = os.getenv('SERVER_NAME') or 'localhost'                               \n"
"-- default time zone (means we don't know)                                        \n"
"_M.ZONE = '-0000'                                                                 \n"
"                                                                                  \n"
"---------------------------------------------------------------------------       \n"
"-- Low level SMTP API                                                             \n"
"-----------------------------------------------------------------------------     \n"
"local metat = { __index = {} }                                                    \n"
"                                                                                  \n"
"function metat.__index:greet(domain)                                              \n"
"    self.try(self.tp:check('2..'))                                                \n"
"    self.try(self.tp:command('EHLO', domain or _M.DOMAIN))                        \n"
"    return socket.skip(1, self.try(self.tp:check('2..')))                         \n"
"end                                                                               \n"
"                                                                                  \n"
"function metat.__index:mail(from)                                                 \n"
"    self.try(self.tp:command('MAIL', 'FROM:' .. from))                            \n"
"    return self.try(self.tp:check('2..'))                                         \n"
"end                                                                               \n"
"                                                                                  \n"
"function metat.__index:rcpt(to)                                                   \n"
"    self.try(self.tp:command('RCPT', 'TO:' .. to))                                \n"
"    return self.try(self.tp:check('2..'))                                         \n"
"end                                                                               \n"
"                                                                                  \n"
"function metat.__index:data(src, step)                                            \n"
"    self.try(self.tp:command('DATA'))                                             \n"
"    self.try(self.tp:check('3..'))                                                \n"
"    self.try(self.tp:source(src, step))                                           \n"
"    self.try(self.tp:send('\r\n.\r\n'))                                           \n"
"    return self.try(self.tp:check('2..'))                                         \n"
"end                                                                               \n"
"                                                                                  \n"
"function metat.__index:quit()                                                     \n"
"    self.try(self.tp:command('QUIT'))                                             \n"
"    return self.try(self.tp:check('2..'))                                         \n"
"end                                                                               \n"
"                                                                                  \n"
"function metat.__index:close()                                                    \n"
"    return self.tp:close()                                                        \n"
"end                                                                               \n"
"                                                                                  \n"
"function metat.__index:login(user, password)                                      \n"
"    self.try(self.tp:command('AUTH', 'LOGIN'))                                    \n"
"    self.try(self.tp:check('3..'))                                                \n"
"    self.try(self.tp:send(mime.b64(user) .. '\r\n'))                              \n"
"    self.try(self.tp:check('3..'))                                                \n"
"    self.try(self.tp:send(mime.b64(password) .. '\r\n'))                          \n"
"    return self.try(self.tp:check('2..'))                                         \n"
"end                                                                               \n"
"                                                                                  \n"
"function metat.__index:plain(user, password)                                      \n"
"    local auth = 'PLAIN ' .. mime.b64('\0' .. user .. '\0' .. password)           \n"
"    self.try(self.tp:command('AUTH', auth))                                       \n"
"    return self.try(self.tp:check('2..'))                                         \n"
"end                                                                               \n"
"                                                                                  \n"
"function metat.__index:auth(user, password, ext)                                  \n"
"    if not user or not password then return 1 end                                 \n"
"    if string.find(ext, 'AUTH[^\n]+LOGIN') then                                   \n"
"        return self:login(user, password)                                         \n"
"    elseif string.find(ext, 'AUTH[^\n]+PLAIN') then                               \n"
"        return self:plain(user, password)                                         \n"
"    else                                                                          \n"
"        self.try(nil, 'authentication not supported')                             \n"
"    end                                                                           \n"
"end                                                                               \n"
"                                                                                  \n"
"-- send message or throw an exception                                             \n"
"function metat.__index:send(mailt)                                                \n"
"    self:mail(mailt.from)                                                         \n"
"    if base.type(mailt.rcpt) == 'table' then                                      \n"
"        for i,v in base.ipairs(mailt.rcpt) do                                     \n"
"            self:rcpt(v)                                                          \n"
"        end                                                                       \n"
"    else                                                                          \n"
"        self:rcpt(mailt.rcpt)                                                     \n"
"    end                                                                           \n"
"    self:data(ltn12.source.chain(mailt.source, mime.stuff()), mailt.step)         \n"
"end                                                                               \n"
"                                                                                  \n"
"function _M.open(server, port, create)                                            \n"
"    local tp = socket.try(tp.connect(server or _M.SERVER, port or _M.PORT,        \n"
"        _M.TIMEOUT, create))                                                      \n"
"    local s = base.setmetatable({tp = tp}, metat)                                 \n"
"    -- make sure tp is closed if we get an exception                              \n"
"    s.try = socket.newtry(function()                                              \n"
"        s:close()                                                                 \n"
"    end)                                                                          \n"
"    return s                                                                      \n"
"end                                                                               \n"
"                                                                                  \n"
"-- convert headers to lowercase                                                   \n"
"local function lower_headers(headers)                                             \n"
"    local lower = {}                                                              \n"
"    for i,v in base.pairs(headers or lower) do                                    \n"
"        lower[string.lower(i)] = v                                                \n"
"    end                                                                           \n"
"    return lower                                                                  \n"
"end                                                                               \n"
"                                                                                  \n"
"---------------------------------------------------------------------------       \n"
"-- Multipart message source                                                       \n"
"-----------------------------------------------------------------------------     \n"
"-- returns a hopefully unique mime boundary                                       \n"
"local seqno = 0                                                                   \n"
"local function newboundary()                                                      \n"
"    seqno = seqno + 1                                                             \n"
"    return string.format('%s%05d==%05u', os.date('%d%m%Y%H%M%S'),                 \n"
"        math.random(0, 99999), seqno)                                             \n"
"end                                                                               \n"
"                                                                                  \n"
"-- send_message forward declaration                                               \n"
"local send_message                                                                \n"
"                                                                                  \n"
"-- yield the headers all at once, it's faster                                     \n"
"local function send_headers(tosend)                                               \n"
"    local canonic = headers.canonic                                               \n"
"    local h = '\r\n'                                                              \n"
"    for f,v in base.pairs(tosend) do                                              \n"
"        h = (canonic[f] or f) .. ': ' .. v .. '\r\n' .. h                         \n"
"    end                                                                           \n"
"    coroutine.yield(h)                                                            \n"
"end                                                                               \n"
"                                                                                  \n"
"-- yield multipart message body from a multipart message table                    \n"
"local function send_multipart(mesgt)                                              \n"
"    -- make sure we have our boundary and send headers                            \n"
"    local bd = newboundary()                                                      \n"
"    local headers = lower_headers(mesgt.headers or {})                            \n"
"    headers['content-type'] = headers['content-type'] or 'multipart/mixed'        \n"
"    headers['content-type'] = headers['content-type'] ..                          \n"
"        '; boundary='' ..  bd .. '''                                              \n"
"    send_headers(headers)                                                         \n"
"    -- send preamble                                                              \n"
"    if mesgt.body.preamble then                                                   \n"
"        coroutine.yield(mesgt.body.preamble)                                      \n"
"        coroutine.yield('\r\n')                                                   \n"
"    end                                                                           \n"
"    -- send each part separated by a boundary                                     \n"
"    for i, m in base.ipairs(mesgt.body) do                                        \n"
"        coroutine.yield('\r\n--' .. bd .. '\r\n')                                 \n"
"        send_message(m)                                                           \n"
"    end                                                                           \n"
"    -- send last boundary                                                         \n"
"    coroutine.yield('\r\n--' .. bd .. '--\r\n\r\n')                               \n"
"    -- send epilogue                                                              \n"
"    if mesgt.body.epilogue then                                                   \n"
"        coroutine.yield(mesgt.body.epilogue)                                      \n"
"        coroutine.yield('\r\n')                                                   \n"
"    end                                                                           \n"
"end                                                                               \n"
"                                                                                  \n"
"-- yield message body from a source                                               \n"
"local function send_source(mesgt)                                                 \n"
"    -- make sure we have a content-type                                           \n"
"    local headers = lower_headers(mesgt.headers or {})                            \n"
"    headers['content-type'] = headers['content-type'] or                          \n"
"        'text/plain; charset='iso-8859-1''                                        \n"
"    send_headers(headers)                                                         \n"
"    -- send body from source                                                      \n"
"    while true do                                                                 \n"
"        local chunk, err = mesgt.body()                                           \n"
"        if err then coroutine.yield(nil, err)                                     \n"
"        elseif chunk then coroutine.yield(chunk)                                  \n"
"        else break end                                                            \n"
"    end                                                                           \n"
"end                                                                               \n"
"                                                                                  \n"
"-- yield message body from a string                                               \n"
"local function send_string(mesgt)                                                 \n"
"    -- make sure we have a content-type                                           \n"
"    local headers = lower_headers(mesgt.headers or {})                            \n"
"    headers['content-type'] = headers['content-type'] or                          \n"
"        'text/plain; charset='iso-8859-1''                                        \n"
"    send_headers(headers)                                                         \n"
"    -- send body from string                                                      \n"
"    coroutine.yield(mesgt.body)                                                   \n"
"end                                                                               \n"
"                                                                                  \n"
"-- message source                                                                 \n"
"function send_message(mesgt)                                                      \n"
"    if base.type(mesgt.body) == 'table' then send_multipart(mesgt)                \n"
"    elseif base.type(mesgt.body) == 'function' then send_source(mesgt)            \n"
"    else send_string(mesgt) end                                                   \n"
"end                                                                               \n"
"                                                                                  \n"
"-- set defaul headers                                                             \n"
"local function adjust_headers(mesgt)                                              \n"
"    local lower = lower_headers(mesgt.headers)                                    \n"
"    lower['date'] = lower['date'] or                                              \n"
"        os.date('!%a, %d %b %Y %H:%M:%S ') .. (mesgt.zone or _M.ZONE)             \n"
"    lower['x-mailer'] = lower['x-mailer'] or socket._VERSION                      \n"
"    -- this can't be overriden                                                    \n"
"    lower['mime-version'] = '1.0'                                                 \n"
"    return lower                                                                  \n"
"end                                                                               \n"
"                                                                                  \n"
"function _M.message(mesgt)                                                        \n"
"    mesgt.headers = adjust_headers(mesgt)                                         \n"
"    -- create and return message source                                           \n"
"    local co = coroutine.create(function() send_message(mesgt) end)               \n"
"    return function()                                                             \n"
"        local ret, a, b = coroutine.resume(co)                                    \n"
"        if ret then return a, b                                                   \n"
"        else return nil, a end                                                    \n"
"    end                                                                           \n"
"end                                                                               \n"
"                                                                                  \n"
"---------------------------------------------------------------------------       \n"
"-- High level SMTP API                                                            \n"
"-----------------------------------------------------------------------------     \n"
"_M.send = socket.protect(function(mailt)                                          \n"
"    local s = _M.open(mailt.server, mailt.port, mailt.create)                     \n"
"    local ext = s:greet(mailt.domain)                                             \n"
"    s:auth(mailt.user, mailt.password, ext)                                       \n"
"    s:send(mailt)                                                                 \n"
"    s:quit()                                                                      \n"
"    return s:close()                                                              \n"
"end)                                                                              \n"
"                                                                                  \n"
"return _M";
if (luaL_dostring(L, F)!=0) cout << "Error: smtp.lua";
else cout << "Fine: smtp.lua";
}