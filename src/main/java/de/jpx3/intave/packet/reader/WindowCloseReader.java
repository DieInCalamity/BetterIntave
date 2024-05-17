package de.jpx3.intave.packet.reader;

public final class WindowCloseReader extends AbstractPacketReader {
  public int container() {
    return packet().getIntegers().read(0);
  }
}
