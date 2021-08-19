package de.jpx3.intave.module;

import de.jpx3.intave.cleanup.Shutdown;
import de.jpx3.intave.module.linker.bukkit.BukkitEventSubscriptionLinker;
import de.jpx3.intave.module.linker.packet.PacketSubscriptionLinker;

public final class Modules {
  private final ModulePool pool = new ModulePool();
  private final ModuleLoader loader = new ModuleLoader();

  public void prepareModules() {
    loader.setup();
  }

  public void proceedBoot(BootSegment bootSegment) {
    loader.loadRequests().forEach(pool::loadModule);
    pool.bootRequests(bootSegment).forEach(pool::enableModule);

    Shutdown.addTask(this::shutdown);
  }

  public void shutdown() {
    pool.disableAll();
    pool.unloadAll();
  }

  public <T extends Module> T find(Class<T> moduleClass) {
    T module = pool.lookup(moduleClass);
    if (module == null) {
      throw new IllegalStateException("Unable to find module " + moduleClass + ", is it loaded?");
    }
    return module;
  }

  // quick accessors

  private final LinkerCategory LINKER_CATEGORY = new LinkerCategory();
  private final DispatchCategory DISPATCH_CATEGORY = new DispatchCategory();

  public LinkerCategory linkers() {
    return LINKER_CATEGORY;
  }

  public DispatchCategory dispatch() {
    return DISPATCH_CATEGORY;
  }

  public class DispatchCategory {
    // empty
  }

  public class LinkerCategory {
    public BukkitEventSubscriptionLinker bukkitLinker() {
      return find(BukkitEventSubscriptionLinker.class);
    }

    public PacketSubscriptionLinker packetLinker() {
      return find(PacketSubscriptionLinker.class);
    }
  }
}
