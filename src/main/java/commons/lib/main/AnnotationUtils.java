package commons.lib.main;

import commons.lib.main.os.LogUtils;
import java.util.logging.Logger;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class AnnotationUtils {
    private static final Logger logger = LogUtils.initLogs();

    public static List<Class<?>> getClassesFromPackageName(String packageName) throws ClassNotFoundException, IOException {
        List<Class<?>> classes;

        if (isInIde()) {
            LogUtils.debug("IN IDE");
            classes = getClassesFromIde(packageName);
        } else {
            LogUtils.debug("IN JAR");
            classes = getClassesFromJar(packageName);
        }
        return classes;
    }

    public static boolean isInIde() {
        final String hintIsInIde = System.getProperty("hintIsInIde");
        if (hintIsInIde != null) {
            return Boolean.parseBoolean(hintIsInIde);
        }
        final String path = AnnotationUtils.class.getResource("AnnotationUtils.class").getPath();
        LogUtils.debug("Testing if the current process is being executed in an IDE with path {}", path);
        return path.startsWith("/") || path.startsWith("file:/");
    }

    private static List<Class<?>> getClassesFromIde(String packageName)
            throws ClassNotFoundException, IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        final String path = packageName.replace('.', '/');
        final Enumeration<URL> resources = classLoader.getResources(path);
        final List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        final List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private static List<Class<?>> getClassesFromJar(String packageName)
            throws ClassNotFoundException, IOException {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        final String path = packageName.replace('.', '/');
        LogUtils.debug("Searching in Resource {}", path);
        return listClassesPerJarFile(classLoader, path);
    }

    private static List<Class<?>> listClassesPerJarFile(ClassLoader classLoader, String path) throws IOException, ClassNotFoundException {
        final List<Class<?>> result = new ArrayList<>();
        final Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            final URL resource = resources.nextElement();
            int endIndex = resource.getPath().indexOf("!");
            if (endIndex == -1) {
                continue;
            }
            final String pathJarFile = resource.getPath().substring("file:".length() /*"file:\\".length()*/, endIndex);
            final JarFile jarFile = new JarFile(pathJarFile);
            final Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                final String name = jarEntry.getName();
                if (name.startsWith(path) && name.endsWith(".class")) {
                    result.add(Class.forName(name.replaceAll("/", ".").substring(0, name.length() - ".class".length())));
                }
            }
        }
        return result;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        final List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        final File[] files = directory.listFiles();
        for (File file : Optional.ofNullable(files).orElse(new File[0])) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

}
