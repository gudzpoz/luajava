/* lsocket.c
 *
 * provide simple and easy socket support for lua
 *
 * Gunnar Zötl <gz@tset.de>, 2013-03
 * Released under MIT/X11 license. See file LICENSE for details.
 */

#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include <fcntl.h>
#include <string.h>
#include <errno.h>
#include <signal.h>

#ifdef _WIN32
#include <winsock2.h>
#include <direct.h>
#else
#include <arpa/inet.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <netdb.h>
#include <sys/stat.h>
#endif

#include <dirent.h>
#include <ifaddrs.h>

#ifndef IPV6_ADD_MEMBERSHIP
	#define IPV6_ADD_MEMBERSHIP IPV6_JOIN_GROUP
#endif

#include "lua.h"
#include "lauxlib.h"

#define LSOCKET_VERSION "1.0-2"

#define LSOCKET "socket"
#define TOSTRING_BUFSIZ 64
#define READER_BUFSIZ 4096
#define LSOCKET_EMPTY "lsocket_empty_table"
#define LSOCKET_INET "inet"
#define LSOCKET_INET6 "inet6"
#define LSOCKET_TCP "tcp"
#define LSOCKET_UDP "udp"
#define LSOCKET_MCAST "mcast"
#define LSOCKET_DFLTTL 1

/* default backlog for listening connections */
#define DFL_BACKLOG 5

/* this may need a little more sophistication, but seems to work ok */
#if LUA_VERSION_NUM == 501
#define luaL_newlib(L,funcs) lua_newtable(L); luaL_register(L, NULL, funcs)
#define luaL_setfuncs(L,funcs,x) luaL_register(L, NULL, funcs)
#endif

/*** Userdata handling ***/

/* structure for socket userdata */
typedef struct _lsocket_socket {
	int sockfd;
	int family;
	int type;
	int mcast;
	int protocol;
	int listening;
} lSocket;

/* lsocket_checklSocket
 *
 * Checks whether the item at the index on the lua stack is a userdata of
 * the type LSOCKET. If so, returns its block address, else throw an error.
 *
 * Arguments:
 * 	L	Lua State
 *	index	stack index where the userdata is expected
 */
static lSocket* lsocket_checklSocket(lua_State *L, int index)
{
	lSocket *sock = (lSocket*) luaL_checkudata(L, index, LSOCKET);
	return sock;
}

/* lsocket_islSocket
 * 
 * Checks whether the item at the index on the lua stack is a userdata of
 * the type LSOCKET. Returns 1 if it is, 0 otherwise.
 * 
 * Arguments:
 * 	L	Lua State
 *	index	stack index where the userdata is expected
 */
static int lsocket_islSocket(lua_State *L, int index)
{
	if (lua_isuserdata(L, index)) {
		if (lua_getmetatable(L, index)) {
			luaL_getmetatable(L, LSOCKET);
			if (lua_rawequal(L, -1, -2)) {
				lua_pop(L, 2);
				return 1;
			}
			lua_pop(L, 2);
		}
	}
	return 0;
}


/* lsocket_pushlSocket
 *
 * create a new, empty lSocket userdata, attach its metatable and push it to the stack.
 *
 * Arguments:
 *	L	Lua state
 * 
 * Lua Returns:
 * 	+1	lSocket userdata
 */
static lSocket* lsocket_pushlSocket(lua_State *L)
{
	lSocket *sock = (lSocket*) lua_newuserdata(L, sizeof(lSocket));
	luaL_getmetatable(L, LSOCKET);
	lua_setmetatable(L, -2);
	return sock;
}

/*** Housekeeping metamethods ***/

/* lsocket_gc
 *
 * __gc metamethod for the sock userdata.
 * closes socket if it is still open
 *
 * Arguments:
 *	L	Lua State
 *
 * Lua Stack:
 *	1	lSocket userdata
 */
static int lsocket_sock__gc(lua_State *L)
{
	lSocket *sock = (lSocket*) lua_touserdata(L, 1);
	if (sock->sockfd > 0)
		close(sock->sockfd);
	sock->sockfd = -1;

	return 0;
}

/* lsocket_toString
 *
 * __tostring metamethod for the lsock userdata.
 * Returns a string representation of the lSocket
 *
 * Arguments:
 *	L	Lua State
 *
 * Lua Stack:
 *	1	lSocket userdata
 * 
 * Lua Returns:
 * 	+1	string representation of lSocket userdata
 */
static int lsocket_sock__toString(lua_State *L)
{
	lSocket *sock = lsocket_checklSocket(L, 1);
	char buf[TOSTRING_BUFSIZ];
	/* length of type name + length of hex pointer rep + '0x' + ': ' + '\0' */
	if (strlen(LSOCKET) + (sizeof(void*) * 2) + 2 + 3 > TOSTRING_BUFSIZ)
		return luaL_error(L, "Whoopsie... the string representation seems to be too long.");
		/* this should not happen, just to be sure! */
	sprintf(buf, "%s: %p", LSOCKET, sock);
	lua_pushstring(L, buf);
	return 1;
}

/* metamethods for the sock userdata
 */
static const luaL_Reg lSocket_meta[] = {
	{"__gc", lsocket_sock__gc},
	{"__tostring", lsocket_sock__toString},
	{0, 0}
};

