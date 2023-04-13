package de.jpx3.intave.connect;

import com.google.common.collect.Lists;
import de.jpx3.intave.annotate.Native;
import de.jpx3.intave.resource.Resource;
import de.jpx3.intave.resource.Resources;

import java.util.List;
import java.util.concurrent.TimeUnit;

public final class IntaveDomains {
  private static final Resource BASE_DOMAIN_RESOURCE = Resources.cacheResourceChain("https://raw.githubusercontent.com/intave/domains/main/base", "bdomains", TimeUnit.DAYS.toMillis(1));
  private static final Resource SERVICE_DOMAIN_RESOURCE = Resources.cacheResourceChain("https://raw.githubusercontent.com/intave/domains/main/service", "sdomains", TimeUnit.DAYS.toMillis(1));

  private static final List<String> baseDomains = Lists.newArrayList();
  private static final List<String> serviceDomains = Lists.newArrayList();

  public static void setup() {
    baseDomains.addAll(BASE_DOMAIN_RESOURCE.readLines());
    serviceDomains.addAll(SERVICE_DOMAIN_RESOURCE.readLines());
    baseDomains.removeIf(String::isEmpty);
    serviceDomains.removeIf(String::isEmpty);
  }

  public static String primaryBaseDomain() {
    return baseDomains.isEmpty() ? "intave.de" : baseDomains.get(0);
  }

  public static List<String> baseDomains() {
    return baseDomains;
  }

  @Native
  public static String primaryServiceDomain() {
    return serviceDomains.isEmpty() ? "service.intave.de" : serviceDomains.get(0);
  }

  public static List<String> serviceDomains() {
    return serviceDomains;
  }

  private static void clearCaches() {
    BASE_DOMAIN_RESOURCE.delete();
    SERVICE_DOMAIN_RESOURCE.delete();
  }
}
