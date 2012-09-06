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

import org.apache.commons.configuration.Configuration;
import org.junit.*;
import static org.junit.Assert.*;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Project;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ScmStatsSensorTest {

  private ScmStatsSensor sensor;
  private final Project myProject = new Project("myProject");
  private static final String URL = "scm:svn:http://";

  @Before
  public void setUp() {
    myProject.setConfiguration(mock(Configuration.class));
    myProject.setLatestAnalysis(true);
    when(myProject.getConfiguration().getString(ScmStatsPlugin.URL)).thenReturn(URL);
    when(myProject.getConfiguration().getBoolean(ScmStatsPlugin.ENABLED, ScmStatsPlugin.ENABLED_DEFAULT)).thenReturn(true);
    ScmConfiguration scmConfiguration = new ScmConfiguration(myProject.getConfiguration());
    sensor = new ScmStatsSensor(scmConfiguration, new UrlChecker(), new ScmFacade(null, scmConfiguration));
  }

  @Test
  public void testShouldExecuteOnProject_WhenLastAnalysis() {
    assertThat(sensor.shouldExecuteOnProject(myProject), is(true));
  }

  @Test
  public void testShouldNotExecuteOnProject_WhenNotLastAnalysis() {
    myProject.setLatestAnalysis(false);
    assertThat(sensor.shouldExecuteOnProject(myProject), is(false));
  }

  @Test
  public void testShouldNotExecuteOnProject_WhenPluginIsNotEnabled() {
    when(myProject.getConfiguration().getBoolean(ScmStatsPlugin.ENABLED, ScmStatsPlugin.ENABLED_DEFAULT)).thenReturn(false);
    assertThat(sensor.shouldExecuteOnProject(myProject), is(false));
  }
}