/*** global helper functions ***/

/* lsocket_error
 * 
 * pushes nil and an error message onto the lua stack and returns 2
 * 
 * Arguments:
 * 	L	lua State
 * 	msg	error message
 *
 * Returns:
 * 	2 (number of items put on the lua stack)
 */
static int lsocket_error(lua_State *L, const char *msg)
{
	lua_pushnil(L);
	lua_pushstring(L, msg);
	return 2;
}

/* _initsocket
 * 
 * helper function: initialize a socket object.
 * 
 * Arguments:
 * 	sock	the socket userdata
 * 	type, protocol
 * 			as specified for socket creation
 * 	listen	flag for whether the socket is listening for incoming connections
 * 
 * Returns:
 * 	0 if all went ok, -1 on failure.
 */
static int _initsocket(lSocket *sock, int family, int type, int mcast, int protocol, int listening)
{
	if (sock->sockfd == -1)
		return 0;

	int on = 1;
	setsockopt(sock->sockfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on));
	sock->family = family;
	sock->type = type;
	sock->mcast = mcast;
	sock->protocol = protocol;
	sock->listening = listening;
	
	return 0;
}

/* _addr2string
 * 
 * helper function: converts a sockaddr to a string, family independent
 * 
 * Arguments:
 * 	sa	sockaddr to convert
 * 	buf	buffer to write result into
 * 	buflen	length of buffer
 * 
 * Returns:
 * 	the result of inet_ntop: a pointer to buf on success, or NULL on
 * 	failure.
 */
static const char *_addr2string(struct sockaddr *sa, char *buf, int buflen)
{
	const char *s;
	if (sa->sa_family == AF_INET)
		s = inet_ntop(sa->sa_family, (const void*) &((struct sockaddr_in*)sa)->sin_addr, buf, TOSTRING_BUFSIZ);
	else
		s = inet_ntop(sa->sa_family, (const void*) &((struct sockaddr_in6*)sa)->sin6_addr, buf, TOSTRING_BUFSIZ);
	return s;
}

/* _portnumber
 * 
 * extracts the port number from a sockaddr, family independent
 * 
 * Arguments:
 * 	sa	sockaddr to extract port number from
 * 
 * Returns:
 * 	the extracted and to local byte order converted port number
 */
static uint16_t _portnumber(struct sockaddr *sa)
{
	uint16_t port;
	if (sa->sa_family == AF_INET)
		port = ((struct sockaddr_in*)sa)->sin_port;
	else
		port = ((struct sockaddr_in6*)sa)->sin6_port;
	return ntohs(port);
}

/* _needsnolookup
 * 
 * helper function: checks if the address consists only of chars that
 * make up a valid ip(v4 or v6) address, and thus needs no nslookup.
 * 
 * Arguments:
 * 	addr	address to check
 * 
 * Returns:
 * 	1 if the address consists only of chars that make up a valid ip(v4
 * 	or v6) address, 0 otherwise.
 * 
 * Note: this does not check whether the address is a valid ip address,
 * just whether it consists of chars that make up one.
 */
static int _needsnolookup(const char *addr)
{
	int len = strlen(addr);
	int pfx = strspn(addr, "0123456789.");
	if (pfx != len) {
		pfx = strspn(addr, "0123456789abcdefABCDEF:");
		/* last 2 words may be in dot notation */
		if (addr[pfx] == '.')
			pfx += strspn(addr + pfx, "0123456789.");
	}
	return pfx == len;
}

/* _gethostaddr
 * 
 * gets the address of a host passed by name, and fills out a sockaddr
 * structure
 * 
 * Arguments:
 * 	L	lua State
 * 	addr	ip address or hostname
 * 	type	SOCK_STREAM or SOCK_DGRAM
 * 	port	port number for sockaddr
 * 	family	(out) address family (AF_INET or AF_INET6) of address
 * 	protocol	(out) protocol of address
 * 	sa	(out) pointer to struct sockaddr to wfill with data.
 * 	slen	(inout)	length of sa, and on return length of data
 * 
 * Returns:
 * 	0 when the lookup succeeded, -1 otherwise
 * 	On success, the arguments family, protocol, sa and slen will have
 * 	been updated with values from the looked up address
 */
static int _gethostaddr(lua_State *L, const char *addr, int type, int port, int *family, int *protocol, struct sockaddr *sa, socklen_t *slen)
{
	struct addrinfo hint, *info =0;
	memset(&hint, 0, sizeof(hint));
	hint.ai_family = AF_UNSPEC;
	hint.ai_socktype = type;
	if (_needsnolookup(addr))
		hint.ai_flags = AI_NUMERICHOST;

	int err = getaddrinfo(addr, 0, &hint, &info);
    if (err != 0) {
		if (info) freeaddrinfo(info);
		return lsocket_error(L, gai_strerror(err));
	} else if (info->ai_family != AF_INET && info->ai_family != AF_INET6) {
		if (info) freeaddrinfo(info);
		return lsocket_error(L, "unknown address family");
	}

	*family = info->ai_family;
    *slen = info->ai_addrlen;
    *protocol = info->ai_protocol;
    memcpy(sa, info->ai_addr, *slen);
    if (*family == AF_INET)
		((struct sockaddr_in*) sa)->sin_port = htons(port);
	else
		((struct sockaddr_in6*) sa)->sin6_port = htons(port);

    freeaddrinfo(info);
    return 0;
}

