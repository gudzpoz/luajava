/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package io.nondev.nonlua.thirdparty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** 
 * @author Thomas Slusny
 * @author mzechner
 * @author Nathan Sweet */
public class ResourcePathFinder {
	static private final HashMap<String, String> foundPaths = new HashMap();
	static public boolean isWindows = System.getProperty("os.name").contains("Windows");
	static public boolean isLinux = System.getProperty("os.name").contains("Linux");
	static public boolean isMac = System.getProperty("os.name").contains("Mac");
	static public boolean isIos = false;
	static public boolean isAndroid = false;
	static public boolean isARM = System.getProperty("os.arch").startsWith("arm");
	static public boolean is64Bit = System.getProperty("os.arch").equals("amd64") || System.getProperty("os.arch").equals("x86_64");
	static public String abi = (System.getProperty("sun.arch.abi") != null ? System.getProperty("sun.arch.abi") : "");

	static {
		String vm = System.getProperty("java.runtime.name");
		if (vm != null && vm.contains("Android Runtime")) {
			isAndroid = true;
			isWindows = false;
			isLinux = false;
			isMac = false;
			is64Bit = false;
		}
		if (!isAndroid && !isWindows && !isLinux && !isMac) {
			isIos = true;
			is64Bit = false;
		}
	}

	public synchronized String findLibrary(String libraryName) {
		return findResource(mapLibraryName(libraryName));
	}

	public synchronized String findResource (String resourceName) {
		if (foundPaths.containsKey(resourceName)) return foundPaths.get(resourceName);

		String path = null;
		File f = new File(resourceName);

		if (f.exists()) {
			path = f.getAbsolutePath();
		}

		try {
			path = loadFile(resourceName);
		} catch (Throwable ex) {
			throw new RuntimeException("Couldn't load resource '" + resourceName + "' for target: "
				+ System.getProperty("os.name") + (is64Bit ? ", 64-bit" : ", 32-bit"), ex);
		}

		if (path != null) foundPaths.put(resourceName, path);
		return path;
	}

	private String crc (InputStream input) {
		if (input == null) throw new IllegalArgumentException("input cannot be null.");
		CRC32 crc = new CRC32();
		byte[] buffer = new byte[4096];

		try {
			while (true) {
				int length = input.read(buffer);
				if (length == -1) break;
				crc.update(buffer, 0, length);
			}
		} catch (Exception ex) {
			if (input != null) {
				try { input.close(); }
				catch (Exception ignored) { }
			}
		}
		return Long.toString(crc.getValue(), 16);
	}

	private String mapLibraryName (String libraryName) {
		if (isWindows) return libraryName + (is64Bit ? "64.dll" : ".dll");
		if (isLinux) return "lib" + libraryName + (isARM ? "arm" + abi : "") + (is64Bit ? "64.so" : ".so");
		if (isMac) return "lib" + libraryName + (is64Bit ? "64.dylib" : ".dylib");
		return libraryName;
	}

	private InputStream readFile (String path) {
		InputStream input = ResourcePathFinder.class.getResourceAsStream("/" + path);
		if (input == null) throw new RuntimeException("Unable to read file for extraction: " + path);
		return input;
	}

	public File extractFile (String sourcePath, String dirName) throws IOException {
		try {
			String sourceCrc = crc(readFile(sourcePath));
			if (dirName == null) dirName = sourceCrc;

			File extractedFile = getExtractedFile(dirName, new File(sourcePath).getName());
			return extractFile(sourcePath, sourceCrc, extractedFile);
		} catch (RuntimeException ex) {
			File file = new File(System.getProperty("java.library.path"), sourcePath);
			if (file.exists()) return file;
			throw ex;
		}
	}

	private File getExtractedFile (String dirName, String fileName) {
		File idealFile = new File(System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/"
			+ dirName, fileName);
		if (canWrite(idealFile)) return idealFile;

		try {
			File file = File.createTempFile(dirName, null);
			if (file.delete()) {
				file = new File(file, fileName);
				if (canWrite(file)) return file;
			}
		} catch (IOException ignored) { }

		File file = new File(System.getProperty("user.home") + "/.libgdx/" + dirName, fileName);
		if (canWrite(file)) return file;

		file = new File(".temp/" + dirName, fileName);
		if (canWrite(file)) return file;

		return idealFile;
	}

	private boolean canWrite (File file) {
		File parent = file.getParentFile();
		File testFile;
		if (file.exists()) {
			if (!file.canWrite() || !canExecute(file)) return false;
			testFile = new File(parent, UUID.randomUUID().toString());
		} else {
			parent.mkdirs();
			if (!parent.isDirectory()) return false;
			testFile = file;
		}
		try {
			new FileOutputStream(testFile).close();
			if (!canExecute(testFile)) return false;
			return true;
		} catch (Throwable ex) {
			return false;
		} finally {
			testFile.delete();
		}
	}

	private boolean canExecute (File file) {
		try {
			Method canExecute = File.class.getMethod("canExecute");
			if ((Boolean)canExecute.invoke(file)) return true;

			Method setExecutable = File.class.getMethod("setExecutable", boolean.class, boolean.class);
			setExecutable.invoke(file, true, false);

			return (Boolean)canExecute.invoke(file);
		} catch (Exception ignored) { }

		return false;
	}

	private File extractFile (String sourcePath, String sourceCrc, File extractedFile) throws IOException {
		String extractedCrc = null;
		if (extractedFile.exists()) {
			try {
				extractedCrc = crc(new FileInputStream(extractedFile));
			} catch (FileNotFoundException ignored) {
			}
		}

		if (extractedCrc == null || !extractedCrc.equals(sourceCrc)) {
			try {
				InputStream input = readFile(sourcePath);
				extractedFile.getParentFile().mkdirs();
				FileOutputStream output = new FileOutputStream(extractedFile);
				byte[] buffer = new byte[4096];
				while (true) {
					int length = input.read(buffer);
					if (length == -1) break;
					output.write(buffer, 0, length);
				}
				input.close();
				output.close();
			} catch (IOException ex) {
				throw new RuntimeException("Error extracting file: " + sourcePath + "\nTo: " + extractedFile.getAbsolutePath(), ex);
			}
		}

		return extractedFile;
	}

	private String loadFile (String sourcePath) {
		String sourceCrc = crc(readFile(sourcePath));

		String fileName = new File(sourcePath).getName();

		File file = new File(System.getProperty("java.io.tmpdir") + "/libgdx" + System.getProperty("user.name") + "/" + sourceCrc,
			fileName);
		String path = loadFile(sourcePath, sourceCrc, file);
		if (path != null) return path;

		try {
			file = File.createTempFile(sourceCrc, null);
			if (file.delete()) {
				path = loadFile(sourcePath, sourceCrc, file);
				if (path != null) return path;
			}
		} catch (Throwable ignored) { }

		file = new File(System.getProperty("user.home") + "/.libgdx/" + sourceCrc, fileName);
		path = loadFile(sourcePath, sourceCrc, file);
		if (path != null) return path;

		file = new File(".temp/" + sourceCrc, fileName);
		path = loadFile(sourcePath, sourceCrc, file);
		if (path != null) return path;

		file = new File(System.getProperty("java.library.path"), sourcePath);
		if (file.exists()) {
			return file.getAbsolutePath();
		}

		return null;
	}

	private String loadFile (String sourcePath, String sourceCrc, File extractedFile) {
		try {
			return extractFile(sourcePath, sourceCrc, extractedFile).getAbsolutePath();
		} catch (Throwable ex) {
			return null;
		}
	}
}
