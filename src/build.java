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

import java.io.File;
import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildConfig;
import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.jnigen.NativeCodeGenerator;
import com.badlogic.gdx.jnigen.BuildTarget.TargetOs;

public class build {
    public static void main (String[] args) throws Exception {
        String[] headers = {"nonlua", "lua", "luasocket"};

        BuildTarget win32 = BuildTarget.newDefaultTarget(TargetOs.Windows, false);
        win32.headerDirs = headers;
        win32.cFlags += " -DLUASOCKET_INET_PTON";
        win32.cppFlags += " -DLUASOCKET_INET_PTON";
        win32.libraries = "-lws2_32 -lwinmm";
        
        BuildTarget win64 = BuildTarget.newDefaultTarget(TargetOs.Windows, true);
        win64.headerDirs = headers;
        win64.cFlags += " -DLUASOCKET_INET_PTON";
        win64.cppFlags += " -DLUASOCKET_INET_PTON";
        win64.libraries = "-lws2_32 -lwinmm";
        
        BuildTarget lin32 = BuildTarget.newDefaultTarget(TargetOs.Linux, false);
        lin32.headerDirs = headers;
        lin32.cFlags += " -DLUASOCKET_INET_PTON";
        lin32.cppFlags += " -DLUASOCKET_INET_PTON";
        
        BuildTarget lin64 = BuildTarget.newDefaultTarget(TargetOs.Linux, true);
        lin64.headerDirs = headers;
        lin64.cFlags += " -DLUASOCKET_INET_PTON";
        lin64.cppFlags += " -DLUASOCKET_INET_PTON";
        
        BuildTarget android = BuildTarget.newDefaultTarget(TargetOs.Android, false);
        android.headerDirs = headers;

        BuildTarget mac32 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, false);
        mac32.headerDirs = headers;

        BuildTarget mac64 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, true);
        mac64.headerDirs = headers;

        BuildTarget ios = BuildTarget.newDefaultTarget(TargetOs.IOS, false);
        ios.headerDirs = headers;

        new NativeCodeGenerator().generate("src", "target/classes", "jni");
        new AntScriptGenerator().generate(new BuildConfig("nonlua"), win32, win64, lin32, lin64, mac32, mac64, android, ios);
    }
}