/*** socket constructors */

/* lsocket_bind
 * 
 * bind a socket to a specific address and port.
 * 
 * Arguments:
 * 	L	Lua State
 * 
 * Lua Stack:
 *	1	(optional) "tcp" or "udp" or "mcast"
 * 	2	(optional) the address to bind to, as a hostname or ip address string
 * 	3	the port to bind to
 *	4	(optional) backlog for listen
 * 
 * Lua Returns:
 * 	+1	the lSocket userdata passed as first argument
 * 	or +1 nil, +2 error message
 */
static int lsocket_bind(lua_State *L)
{
	char sabuf[sizeof(struct sockaddr_in6)];
	struct sockaddr *sa = (struct sockaddr*) sabuf;
	socklen_t slen = sizeof(sabuf);
	int family = AF_INET;
	int protocol = 0;
	int mcast = 0;
	const char *addr = NULL;
	int type = SOCK_STREAM;
	int top = 1;
	
	/* (optional) socket type */
	if (lua_type(L, top) == LUA_TSTRING) {
		const char *str = lua_tostring(L, top);
		if (!strcasecmp(str, LSOCKET_TCP)) {
			top += 1;
		} else if (!strcasecmp(str, LSOCKET_UDP)) {
			type = SOCK_DGRAM;
			top += 1;
		} else if (!strcasecmp(str, LSOCKET_MCAST)) {
			type = SOCK_DGRAM;
			mcast = 1;
			top += 1;
		}
	}
	/* (optional) ip address to bind to */
	if (lua_type(L, top) == LUA_TSTRING) {
		addr = lua_tostring(L, top++);
	}
	/* port to bind to */
	int port = luaL_checknumber(L, top++);
	/* backlog len */
	int backlog = luaL_optnumber(L, top, DFL_BACKLOG);

	if (addr) {
		int err = _gethostaddr(L, addr, type, port, &family, &protocol, sa, &slen);
		if (err) return err;
	} else {
		/* default: bind to ipv4 INADDR_ANY */
		memset(&sabuf, 0, sizeof(sabuf));
		struct sockaddr_in *si = (struct sockaddr_in*) sa;
		sa->sa_family = AF_INET;
		si->sin_addr.s_addr = INADDR_ANY;
		si->sin_port = htons(port);
		slen = sizeof(struct sockaddr_in);
	}

	lSocket *sock = lsocket_pushlSocket(L);
	sock->sockfd = socket(family, type, protocol);
	_initsocket(sock, family, type, mcast, protocol, 1);

	/* setup socket for broad/multicast if necessary */
	if (mcast) {
		if (family == AF_INET) {
			if (setsockopt(sock->sockfd, SOL_SOCKET, SO_BROADCAST, &mcast, sizeof(mcast)) < 0)
				return lsocket_error(L, strerror(errno));
		} else {
			struct ipv6_mreq mcr;
			memcpy(&mcr.ipv6mr_multiaddr, &((struct sockaddr_in6*)sa)->sin6_addr, sizeof(mcr.ipv6mr_multiaddr));
			mcr.ipv6mr_interface = 0;
			if (setsockopt(sock->sockfd, IPPROTO_IPV6, IPV6_ADD_MEMBERSHIP, &mcr, sizeof(mcr)) < 0)
				return lsocket_error(L, strerror(errno));
		}
	}

	if (bind(sock->sockfd, sa, slen) < 0)
		return lsocket_error(L, strerror(errno));

	/* only listen for tcp sockets, does not make much sense for udp */
	if (type == SOCK_STREAM)
		if (listen(sock->sockfd, backlog) < 0)
			return lsocket_error(L, strerror(errno));

	return 1;
}

/* lsocket_connect
 * 
 * connect a socket to a specific address and port.
 * 
 * Arguments:
 * 	L	Lua State
 * 
 * Lua Stack:
 *	1	(optional) "tcp" or "udp" or "mcast"
 * 	2	the address to bind to, as a hostname or ip address string
 * 	3	the port to bind to
 * 	4	(optional) ttl for multicasts
 * 
 * Lua Returns:
 * 	+1	the lSocket userdata passed as first argument
 * 	or +1 nil, +2 error message
 */
