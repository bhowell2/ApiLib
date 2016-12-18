package io.bhowell2.ApiLib;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Blake Howell
 */
public class ApiVersionTest {

  @Test
  public void testCompareVersions() {
    assertTrue("Should return LESS THAN 0, because V0 compared to V1 is smaller.", AppApiVersion.V0.compareVersions(AppApiVersion.V1) < 0);
    assertTrue("Should return EQUAL TO 0, because V0 is equal to V0...", AppApiVersion.V0.compareVersions(AppApiVersion.V0) == 0);
    assertTrue("Should return GREATER THAN 0, because V1 > V0.", AppApiVersion.V1.compareVersions(AppApiVersion.V0) > 0);
  }

  @Test
  public void testGetVersionString() {
    assertTrue("Should return the exact string used to create it.", AppApiVersion.V0.getVersionString().equals("V0"));
  }

}
