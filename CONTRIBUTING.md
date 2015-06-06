# Contributing to nonlua

Please take a moment to review this document in order to make the contribution
process easy and effective for everyone involved.

Following these guidelines helps to communicate that you respect the time of
the developers managing and developing this open source project. In return,
they should reciprocate that respect in addressing your issue or assessing
patches and features.


## Using the issue tracker

The issue tracker is the preferred channel for [bug reports](#bug-reports),
[features requests](#feature-requests) and [submitting pull
requests](#pull-requests), but please respect the following restrictions:

* Please **do not** use the issue tracker for personal support requests

* Please **do not** derail or troll issues. Keep the discussion on topic and
  respect the opinions of others.


## Bug reports

A bug is a _demonstrable problem_ that is caused by the code in the repository.
Good bug reports are extremely helpful - thank you!

Guidelines for bug reports:

1. **Use the GitHub issue search** &mdash; check if the issue has already been
   reported. Make sure to search all issues, not only the open issues.

2. **Check if the issue has been fixed** &mdash; try to reproduce it using the
   latest `master` or development branch in the repository.

3. **Isolate the problem** &mdash; create a reduced test case

A good bug report shouldn't leave others needing to chase you up for more
information. Please try to be as detailed as possible in your report. What is
your environment? What steps will reproduce the issue? What OS experience 
the problem? What would you expect to be the outcome? All these details will 
help people to fix any potential bugs.

## Feature requests

Feature requests are welcome. But take a moment to find out whether your idea
fits with the scope and aims of the project. It's up to *you* to make a strong
case to convince the project's developers of the merits of this feature. Please
provide as much detail and context as possible.

## Pull requests

Contributing to nonlua is easy:

  * Fork nonlua on http://github.com/nodev/nonlua

  * Follow [the coding guidelines](#coding-guidelines)

  * Hack away and sent a pull request on Github!

If you are submitting pull request what is modifying only documentation,
always add [ci skip] to the end of your commit message.

## Coding guidelines

If you modify an existing file, follow the style of the code in there.

If you create a new file, make sure to add the MIT license file header,
as seen [here](https://github.com/nondev/nonlua/blob/master/src/io/nondev/nonlua/Lua.java).

### Java

nonlua does not have an official coding standard. But try to follow the
usual [Java style](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html).

A few things we'd rather not like to see:

  * Underscores in any kind of identifier

  * [Hungarian notation](http://en.wikipedia.org/wiki/Hungarian_notation)

  * Prefixes for fields or arguments

  * Curlies on new lines

  * Conditional block bodies without curlies

  * Avoid locking, nonlua is mostly not thread-safe

  * Avoid temporary object allocation wherever possible (because of
    performance issues with Dalvik)

### Native

Here are few points you should consider while submitting pull rewuests with
native code changes:

  * Include proper MIT license header (as defined in [java section](#java) )

  * If you are adding JNI function, always use features of gdx-jnigen,
    as seen [here](https://github.com/nondev/nonlua/blob/master/src/io/nondev/nonlua/Lua.java)

  * Submit only code, which will work on all platforms what nonlua supports
    (Windows, Linux, Mac OS X, Android and iOS)