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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

public final class LuaUtils {
    public static Number convertNumber(Double db, Class retType) {
        if (retType.isPrimitive()) {
            if (retType == Integer.TYPE) {
                return new Integer(db.intValue());
            } else if (retType == Float.TYPE) {
                return new Float(db.floatValue());
            } else if (retType == Double.TYPE) {
                return db;
            } else if (retType == Byte.TYPE) {
                return new Byte(db.byteValue());
            } else if (retType == Long.TYPE) {
                return new Long(db.longValue());
            } else if (retType == Short.TYPE) {
                return new Short(db.shortValue());
            }
        } else if (retType.isAssignableFrom(Number.class)) {
            if (retType.isAssignableFrom(Integer.class)) {
                return new Integer(db.intValue());
            } else if (retType.isAssignableFrom(Float.class)) {
                return new Float(db.floatValue());
            } else if (retType.isAssignableFrom(Double.class)) {
                return db;
            } else if (retType.isAssignableFrom(Byte.class)) {
                return new Byte(db.byteValue());
            } else if (retType.isAssignableFrom(Long.class)) {
                return new Long(db.longValue());
            } else if (retType.isAssignableFrom(Short.class)) {
                return new Short(db.shortValue());
            }
        }
    
        return null;    
    }

    public static InputStream getStream(LuaLoader loader, String path) throws IOException {
        File file = new File(loader.path(), path);

        if (file.exists()) {
            return new FileInputStream(file);
        }
        
        return LuaUtils.class.getResourceAsStream("/" + file.getPath().replace('\\', '/'));
    }

    public static String readStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        
        return out.toString();
    }
}