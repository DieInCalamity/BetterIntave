package de.jpx3.intave.connect.shadow;

import de.jpx3.intave.patchy.annotate.PatchyAutoTranslation;
import de.jpx3.intave.tools.annotate.Native;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@PatchyAutoTranslation
public final class v8PipelineInjector implements PipelineInjector {
  private final LabymodShadowIntegration integration;

  public v8PipelineInjector(LabymodShadowIntegration integration) {
    this.integration = integration;
  }

  @Override
  @Native
  @PatchyAutoTranslation
  public void inject(Player target) {
    EntityPlayer entityPlayer = ((CraftPlayer) target).getHandle();
    Channel channel = entityPlayer.playerConnection.networkManager.channel;
    ChannelPipeline pipeline = channel.pipeline();
//    IntaveLogger.logger().globalPrintLn(pipeline.get("protocol_lib_decoder"));
    pipeline.addBefore("decoder", "shadowpacketin", new v8PipelineHandler(target.getUniqueId(), integration));
  }

  @Override
  @Native
  @PatchyAutoTranslation
  public void uninject(Player target) {
    EntityPlayer entityPlayer = ((CraftPlayer) target).getHandle();
    Channel channel = entityPlayer.playerConnection.networkManager.channel;
    ChannelPipeline pipeline = channel.pipeline();
    pipeline.remove("shadowpacketin");
  }
}