static int lsocket_connect(lua_State *L)
{
	int type = SOCK_STREAM;
	int top = 1;
	char sabuf[sizeof(struct sockaddr_in6)];
	struct sockaddr *sa = (struct sockaddr*) sabuf;
	socklen_t slen = sizeof(sabuf);
	int family = AF_INET;
	int protocol = 0;
	int mcast = 0;
	
	/* (optional) socket type */
	if (lua_type(L, top) == LUA_TSTRING) {
		const char *str = lua_tostring(L, 1);
		if (!strcasecmp(str, LSOCKET_TCP))
			top += 1;
		else if (!strcasecmp(str, LSOCKET_UDP)) {
			type = SOCK_DGRAM;
			top += 1;
		} else if (!strcasecmp(str, LSOCKET_MCAST)) {
			type = SOCK_DGRAM;
			mcast = 1;
			top += 1;
		}
	}
	/* ip address to connect to */
	const char *addr = luaL_checkstring(L, top);
	/* port to connect to */
	int port = luaL_checknumber(L, top + 1);
	/* ttl in the case of multicast */
	int ttl = luaL_optnumber(L, top + 2, LSOCKET_DFLTTL);

	int err = _gethostaddr(L, addr, type, port, &family, &protocol, sa, &slen);
	if (err) return err;

	lSocket *sock = lsocket_pushlSocket(L);
	sock->sockfd = socket(family, type, protocol);
	_initsocket(sock, family, type, mcast, protocol, 0);
	if (mcast) {
		int ok;
		if (setsockopt(sock->sockfd, SOL_SOCKET, SO_BROADCAST, &mcast, sizeof(mcast)) < 0)
			return lsocket_error(L, strerror(errno));
		if (family == AF_INET) {
			ok = setsockopt(sock->sockfd, IPPROTO_IP, IP_MULTICAST_TTL, &ttl, sizeof(ttl));
		} else
			ok = setsockopt(sock->sockfd, IPPROTO_IPV6, IPV6_MULTICAST_HOPS, &ttl, sizeof(ttl));
		if (ok < 0)
			return lsocket_error(L, strerror(errno));
	}
	
	if (connect(sock->sockfd, sa, slen) < 0)
		return lsocket_error(L, strerror(errno));

	return 1;
}

/*** socket methods ***/

/* _push_sockname
 * 
 * helper for lsocket_sock_info: create a table with fields port, family
 * and addr from the sockaddr passed as argument, and leave it on the
 * stack. Leave nil on error.
 * 
 * Arguments:
 * 	L	Lua State
 *	sa	pointer to sockaddr to get data from
 * 
 * Lua Returns:
 * 	+1 table with info about address, or nil
 */
static void _push_sockname(lua_State *L, struct sockaddr *sa)
{
	char buf[TOSTRING_BUFSIZ];
	const char *s;
	lua_pushliteral(L, "port");
	lua_pushnumber(L, _portnumber(sa));
	lua_rawset(L, -3);
	lua_pushliteral(L, "family");
	lua_pushstring(L, sa->sa_family == AF_INET ? LSOCKET_INET : LSOCKET_INET6);
	lua_rawset(L, -3);
	lua_pushliteral(L, "addr");
	s = _addr2string(sa, buf, TOSTRING_BUFSIZ);
	if (s) {
		lua_pushstring(L, s);
		lua_rawset(L, -3);
	} else {
		lua_pop(L, 1);
	}
}

/* lsocket_sock_info
 * 
 * returns a table with information about the socket, such as file descriptor,
 * family, type, and listening, or local and peer addresses.
 * 
 * Arguments:
 * 	L	Lua State
 * 
 * Lua Stack:
 * 	1	the lSocket userdata
 *  2	(optional) what to return info about ("peer", "socket")
 * 
 * Lua Returns:
 * 	+1	the table with the information
 * 		for what==none or nil: in the fields fd, family, type, listening
 * 		for what == "peer" or "socket": addr, port
 */
static int lsocket_sock_info(lua_State *L)
{
	lSocket *sock = lsocket_checklSocket(L, 1);
	const char *which = luaL_optstring(L, 2, NULL);
	char sabuf [sizeof(struct sockaddr_in6)];
	struct sockaddr *sa = (struct sockaddr*) sabuf;
	socklen_t slen = sizeof(sabuf);

	lua_newtable(L);
	
	if (which == NULL) {
		lua_pushliteral(L, "fd");
		lua_pushnumber(L, sock->sockfd);
		lua_rawset(L, -3);

		lua_pushliteral(L, "family");
		switch (sock->family) {
			case AF_INET: lua_pushliteral(L, LSOCKET_INET); break;
			case AF_INET6: lua_pushliteral(L, LSOCKET_INET6); break;
			default: lua_pushliteral(L, "unknown");
		}
		lua_rawset(L, -3);

		lua_pushliteral(L, "type");
		switch (sock->type) {
			case SOCK_STREAM: lua_pushliteral(L, LSOCKET_TCP); break;
			case SOCK_DGRAM: lua_pushliteral(L, LSOCKET_UDP); break;
			default: lua_pushliteral(L, "unknown");
		}
		lua_rawset(L, -3);
		
		lua_pushliteral(L, "listening");
		lua_pushboolean(L, sock->listening);
		lua_rawset(L, -3);

		lua_pushliteral(L, "multicast");
		lua_pushboolean(L, sock->mcast);
		lua_rawset(L, -3);
	} else if (!strcasecmp(which, "peer")) {
		if (getpeername(sock->sockfd, sa, &slen) >= 0)
			_push_sockname(L, sa);
		else {
			lua_pop(L, 1);
			lua_pushnil(L);
		}
	} else if (!strcasecmp(which, "socket")) {
		if (getsockname(sock->sockfd, sa, &slen) >= 0)
			_push_sockname(L, sa);
		else {
			lua_pop(L, 1);
			lua_pushnil(L);
		}
	} else {
		lua_pop(L, 1);
		lua_pushnil(L);
	}
	return 1;
}

/* _canacceptdata
 *
 * helper for all socket methods: check whether a send or receive operation
 * can be performed without blocking
 *
 * Arguments:
 *	fd	socket desctiptor to check
 *	send	flag for whether to test if data can be sent (1) or received (0)
 *
 * Returns:
 *	1 if the operation can be performed, 0 otherwise
 */
