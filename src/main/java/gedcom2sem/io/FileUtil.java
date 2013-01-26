package gedcom2sem.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import com.hp.hpl.jena.util.FileUtils;

public class FileUtil
{
    /**
     * @see FileUtils#guessLang
     * @param file
     * @return null for unknown extension
     * @throws MalformedURLException
     */

    public static String guessLanguage(File file) throws MalformedURLException
    {
        return FileUtils.guessLang(file.toURI().toURL().toString(), null);
    }

    /**
     * Reads the whole file.
     * 
     * @param file
     *        UTF-8 encoding assumed.
     * @return
     * @throws IOException
     */
    public static String read(final File file) throws IOException
    {
        final byte[] bytes = new byte[(int) file.length()];
        final FileInputStream inputStream = new FileInputStream(file);
        try
        {
            inputStream.read(bytes);
        }
        finally
        {
            inputStream.close();
        }
        return new String(bytes, "UTF-8");
    }

}