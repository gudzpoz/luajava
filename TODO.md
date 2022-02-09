# What On Earth

## TODO

- Modify the C++ code part BACK to what it looked like in LuaJava, since nonlua devs seems to have just appended "nonlua_" prefix AND RID EVERY COMMENT.
  - I mean I do not understand why any one copying code would rid the comments (and add their own prefix, wow).
  - LuaJava: https://github.com/jasonsantos/luajava/blob/master/src/c/luajava.c

库A，不断在更新大版本，诸如版本A1、A2、A3等等，但新版本不保证兼容旧版本。一个版本也够用，所以也没什么问题。

有人觉得可以用高科技优化A的性能，于是弄了一个库Ab，原本也是Ab1、Ab2这样跟着大版本走的，逐渐推出一些新功能，但原来的库A2到A3的时候跳跃太大，有些争议，于是Ab它索性就直接留在Ab2这个阶段了。

Ab这个库性能的确很好，但是中途停更了一段时间，大家担心它停止维护了，于是纷纷在原来的基础上弄了自己的版本，比如Aba2、Abb2、Abc2等等。

这也不是重点。

编程有很多种编程语言，A、Ab用的是同一体系的语言（不如代称为C），但是对于其它编程语言的使用者（如D、E、F等）使用这两个库都还不太方便，于是又有大佬们推出了自己实现的版本，譬如AD、AEA、AEB等等。这也还不是重点。

当然，比如我们在某个D语言（真的有D语言，但我们这里只是代称）里想使用A的功能，我们不一定要用AD，我们也可以用某些特殊的方法来直接（或间接）使用A。有人实现了这样的库，比如称为AonD。当然对于想要追求性能的人来说，为什么我们不用AbonD呢？欸，真的有这样的库。<del>虽然很久没维护了，虽然基本是照抄AonD的代码，虽然照抄了代码还把人家注释给删了还把函数名改得一塌糊涂让人看起来很难受。</del>

好的其实这段文字就是没有任何重点。只是自己有点地铁老人看手机罢了……打扰了

- Exception handling: use C++ exception to raise an exception to the calling Java native method

- Try to follow suit and use some Jalmag.h to include all source file into one big file

- Write the tests (first copy from LuaJava)

- Rid unnecessary checks (probably wrap with `#ifdef DEBUG` etc.)

- Cache more methodID's

