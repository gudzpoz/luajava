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

package io.nondev.nonlua;

import java.io.File;
import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildConfig;
import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.jnigen.NativeCodeGenerator;
import com.badlogic.gdx.jnigen.BuildTarget.TargetOs;

public class LuaBuild {
    public static void main (String[] args) throws Exception {
        BuildTarget win32 = BuildTarget.newDefaultTarget(TargetOs.Windows, false);
        win32.linkerFlags += " -lws2_32 -lwinmm";
        BuildTarget win64 = BuildTarget.newDefaultTarget(TargetOs.Windows, true);
        win64.linkerFlags += " -lws2_32 -lwinmm";
        BuildTarget lin32 = BuildTarget.newDefaultTarget(TargetOs.Linux, false);
        BuildTarget lin64 = BuildTarget.newDefaultTarget(TargetOs.Linux, true);
        BuildTarget android = BuildTarget.newDefaultTarget(TargetOs.Android, false);
        BuildTarget mac32 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, false);
        BuildTarget mac64 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, true);
        BuildTarget ios = BuildTarget.newDefaultTarget(TargetOs.IOS, false);
        new NativeCodeGenerator().generate("src", "bin/classes", "jni");
        new AntScriptGenerator().generate(new BuildConfig("nonlua"), win32, win64, lin32, lin64, mac32, mac64, android, ios);
    }
}