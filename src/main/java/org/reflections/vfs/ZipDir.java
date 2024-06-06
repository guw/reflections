package org.reflections.vfs;

import com.google.common.collect.AbstractIterator;
import org.reflections.Reflections;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/** an implementation of {@link org.reflections.vfs.Vfs.Dir} for {@link java.util.zip.ZipFile} */
public class ZipDir implements Vfs.Dir {
    final java.util.zip.ZipFile jarFile;

    public ZipDir(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    @Override
    public String getPath() {
        return jarFile.getName();
    }

    @Override
    public Iterable<Vfs.File> getFiles() {
        return new Iterable<Vfs.File>() {
            @Override
            public Iterator<Vfs.File> iterator() {
                return new AbstractIterator<Vfs.File>() {
                    final Enumeration<? extends ZipEntry> entries = jarFile.entries();

                    @Override
                    protected Vfs.File computeNext() {
                        while (entries.hasMoreElements()) {
                            ZipEntry entry = entries.nextElement();
                            if (!entry.isDirectory()) {
                                return new ZipFile(ZipDir.this, entry);
                            }
                        }

                        return endOfData();
                    }
                };
            }
        };
    }

    @Override
    public void close() {
        try { jarFile.close(); } catch (IOException e) {
            if (Reflections.log != null) {
                Reflections.log.warn("Could not close JarFile", e);
            }
        }
    }

    @Override
    public String toString() {
        return jarFile.getName();
    }
}