static int _canacceptdata(int fd, int send)
{
	fd_set rfds;
	struct timeval tv;
	int ok = -1;
	
	FD_ZERO(&rfds);
	FD_SET(fd, &rfds);
	tv.tv_sec = 0;
	tv.tv_usec = 0;
	
	if (send == 0)
		ok = select(fd + 1, &rfds, NULL, NULL, &tv);
	else
		ok = select(fd + 1, NULL, &rfds, NULL, &tv);

	return ok;
}

/* lsocket_sock_accept
 * 
 * accept a new connection on a socket
 * 
 * Arguments:
 * 	L	Lua State
 * 
 * Lua Stack:
 * 	1	the lSocket userdata
 * 
 * Lua Returns:
 * 	+1	new socket, +2 client ip, +3 client port on success
 *  or +1 false if nonblocking socket returned EAGAIN
 * 	or +1 nil, +2 error message on error
 */
static int lsocket_sock_accept(lua_State *L)
{
	lSocket *sock = lsocket_checklSocket(L, 1);
	char buf[TOSTRING_BUFSIZ];

	if (!_canacceptdata(sock->sockfd, 0)) {
		lua_pushboolean(L, 0);
		return 1;
	}
	
	char sabuf[sizeof(struct sockaddr_in6)];
	struct sockaddr *sa = (struct sockaddr*) sabuf;
	socklen_t slen = sizeof(sabuf);
	int newfd = accept(sock->sockfd, sa, &slen);

	if (newfd < 0)
		return lsocket_error(L, strerror(errno));

	lSocket *nsock = lsocket_pushlSocket(L);
	nsock->sockfd = newfd;
	if (_initsocket(nsock, sa->sa_family, sock->type, sock->mcast, sock->protocol, 0) == -1)
		return lsocket_error(L, strerror(errno));
	lua_pushstring(L, _addr2string(sa, buf, TOSTRING_BUFSIZ));
	lua_pushnumber(L, _portnumber(sa));
	return 3;
}

/* lsocket_sock_recv
 * 
 * reads data from a socket
 * 
 * Arguments:
 * 	L	Lua State
 * 
 * Lua Stack:
 * 	1	the lSocket userdata
 * 	2	(optional) the length of the buffer to use for reading, defaults
 * 		to some internal value
 * 
 * Lua Returns:
 * 	+1	a string containing the data read
 *  or +1 false if nonblocking socket returned EAGAIN (no data available)
 *  or +1 nil if the remote end has closed the socket
 * 	or +1 nil, +2 error message on error
 */
static int lsocket_sock_recv(lua_State *L)
{
	lSocket *sock = lsocket_checklSocket(L, 1);

	uint32_t howmuch = luaL_optnumber(L, 2, READER_BUFSIZ);
	if (lua_tonumber(L, 2) > UINT_MAX)
		return luaL_error(L, "bad argument #1 to 'recv' (invalid number)");
	
	char *buf = malloc(howmuch);
	int nrd = recv(sock->sockfd, buf, howmuch, MSG_DONTWAIT);
	if (nrd < 0) {
		free(buf);
		if (errno == EAGAIN || errno == EWOULDBLOCK)
			lua_pushboolean(L, 0);
		else
			return lsocket_error(L, strerror(errno));
	} else if (nrd == 0)
		lua_pushnil(L);
	else {
		lua_pushlstring(L, buf, nrd);
		free(buf);
	}
	return 1;
}

/* lsocket_sock_recvfrom
 * 
 * reads data from a socket
 * 
 * Arguments:
 * 	L	Lua State
 * 
 * Lua Stack:
 * 	1	the lSocket userdata
 * 	2	(optional) the length of the buffer to use for reading, defaults
 * 		to some internal value
 * 
 * Lua Returns:
 * 	+1	a string containing the data read
 *  +2	ip address if remote end
 *  +3	port of remote end
 *  or +1 false if nonblocking socket returned EAGAIN (no data available)
 *  or +1 nil if the remote end has closed the socket
 * 	or +1 nil, +2 error message on error
 */
static int lsocket_sock_recvfrom(lua_State *L)
{
	lSocket *sock = lsocket_checklSocket(L, 1);
	uint32_t howmuch = luaL_optnumber(L, 2, READER_BUFSIZ);
	if (lua_tonumber(L, 2) > UINT_MAX)
		return luaL_error(L, "bad argument #1 to 'recvfrom' (invalid number)");
	
	char sabuf[sizeof(struct sockaddr_in6)];
	struct sockaddr *sa = (struct sockaddr*) sabuf;
	socklen_t slen = sizeof(sabuf);
	char *buf = malloc(howmuch);
	int nrd = recvfrom(sock->sockfd, buf, howmuch, MSG_DONTWAIT, sa, &slen);
	if (nrd < 0) {
		free(buf);
		if (errno == EAGAIN || errno == EWOULDBLOCK)
			lua_pushboolean(L, 0);
		else
			return lsocket_error(L, strerror(errno));
	} else if (nrd == 0)
		lua_pushnil(L); /* not possible for udp, so should not get here */
	else {
		lua_pushlstring(L, buf, nrd);
		free(buf);
		char ipbuf[TOSTRING_BUFSIZ];
		const char *s = _addr2string(sa, ipbuf, TOSTRING_BUFSIZ);
		if (s)
			lua_pushstring(L, s);
		else
			return lsocket_error(L, strerror(errno)); /* should not happen */
		lua_pushnumber(L, _portnumber(sa));
		return 3;
	}
	return 1;
}

