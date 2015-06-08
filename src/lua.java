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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.nondev.nonlua.Lua;
import io.nondev.nonlua.LuaException;

public class lua {
   public static void main(String[] args) {
      try {
         Lua L = new Lua();

         if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
               int res = L.run(args[i]);
               if (res != 0) {
                  throw new LuaException("Error on file: " + args[i] + ". " + L.toString(-1));
               }
            }

            return;
         }

         System.out.println("nonlua");

         BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));

         String line;

         System.out.print("> ");
         while ((line = inp.readLine()) != null && !line.equals("exit")) {
            int ret = L.load(line);
            if (ret == 0) {
               synchronized (L) {
               	ret = L.pcall(0, 0, 0);	
					}
            } else {
               System.err.println("Error on line: " + line);
               System.err.println(L.toString(-1));
            }

            System.out.print("> ");
         }

         L.dispose();
      } catch (Exception e) {
         e.printStackTrace();
      }

   }
}