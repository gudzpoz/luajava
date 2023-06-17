package party.iroiro.luajava.util;

import com.badlogic.gdx.utils.SharedLibraryLoadRuntimeException;
import com.badlogic.gdx.utils.SharedLibraryLoader;

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
 *         <li>Defautls to {@code {java.library.path}/{filename}}</li>
 *     </ul>
 *     </li>
 * </ul>
 *
 * <p>Most of the code above is private, and we have to work around this a bit.</p>
 */
public class GlobalLibraryLoader {
    private final static SharedLibraryLoader loader = new SharedLibraryLoader();

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
        if (SharedLibraryLoader.isIos) {
            return "";
        }
        String fileName = loader.mapLibraryName(libraryName);
        if (SharedLibraryLoader.isAndroid) {
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
}