/* lsocket_sock_send
 * 
 * writes data to a socket
 * 
 * Arguments:
 * 	L	Lua State
 * 
 * Lua Stack:
 * 	1	the lSocket userdata
 * 	2	string containing data to be written to socket
 * 
 * Lua Returns:
 * 	+1	the number of bytes written
 *  or +1 false if nonblocking socket returned EAGAIN (not ready to accept data)
 * 	or +1 nil, +2 error message
 */
static int lsocket_sock_send(lua_State *L)
{
	lSocket *sock = lsocket_checklSocket(L, 1);
	size_t len;
	const char *data = luaL_checklstring(L, 2, &len);
	
	int nwr = send(sock->sockfd, data, len, MSG_DONTWAIT);
	
	if (nwr < 0) {
		if (errno == EAGAIN || errno == EWOULDBLOCK)
			lua_pushboolean(L, 0);
		else
			return lsocket_error(L, strerror(errno));
	}
	lua_pushnumber(L, nwr);
	return 1;
}

/* lsocket_sock_sendto
 * 
 * writes data to a socket
 * 
 * Arguments:
 * 	L	Lua State
 * 
 * Lua Stack:
 * 	1	the lSocket userdata
 * 	2	string containing data to be written to socket
 *  3	ip address to sent to
 *  4 	port to send to
 * 
 * Lua Returns:
 * 	+1	the number of bytes written
 *  or +1 false if nonblocking socket returned EAGAIN (not ready to accept data)
 * 	or +1 nil, +2 error message
 */
static int lsocket_sock_sendto(lua_State *L)
{
	lSocket *sock = lsocket_checklSocket(L, 1);
	size_t len;
	const char *data = luaL_checklstring(L, 2, &len);
	const char *addr = luaL_checkstring(L, 3);
	int port = luaL_checknumber(L, 4);
	char sabuf[sizeof(struct sockaddr_in6)];
	struct sockaddr *sa = (struct sockaddr*) sabuf;
	socklen_t slen = sizeof(sabuf);
	int family, protocol;

	int err = _gethostaddr(L, addr, sock->type, port, &family, &protocol, sa, &slen);
	if (err) return err;
	
	int nwr = sendto(sock->sockfd, data, len, MSG_DONTWAIT, sa, slen);
	
	if (nwr < 0) {
		if (errno == EAGAIN || errno == EWOULDBLOCK)
			lua_pushboolean(L, 0);
		else
			return lsocket_error(L, strerror(errno));
	}
	lua_pushnumber(L, nwr);
	return 1;
}

/* lsocket_sock_close
 * 
 * closes a socket
 * 
 * Arguments:
 * 	L	Lua State
 * 
 * Lua Stack:
 * 	1	the lSocket userdata
 * 
 * Lua Returns:
 * 	+1	true
 * 	or +1 nil, +2 error message
 */
static int lsocket_sock_close(lua_State *L)
{
	lSocket *sock = lsocket_checklSocket(L, 1);
	int err = 0;
	if (sock->sockfd >= 0)
		err = close(sock->sockfd);
	sock->sockfd = -1;
	sock->type = -1;
	sock->listening = 0;
	if (err)
		return lsocket_error(L, strerror(errno));
	lua_pushboolean(L, 1);
	return 1;
}

/* socket method list
 */
static const struct luaL_Reg lSocket_methods [] ={
	{"info", lsocket_sock_info},
	{"accept", lsocket_sock_accept},
	{"recv", lsocket_sock_recv},
	{"recvfrom", lsocket_sock_recvfrom},
	{"send", lsocket_sock_send},
	{"sendto", lsocket_sock_sendto},
	{"close", lsocket_sock_close},
	
	{NULL, NULL}
};

/* utility functions */

/* _table2fd_set
 * 
 * helper function for lsocket_select
 * reads sockets (and later maybe files) from the table at idx and inserts their
 * fd's into the fd_set
 * 
 * Arguments:
 * 	L	Lua State
 * 	idx	index at which the table resides
 * 	s	fd_set to fill
 * 
 * Returns:
 * 	the largest fd number found in the table, or -1 if there is nothing
 *  in the table
 */
static int _table2fd_set(lua_State *L, int idx, fd_set *s)
{
	int i = 1;
	int maxfd = -1;
	lua_rawgeti(L, idx, i++);
	while (lsocket_islSocket(L, -1)) {
		lSocket *sock = lsocket_checklSocket(L, -1);
		if (sock->sockfd >= 0) {
			FD_SET(sock->sockfd, s);
			if (sock->sockfd > maxfd) maxfd = sock->sockfd;
		}
		lua_pop(L, 1);
		lua_rawgeti(L, idx, i++);
	}

	if (!lua_isnil(L, -1) && !lsocket_islSocket(L, -1)) {
		lua_pop(L, 1);
		return luaL_error(L, "bad argument to 'select' (tables can only contain sockets)");
	}
	
	lua_pop(L, 1);
	return maxfd;
}

