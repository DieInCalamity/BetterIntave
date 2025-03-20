package de.jpx3.intave.share;

import com.comphenix.protocol.utility.MinecraftMethods;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.lang.reflect.Method;

public final class FriendlyByteBuf {
  public static ByteBuf from256Unpooled() {
    return wrapping(Unpooled.buffer(256, 2048));
  }

  public static ByteBuf wrapping(ByteBuf byteBuf) {
    return (ByteBuf) MinecraftMethods.getFriendlyBufBufConstructor().apply(byteBuf);
  }

  public static String readUtf(ByteBuf friendly, int maxLength) {
    try {
      return (String) readUtfMethod.invoke(friendly, maxLength);
    } catch (Exception e) {
      e.printStackTrace();
      return "something went wrong";
    }
  }

  private final static Method readUtfMethod;

  static {
    Method method;
    try {
      method = from256Unpooled().getClass().getDeclaredMethod("readUtf", int.class);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
      method = null;
    }
    readUtfMethod = method;
    readUtfMethod.setAccessible(true);
  }
}
