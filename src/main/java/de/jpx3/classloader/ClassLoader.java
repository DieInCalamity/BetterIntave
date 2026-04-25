package de.jpx3.classloader;


import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class ClassLoader {
  public static final boolean USE_NATIVE_ACCESS = currentJavaVersion() >= 15;

  private static boolean loaded;

  public static void setupEnvironment(File parentTempDirectory) {
    if (USE_NATIVE_ACCESS) {
      NativeLibrary nativeLibrary = new NativeLibrary(
        "classloader", 1, parentTempDirectory,
        "https://github.com/intave/classloader/releases/download/v1.0.1/classloader.dll",
        "https://github.com/intave/classloader/releases/download/v1.0.1/libclassloader.so",
        "https://github.com/intave/classloader/releases/download/v1.0.1/libclassloader.dylib",
        Arrays.asList(
          "895786b3bcac6e270e064cca8eb9df39307e0aa576310916180f28960f890823",
          "4d6929ac47b20dbcb04e972920bd576ec1fb4449c0026c1f866c2c72bf64f991",
          "9bb42f8f9d9a526c2be70256d0d7d0e2efc4eb2550c919745f0b36257596f563"
        )
      );
      nativeLibrary.load();
    }
	  if (!classLoaded("java.lang.String")) {
      throw new IllegalStateException("Something went wrong");
	  }
    if (classLoaded("de.jpx3.intave.i.will.never.ever.exist.Hopefully")) {
      throw new IllegalStateException("Something went wrong");
    }
    loaded = true;
  }

  public static boolean loaded() {
    return loaded;
  }

  public static boolean usesNativeAccess() {
    return USE_NATIVE_ACCESS;
  }

  public static boolean classLoaded(String name) {
    if (USE_NATIVE_ACCESS) {
	    return classLoaded0(name);
    } else {
      return classLoadedLegacy(name);
    }
  }

  private static boolean classLoadedLegacy(String className) {
    try {
      Method findLoadedClass = java.lang.ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
      if (!findLoadedClass.isAccessible()) {
        findLoadedClass.setAccessible(true);
      }
      return findLoadedClass.invoke(ClassLoader.class.getClassLoader(), className) != null;
    } catch (Exception exception) {
      exception.printStackTrace();
      return true;
    }
  }

  private static native boolean classLoaded0(String name);

  public static void classLoad(byte[] bytes) {
    if (USE_NATIVE_ACCESS) {
      classLoad0(bytes);
    } else {
      classLoadLegacy(bytes);
    }
  }

  private static void classLoadLegacy(byte[] bytes) {
    try {
      Method defineClass = java.lang.ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
      if (!defineClass.isAccessible()) {
        defineClass.setAccessible(true);
      }
      defineClass.invoke(ClassLoader.class.getClassLoader(), bytes, 0, bytes.length);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private static native void classLoad0(byte[] bytes);

  private static int currentJavaVersion() {
    String version = System.getProperty("java.version");
    if (version.startsWith("1.")) {
      version = version.substring(2, 3);
    } else {
      int dot = version.indexOf(".");
      if (dot != -1) {
        version = version.substring(0, dot);
      }
    }
    return Integer.parseInt(version);
  }
}