/* _push_sock_for_fd
 * 
 * helper function for lsocket_select
 * finds a socket object in the table at index idx on the lua stack whose
 * file descriptor is equal to fd, and leaves that on the stack.
 * 
 * Arguments:
 * 	L	Lua State
 * 	idx	index at which the table with the lSocket userdatas resides
 * 	fd	file descriptor to search for
 * 
 * Returns:
 * 	1 if a socket was found, which is then left on the stack
 *  0 if no socket was found, nothing on the stack
 */	
static int _push_sock_for_fd(lua_State *L, int idx, int fd)
{
	int i = 1;
	lua_rawgeti(L, idx, i++);
	while (lsocket_islSocket(L, -1)) {
		lSocket *sock = lsocket_checklSocket(L, -1);
		if (sock->sockfd == fd) return 1;
		lua_pop(L, 1);
		lua_rawgeti(L, idx, i++);
	}
	
	if (!lua_isnil(L, -1) && !lsocket_islSocket(L, -1))
		return luaL_error(L, "bad argument to 'select' (tables can only contain sockets)");
	
	return 0;
}

/* _fd_set2table
 * 
 * helper for lsocket_select
 * constructs a table with all entries from the table at idx on the stack that 
 * have a file descriptor that is present in s
 * 
 * Arguments:
 * 	L	Lua State
 * 	idx	index at which the socket table resides
 * 	s	fd_set with socket descriptors returned from select
 * 	maxfd	largets fd to look for
 * 
 * Returns:
 * 	1 + a table with the found sockets on the lua stack
 * 	or 2 + nil+error message on the lua stack
 */
static int _fd_set2table(lua_State *L, int idx, fd_set *s, int maxfd)
{
	int i, n = 1;
	lua_newtable(L);
	for (i = 0; i <= maxfd; ++i) {
		if (FD_ISSET(i, s)) {
			if (_push_sock_for_fd(L, idx, i))
				lua_rawseti(L, -2, n++);
			else
				return luaL_error(L, "unexpected file descriptor returned from select");
		}
	}
	return 1;
}

/* lsocket_select
 * 
 * calls select() on up to 3 tables of sockets, plus timeout
 * 
 * Arguments:
 * 	L	Lua State
 * 
 * Lua Stack:
 * 	1	(opt) sockets to wait for reading
 * 	2	(opt) sockets to wait for writing
 *  3	(opt) timeout
 * 
 * Lua Returns:
 * 	+1...+2 tables of sockets from the corresponding tables passed as
 * 		arguments that have become ready
 *	or +1 false on timeout
 * 	or +1 nil, +2 error message
 */
static int lsocket_select(lua_State *L)
{
	fd_set readfd, writefd;
	int hasrd = 0, haswr = 0;
	double timeo = 0;
	struct timeval timeout, *timeop = &timeout;
	int top = 1;
	int maxfd = -1, mfd;
	int nargs = lua_gettop(L);
	
	FD_ZERO(&readfd);
	FD_ZERO(&writefd);
	
	if (lua_istable(L, 1)) {
		mfd = _table2fd_set(L, 1, &readfd);
		if (mfd > maxfd) maxfd = mfd;
		hasrd = 1;
		top = 2;
	}
	if (lua_istable(L, 2)) {
		mfd = _table2fd_set(L, 2, &writefd);
		if (mfd > maxfd) maxfd = mfd;
		haswr = 1;
		top = 3;
	}

	timeo = luaL_optnumber(L, top, -1);
	
	if (maxfd < 0 && timeo == -1) return lsocket_error(L, "no open sockets to check and no timeout set");
	if (top < nargs) luaL_error(L, "bad argument to 'select' (invalid option)");
	
	if (timeo < 0) {
		timeop = NULL;
	} else {
		timeout.tv_sec = (long) timeo;
		timeout.tv_usec = (timeo - timeout.tv_sec) * 1000000;
	}
	
	int ok = select(maxfd+1, hasrd ? &readfd : NULL, haswr ? &writefd : NULL, NULL, timeop);
	
	if (ok == 0) {
		lua_pushboolean(L, 0);
		return 1;
	} else if (ok < 0)
		return lsocket_error(L, strerror(errno));

	int nret = 0;
	if (hasrd) {
		_fd_set2table(L, 1, &readfd, maxfd);
		nret = 1;
	}
	if (haswr) {
		if (!hasrd) {
			lua_pushliteral(L, LSOCKET_EMPTY);
			lua_gettable(L, LUA_REGISTRYINDEX);
		}
		_fd_set2table(L, 2, &writefd, maxfd);
		nret = 2;
	}
	
	return nret;
}

/* lsocket_resolve
 * 
 * resolves a name to an address
 * 
 * Arguments:
 * 	L	lua State
 * 
 * Lua Stack:
 * 	1	name of host to resolve
 * 
 * Lua Returns:
 * 	+1	a table of all addresses the argument resolves to. For each address,
 * 		a subtable with the fields family and addr is created.
 */
