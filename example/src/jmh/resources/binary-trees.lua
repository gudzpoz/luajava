-- Copyright © 2004-2008 Brent Fulgham, 2005-2024 Isaac Gouy
-- All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without
-- modification, are permitted provided that the following conditions are met:
--
-- 1. Redistributions of source code must retain the above copyright notice,
-- this list of conditions and the following disclaimer.
--
-- 2. Redistributions in binary form must reproduce the above copyright notice,
-- this list of conditions and the following disclaimer in the documentation
-- and/or other materials provided with the distribution.
--
-- 3. Neither the name "The Computer Language Benchmarks Game" nor the name "The
-- Benchmarks Game" nor the name "The Computer Language Shootout Benchmarks" nor
-- the names of its contributors may be used to endorse or promote products
-- derived from this software without specific prior written permission.
--

-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
-- AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
-- IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
-- ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
-- LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
-- CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
-- SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
-- INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
-- CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
-- ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
-- POSSIBILITY OF SUCH DAMAGE.

-- The Computer Language Benchmarks Game
-- https://salsa.debian.org/benchmarksgame-team/benchmarksgame/
-- contributed by Mike Pall
-- *reset*

local function BottomUpTree(depth)
  if depth > 0 then
    depth = depth - 1
    local left, right = BottomUpTree(depth), BottomUpTree(depth)
    return { left, right }
  else
    return { }
  end
end

local function ItemCheck(tree)
  if tree[1] then
    return 1 + ItemCheck(tree[1]) + ItemCheck(tree[2])
  else
    return 1
  end
end

local N = tonumber(arg and arg[1]) or 0
local mindepth = 4
local maxdepth = mindepth + 2
if maxdepth < N then maxdepth = N end

do
  local stretchdepth = maxdepth + 1
  local stretchtree = BottomUpTree(stretchdepth)
  io.write(string.format("stretch tree of depth %d\t check: %d\n",
    stretchdepth, ItemCheck(stretchtree)))
end

local longlivedtree = BottomUpTree(maxdepth)

for depth=mindepth,maxdepth,2 do
  local iterations = 2 ^ (maxdepth - depth + mindepth)
  local check = 0
  for i=1,iterations do
    check = check + ItemCheck(BottomUpTree(depth))
  end
  io.write(string.format("%d\t trees of depth %d\t check: %d\n",
    iterations, depth, check))
end

io.write(string.format("long lived tree of depth %d\t check: %d\n",
  maxdepth, ItemCheck(longlivedtree)))
