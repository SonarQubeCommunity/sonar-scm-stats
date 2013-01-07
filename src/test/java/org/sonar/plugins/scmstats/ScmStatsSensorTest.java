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

import org.junit.*;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ScmStatsSensorTest {

  private ScmStatsSensor sensor;
  private final Project myProject = new Project("myProject");
  private final Settings settings = new Settings();

  @Before
  public void setUp() {
    myProject.setLatestAnalysis(true);
    settings.setProperty(ScmStatsPlugin.ENABLED, true);
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
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
    settings.setProperty(ScmStatsPlugin.ENABLED, false);
    assertThat(sensor.shouldExecuteOnProject(myProject), is(false));
  }
}
