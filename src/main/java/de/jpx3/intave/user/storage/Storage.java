package de.jpx3.intave.user.storage;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public interface Storage {
  void writeTo(ByteArrayDataOutput output);
  void readFrom(ByteArrayDataInput input);
  default int id() {
    return -1;
  }
  default int version() {
    return -1;
  }

  boolean sameContentsAs(Storage other);
}
