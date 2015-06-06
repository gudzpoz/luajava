{
static const char* F =
"-----------------------------------------------------------------------------\n"
"-- Canonic header field capitalization                                       \n"
"-- LuaSocket toolkit.                                                        \n"
"-- Author: Diego Nehab                                                       \n"
"-----------------------------------------------------------------------------\n"
"local socket = require('socket')                                             \n"
"socket.headers = {}                                                          \n"
"local _M = socket.headers                                                    \n"
"                                                                             \n"
"_M.canonic = {                                                               \n"
"    ['accept'] = 'Accept',                                                   \n"
"    ['accept-charset'] = 'Accept-Charset',                                   \n"
"    ['accept-encoding'] = 'Accept-Encoding',                                 \n"
"    ['accept-language'] = 'Accept-Language',                                 \n"
"    ['accept-ranges'] = 'Accept-Ranges',                                     \n"
"    ['action'] = 'Action',                                                   \n"
"    ['alternate-recipient'] = 'Alternate-Recipient',                         \n"
"    ['age'] = 'Age',                                                         \n"
"    ['allow'] = 'Allow',                                                     \n"
"    ['arrival-date'] = 'Arrival-Date',                                       \n"
"    ['authorization'] = 'Authorization',                                     \n"
"    ['bcc'] = 'Bcc',                                                         \n"
"    ['cache-control'] = 'Cache-Control',                                     \n"
"    ['cc'] = 'Cc',                                                           \n"
"    ['comments'] = 'Comments',                                               \n"
"    ['connection'] = 'Connection',                                           \n"
"    ['content-description'] = 'Content-Description',                         \n"
"    ['content-disposition'] = 'Content-Disposition',                         \n"
"    ['content-encoding'] = 'Content-Encoding',                               \n"
"    ['content-id'] = 'Content-ID',                                           \n"
"    ['content-language'] = 'Content-Language',                               \n"
"    ['content-length'] = 'Content-Length',                                   \n"
"    ['content-location'] = 'Content-Location',                               \n"
"    ['content-md5'] = 'Content-MD5',                                         \n"
"    ['content-range'] = 'Content-Range',                                     \n"
"    ['content-transfer-encoding'] = 'Content-Transfer-Encoding',             \n"
"    ['content-type'] = 'Content-Type',                                       \n"
"    ['cookie'] = 'Cookie',                                                   \n"
"    ['date'] = 'Date',                                                       \n"
"    ['diagnostic-code'] = 'Diagnostic-Code',                                 \n"
"    ['dsn-gateway'] = 'DSN-Gateway',                                         \n"
"    ['etag'] = 'ETag',                                                       \n"
"    ['expect'] = 'Expect',                                                   \n"
"    ['expires'] = 'Expires',                                                 \n"
"    ['final-log-id'] = 'Final-Log-ID',                                       \n"
"    ['final-recipient'] = 'Final-Recipient',                                 \n"
"    ['from'] = 'From',                                                       \n"
"    ['host'] = 'Host',                                                       \n"
"    ['if-match'] = 'If-Match',                                               \n"
"    ['if-modified-since'] = 'If-Modified-Since',                             \n"
"    ['if-none-match'] = 'If-None-Match',                                     \n"
"    ['if-range'] = 'If-Range',                                               \n"
"    ['if-unmodified-since'] = 'If-Unmodified-Since',                         \n"
"    ['in-reply-to'] = 'In-Reply-To',                                         \n"
"    ['keywords'] = 'Keywords',                                               \n"
"    ['last-attempt-date'] = 'Last-Attempt-Date',                             \n"
"    ['last-modified'] = 'Last-Modified',                                     \n"
"    ['location'] = 'Location',                                               \n"
"    ['max-forwards'] = 'Max-Forwards',                                       \n"
"    ['message-id'] = 'Message-ID',                                           \n"
"    ['mime-version'] = 'MIME-Version',                                       \n"
"    ['original-envelope-id'] = 'Original-Envelope-ID',                       \n"
"    ['original-recipient'] = 'Original-Recipient',                           \n"
"    ['pragma'] = 'Pragma',                                                   \n"
"    ['proxy-authenticate'] = 'Proxy-Authenticate',                           \n"
"    ['proxy-authorization'] = 'Proxy-Authorization',                         \n"
"    ['range'] = 'Range',                                                     \n"
"    ['received'] = 'Received',                                               \n"
"    ['received-from-mta'] = 'Received-From-MTA',                             \n"
"    ['references'] = 'References',                                           \n"
"    ['referer'] = 'Referer',                                                 \n"
"    ['remote-mta'] = 'Remote-MTA',                                           \n"
"    ['reply-to'] = 'Reply-To',                                               \n"
"    ['reporting-mta'] = 'Reporting-MTA',                                     \n"
"    ['resent-bcc'] = 'Resent-Bcc',                                           \n"
"    ['resent-cc'] = 'Resent-Cc',                                             \n"
"    ['resent-date'] = 'Resent-Date',                                         \n"
"    ['resent-from'] = 'Resent-From',                                         \n"
"    ['resent-message-id'] = 'Resent-Message-ID',                             \n"
"    ['resent-reply-to'] = 'Resent-Reply-To',                                 \n"
"    ['resent-sender'] = 'Resent-Sender',                                     \n"
"    ['resent-to'] = 'Resent-To',                                             \n"
"    ['retry-after'] = 'Retry-After',                                         \n"
"    ['return-path'] = 'Return-Path',                                         \n"
"    ['sender'] = 'Sender',                                                   \n"
"    ['server'] = 'Server',                                                   \n"
"    ['smtp-remote-recipient'] = 'SMTP-Remote-Recipient',                     \n"
"    ['status'] = 'Status',                                                   \n"
"    ['subject'] = 'Subject',                                                 \n"
"    ['te'] = 'TE',                                                           \n"
"    ['to'] = 'To',                                                           \n"
"    ['trailer'] = 'Trailer',                                                 \n"
"    ['transfer-encoding'] = 'Transfer-Encoding',                             \n"
"    ['upgrade'] = 'Upgrade',                                                 \n"
"    ['user-agent'] = 'User-Agent',                                           \n"
"    ['vary'] = 'Vary',                                                       \n"
"    ['via'] = 'Via',                                                         \n"
"    ['warning'] = 'Warning',                                                 \n"
"    ['will-retry-until'] = 'Will-Retry-Until',                               \n"
"    ['www-authenticate'] = 'WWW-Authenticate',                               \n"
"    ['x-mailer'] = 'X-Mailer',                                               \n"
"}                                                                            \n"
"                                                                             \n"
"return _M";
if (luaL_dostring(L, F)!=0) cout << "Error: headers.lua";
else cout << "Fine: headers.lua";
}