static int lsocket_resolve(lua_State *L)
{
	const char *name = luaL_checkstring(L, 1);
	char buf[TOSTRING_BUFSIZ];
	struct addrinfo hint, *info =0;
	memset(&hint, 0, sizeof(hint));
	hint.ai_family = AF_UNSPEC;
	if (_needsnolookup(name))
		hint.ai_flags = AI_NUMERICHOST;

	int err = getaddrinfo(name, 0, &hint, &info);
    if (err != 0) {
		if (info) freeaddrinfo(info);
		return lsocket_error(L, gai_strerror(err));
	}

	int i = 1;
	lua_newtable(L);
	while (info) {
		if (info->ai_family == AF_INET || info->ai_family == AF_INET6) {
			lua_newtable(L);
			lua_pushliteral(L, "family");
			lua_pushstring(L, info->ai_family == AF_INET ? LSOCKET_INET : LSOCKET_INET6);
			lua_rawset(L, -3);
			lua_pushliteral(L, "addr");
			lua_pushstring(L, _addr2string(info->ai_addr, buf, TOSTRING_BUFSIZ));
			lua_rawset(L, -3);
			lua_rawseti(L, -2, i++);
			info = info->ai_next;
		}
		/* silently ignore unknown address families */
	}
	
	freeaddrinfo(info);
	return 1;
}

/* lsocket_getinterfaces
 * 
 * enumerates all available interfaces
 * 
 * Arguments:
 * 	L	lua State
 * 
 * Lua Stack:
 * 	-
 * 
 * Lua Returns:
 * 	+1	a table with information on all available interfaces. For each
 * 		interface, a subtable is returned with the fields name, family,
 * 		address and mask
 */
static int lsocket_getinterfaces(lua_State *L)
{
	struct ifaddrs *ifa;
	char buf[TOSTRING_BUFSIZ];
	const char *s;
	int i = 1;

	if (getifaddrs(&ifa) < 0)
		return lsocket_error(L, strerror(errno));
	
	lua_newtable(L);
	while (ifa) {
		lua_newtable(L);
		lua_pushliteral(L, "name");
		lua_pushstring(L, ifa->ifa_name);
		lua_rawset(L, -3);
		s = _addr2string(ifa->ifa_addr, buf, TOSTRING_BUFSIZ);
		if (s) {
			lua_pushliteral(L, "family");
			lua_pushstring(L, ifa->ifa_addr->sa_family == AF_INET ? LSOCKET_INET : LSOCKET_INET6);
			lua_rawset(L, -3);
			lua_pushliteral(L, "addr");
			lua_pushstring(L, s);
			lua_rawset(L, -3);
			s = _addr2string(ifa->ifa_netmask, buf, TOSTRING_BUFSIZ);
			if (s) {
				lua_pushliteral(L, "mask");
				lua_pushstring(L, s);
				lua_rawset(L, -3);
			}
			lua_rawseti(L, -2, i++);
		} else
			lua_pop(L, 1);
		ifa = ifa->ifa_next;
	}

	freeifaddrs(ifa);
	return 1;
}

/* Function list
 */
static const struct luaL_Reg lsocket [] ={
	{"connect", lsocket_connect},
	{"bind", lsocket_bind},
	{"select", lsocket_select},
	{"resolve", lsocket_resolve},
	{"getinterfaces", lsocket_getinterfaces},
	
	{NULL, NULL}
};

/* lsocket_ignore
 * 
 * helper function: attached as __newindex to empty table to inhibit
 * changes.
 */
static int lsocket_ignore(lua_State *L)
{
	return 0;
}

/* luaopen_lsocket
 * 
 * open and initialize this library
 */
int luaopen_lsocket(lua_State *L)
{
	luaL_newlib(L, lsocket);

	/* add constants */
	lua_pushliteral(L, "INADDR_ANY");
	lua_pushliteral(L, "0.0.0.0");
	lua_rawset(L, -3);

	lua_pushliteral(L, "IN6ADDR_ANY");
	lua_pushliteral(L, "::0");
	lua_rawset(L, -3);

	lua_pushliteral(L, "_VERSION");
	lua_pushliteral(L, LSOCKET_VERSION);
	lua_rawset(L, -3);

	/* add lSocket userdata metatable */
	luaL_newmetatable(L, LSOCKET);
	luaL_setfuncs(L, lSocket_meta, 0);
	/* methods */
	lua_pushliteral(L, "__index");
	luaL_newlib(L, lSocket_methods);
	lua_rawset(L, -3);
	/* type */
	lua_pushliteral(L, "__type");
	lua_pushstring(L, LSOCKET);
	lua_rawset(L, -3);
	/* cleanup */
	lua_pop(L, 1);

	/* finally, create an empty table and store it in the registry, so
	 * that we don't create loads of garbage for empty tables we might
	 * return from select */
	lua_newtable(L);	/* empty table */
	lua_newtable(L);	/* metatable */
	lua_pushliteral(L, "__newindex");
	lua_pushcfunction(L, lsocket_ignore);
	lua_rawset(L, -3);
	lua_pushliteral(L, "__metatable");
	lua_pushboolean(L, 0);
	lua_rawset(L, -3);
	lua_setmetatable(L, -2);
	lua_pushliteral(L, LSOCKET_EMPTY);
	lua_pushvalue(L, -2);
	lua_settable(L, LUA_REGISTRYINDEX);
	lua_pop(L, 1);

	/* if we don't ignore SIGPIPE, such a signal will just kill the application */
	signal(SIGPIPE, SIG_IGN);

	return 1;
}
