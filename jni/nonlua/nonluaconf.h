/*******************************************************************************
 * Copyright (c) 2015 Thomas Slusny.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

#ifndef nonconf_h
#define nonconf_h

#define NONLUA_API LUA_API

#define NONLUA_CPTRCLASS "io/nondev/nonlua/CPtr"
#define NONLUA_FUNCTIONCLASS "io/nondev/nonlua/LuaFunction"
#define NONLUA_LUAJAVACLASS "io/nondev/nonlua/LuaJava"

#define NONLUA_JNIENVTAG "__nonluajnienv"
#define NONLUA_STATEINDEX "nonluastateindex"
#define NONLUA_ISJAVAOBJECT "__nonluaisjavaobject"
#define NONLUA_ISFUNCCALLED "__nonluaisfunccalled"

#define LUA_INDEXTAG "__index"
#define LUA_NEWINDEXTAG "__newindex"
#define LUA_GCTAG "__gc"
#define LUA_CALLTAG "__call"

#endif