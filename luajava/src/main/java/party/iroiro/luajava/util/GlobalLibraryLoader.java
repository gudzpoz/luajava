package party.iroiro.luajava.util;

import com.badlogic.gdx.utils.Os;
import com.badlogic.gdx.utils.SharedLibraryLoadRuntimeException;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import party.iroiro.luajava.LuaNatives;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A class support loading classes with {@code RTLD_GLOBAL}.
 *
 * <h2>{@link SharedLibraryLoader} Internals</h2>
 *
 * <ul>
 *     <li>{@link SharedLibraryLoader#load(String)}
 *     <ul>
 *         <li>Uses {@link SharedLibraryLoader#mapLibraryName(String)} to get the filename of the library.</li>
 *         <li>Calls {@link System#loadLibrary(String)} on Android.</li>
 *         <li>Calls private {@code SharedLibraryLoader#loadFile(String)} on other platforms.</li>
 *     </ul>
 *     </li>
 *     <li>{@code SharedLibraryLoader#loadFile(String)} (private)
 *     <ul>
 *         <li>Writes to temporary paths: (tries in the following order)
 *         <ul>
 *             <li>{@code {java.io.tmpdir}/libgdx{username}/{crc}/{filename}}</li>
 *             <li>{@code File.createTempFile({crc}, null)}</li>
 *             <li>{@code {user.home}/.libgdx/{crc}/{filename}}</li>
 *             <li>{@code ./.temp/{crc}/{filename}}</li>
 *         </ul>
 *         </li>
 *         <li>Defaults to {@code {java.library.path}/{filename}}</li>
 *     </ul>
 *     </li>
 * </ul>
 *
 * <p>Most of the code above is private, and we have to work around this a bit.</p>
 */
public class GlobalLibraryLoader {
    private final static SharedLibraryLoader loader = new SharedLibraryLoader();
    private static volatile Class<? extends LuaNatives> loadedNatives = null;
    private static volatile int nativesLoaded = 0;

    private static InputStream readFile(String path) {
        InputStream input = SharedLibraryLoader.class.getResourceAsStream("/" + path);
        if (input == null) {
            throw new SharedLibraryLoadRuntimeException("Unable to read file for extraction: " + path);
        }
        return input;
    }

    /**
     * Loads a library and returns the path to it.
     *
     * @param libraryName the name of the library (without prefix or suffix)
     * @return the extracted path
     */
    public static String load(String libraryName) {
        loader.load(libraryName);
        if (SharedLibraryLoader.os == Os.IOS) {
            return "";
        }
        String fileName = loader.mapLibraryName(libraryName);
        if (SharedLibraryLoader.os == Os.Android) {
            return fileName;
        }
        String sourceCrc = loader.crc(readFile(fileName));
        File[] paths = {
                new File(
                        System.getProperty("java.io.tmpdir") + "/libgdx"
                        + System.getProperty("user.name") + "/" + sourceCrc,
                        fileName
                ),
        };
        for (File path : paths) {
            try {
                if (path.exists() && sourceCrc.equals(loader.crc(new FileInputStream(path)))) {
                    return path.toString();
                }
            } catch (FileNotFoundException ignored) {
            }
        }
        throw new SharedLibraryLoadRuntimeException("Unable to locate the library path");
    }

    /**
     * Marks natives of a certain version as loaded, used to prevent JVM crashes from incompatible symbols.
     *
     * <ul>
     *     <li>{@code loadedNatives == null && nativesLoaded == 0}: None loaded.</li>
     *     <li>{@code loadedNatives != null && nativesLoaded > 0}: Some loaded.</li>
     *     <li>{@code loadedNatives != null && nativesLoaded == 0}: Global loaded.</li>
     *     <li>{@code loadedNatives == null && nativesLoaded > 0}: Never.</li>
     * </ul>
     *
     * @param natives the natives to be loaded
     * @param global  whether the natives are
     */
    public synchronized static void register(Class<? extends LuaNatives> natives, boolean global) {
        if (loadedNatives == null && nativesLoaded == 0) {
            loadedNatives = natives;
            nativesLoaded = global ? 0 : 1;
            return;
        }
        if (global) {
            if (loadedNatives == natives && nativesLoaded == 1) {
                // Natives already loaded either:
                // - as the only RTLD_LOCAL library,
                // - or with RTLD_GLOBAL.
                nativesLoaded = 0;
                return;
            }
            // Other natives already loaded, rejecting making it global.
            throw new SharedLibraryLoadRuntimeException(
                    "Library " + loadedNatives.getName()
                    + " already loaded when loading " + natives.getName() + " globally"
            );
        } else {
            if (loadedNatives != null && nativesLoaded == 0 && loadedNatives != natives) {
                // Global library already loaded.
                throw new SharedLibraryLoadRuntimeException(
                        "Global library " + loadedNatives.getName()
                        + " already loaded when loading " + natives.getName()
                );
            }
            // Already loaded as global, or all others loaded as local.
            loadedNatives = natives;
            nativesLoaded++;
        }
    }
}
