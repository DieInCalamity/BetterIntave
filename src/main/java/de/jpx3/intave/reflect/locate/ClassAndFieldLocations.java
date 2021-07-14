package de.jpx3.intave.reflect.locate;

public final class ClassAndFieldLocations {
  private final ClassLocations classLocations;
  private final FieldLocations fieldLocations;

  public ClassAndFieldLocations(ClassLocations classLocations, FieldLocations fieldLocations) {
    this.classLocations = classLocations;
    this.fieldLocations = fieldLocations;
  }

  public ClassAndFieldLocations reduced() {
    return new ClassAndFieldLocations(
      classLocations.reduceToCurrentVersion(),
      fieldLocations.reduceToCurrentVersion()
    );
  }

  public ClassLocations classLocations() {
    return classLocations;
  }

  public FieldLocations fieldLocations() {
    return fieldLocations;
  }
}
