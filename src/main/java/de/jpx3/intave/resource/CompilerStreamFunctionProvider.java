package de.jpx3.intave.resource;

import de.jpx3.intave.IntavePlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public interface CompilerStreamFunctionProvider<O> extends Function<List<String>, O> {
  default O fromFile(File file) throws FileNotFoundException {
    return fromStream(new FileInputStream(file));
  }

  default O fromResource(Resource resource) {
    return fromStream(resource.read());
  }

  default O fromWithinJar(String path) {
    InputStream resource = IntavePlugin.class.getResourceAsStream(path);
    if (resource == null) {
      resource = IntavePlugin.class.getResourceAsStream(path.substring(1));
      if (resource == null) {
        throw new IllegalStateException("Unable to locate resource in jar: " + path);
      }
    }
    return fromStream(resource);
  }

  default O fromStream(InputStream inputStream) {
    return apply(lineExtraction(inputStream));
  }

  static List<String> lineExtraction(InputStream inputStream) {
    Scanner scanner = new Scanner(inputStream);
    List<String> strings = new ArrayList<>();
    while (scanner.hasNextLine()) {
      strings.add(scanner.nextLine());
    }
    return strings;
  }
}
