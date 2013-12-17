/*
 * Sonar SCM Stats Plugin
 * Copyright (C) 2012 Patroklos PAPAPETROU
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.scmstats;

import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.sonar.api.resources.Resource;
import org.sonar.api.scan.filesystem.FileExclusions;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import org.sonar.plugins.scmstats.utils.DateRange;
import static org.fest.assertions.Assertions.*;
import org.fest.assertions.MapAssert;

public class AbstractScmAdapterTest {

  private final FileExclusions fileExclusions = mock(FileExclusions.class);
  private final ScmConfiguration config = mock(ScmConfiguration.class);
  private final String resourceName = "src/main/java/org/my/package/foo.java";
  private final String testResourceName = "src/test/java/org/my/package/fooTest.java";
  private AbstractScmAdapter abstractAdapter;

  @Before
  public void init() {
    when(fileExclusions.sourceExclusions()).thenReturn(new String[0]);
    when(fileExclusions.sourceInclusions()).thenReturn(new String[0]);
    when(config.getSourceDir()).thenReturn("src/main/java");
    when(config.getTestSourceDir()).thenReturn("src/test/java");

    abstractAdapter = new AbstractScmAdapterImpl();

  }

  @Test
  public void should_Create_Resource() {
    Resource result = abstractAdapter.createResource(resourceName);

    assertThat(result.getKey()).isEqualTo("org/my/package/foo.java");
    assertThat(result.getName()).isEqualTo("foo.java");

  }

  @Test
  public void should_included_when_no_inclusions_exclusions() {
    assertThatResourceIsIncluded();
  }

  @Test
  public void should_included_when_exclusions() {
    when(fileExclusions.sourceExclusions()).thenReturn(new String[]{"org/other/package/*.java"});
    assertThatResourceIsIncluded();
  }

  @Test
  public void should_not_included_when_exclusions() {
    when(fileExclusions.sourceExclusions()).thenReturn(new String[]{"org/my/package/**/*.java"});
    assertThatResourceIsNotIncluded();
  }

  @Test
  public void should_not_included_when_inclusions() {
    when(fileExclusions.sourceInclusions()).thenReturn(new String[]{"org/other/package/**/*.java"});
    assertThatResourceIsNotIncluded();
  }

  @Test
  public void should_included_when_inclusions() {
    when(fileExclusions.sourceInclusions()).thenReturn(new String[]{"org/my/package/**/*.java"});
    assertThatResourceIsIncluded();
  }

  @Test
  public void should_not_included_test_resource_when_exclusions() {
    when(fileExclusions.sourceExclusions()).thenReturn(new String[]{"org/my/package/**/*.java"});
    assertThatTestResourceIsNotIncluded();
  }

  @Test
  public void should_not_update_activity() {
    when(fileExclusions.sourceExclusions()).thenReturn(new String[]{"org/my/package/**/*.java"});
    abstractAdapter = new AbstractScmAdapterImpl();
    Map<String, Integer> activities = Maps.newHashMap();
    Map<String, Integer> updatedActivities = abstractAdapter.updateActivity(resourceName, activities, "");
    assertThat(activities).isEqualTo(updatedActivities);
  }

  @Test
  public void should_update_activity() {
    abstractAdapter = new AbstractScmAdapterImpl();
    Map<String, Integer> activities = Maps.newHashMap();
    Map<String, Integer> updatedActivities = abstractAdapter.updateActivity(resourceName, activities, "KEY");
    assertThat(updatedActivities).hasSize(1).includes(MapAssert.entry("KEY",1));
  }

  class AbstractScmAdapterImpl extends AbstractScmAdapter {

    public AbstractScmAdapterImpl() {
      super(config, fileExclusions, null);
    }

    @Override
    public ChangeLogHandler getChangeLog(DateRange dateRange) {
      return null;
    }

    @Override
    public boolean isResponsible(String scmType) {
      return false;
    }
  }

  private void assertThatResourceIsIncluded() {
    boolean isIncluded = createResourceAndGetIncludedResponse(resourceName);
    assertThat(isIncluded).isTrue();
  }

  private void assertThatResourceIsNotIncluded() {
    boolean isIncluded = createResourceAndGetIncludedResponse(resourceName);
    assertThat(isIncluded).isFalse();
  }

  private void assertThatTestResourceIsNotIncluded() {
    boolean isIncluded = createResourceAndGetIncludedResponse(testResourceName);
    assertThat(isIncluded).isFalse();
  }

  private boolean createResourceAndGetIncludedResponse(String resName) {
    abstractAdapter = new AbstractScmAdapterImpl();
    Resource result = abstractAdapter.createResource(resName);
    return abstractAdapter.isIncluded(result);
  }
}
