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

import org.sonar.plugins.scmstats.utils.UrlChecker;
import java.io.File;
import static org.fest.assertions.Assertions.assertThat;
import static org.assertj.jodatime.api.Assertions.assertThat;
import org.junit.*;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import static org.mockito.Mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.joda.time.DateTime;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import org.sonar.plugins.scmstats.utils.DateRange;

public class ScmStatsSensorTest {

  private ScmStatsSensor sensor;
  Project myProject = mock(Project.class);
  private ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);
  private ScmAdapterFactory scmAdapterFactory = mock(ScmAdapterFactory.class);
  private AbstractScmAdapter adapter = mock(AbstractScmAdapter.class);
  private final Settings settings = new Settings();
  private UrlChecker checker;
  private final SensorContext context = mock(SensorContext.class);
  private final ScmUrlGuess scmUrlGuess = mock(ScmUrlGuess.class);
  private final ChangeLogHandler holder = mock(ChangeLogHandler.class);
  private final static String URL = "someUrl";

  @Before
  public void setUp() {

    settings.setProperty(ScmStatsConstants.ENABLED, true);
    when(projectFileSystem.getBasedir()).thenReturn(new File("/"));
    when(scmAdapterFactory.getScmAdapter()).thenReturn(adapter);
    checker = mock(UrlChecker.class);

    when(checker.check(null)).thenReturn(Boolean.FALSE);
    when(checker.check(URL)).thenReturn(Boolean.TRUE);

    ScmConfiguration scmConfiguration = new ScmConfiguration(settings, scmUrlGuess);
    sensor = new ScmStatsSensor(scmConfiguration, checker, scmAdapterFactory);
  }

  @Test
  public void shouldAnalyzeOnlyOnce() {
    settings.setProperty(ScmStatsConstants.URL, URL);
    settings.setProperty(ScmStatsConstants.PERIOD_1, 0);
    settings.setProperty(CoreProperties.PROJECT_DATE_PROPERTY, "2013-10-10");
    when(adapter.getChangeLog((Project) anyObject(), (DateRange) anyObject())).thenReturn(holder);

    sensor.analyse(myProject, context);
    verify(holder).generateMeasures();
    verify(holder).saveMeasures(context, ScmStatsConstants.PERIOD_1);
  }

  @Test
  public void testShouldExecuteOnProject_WhenUrlIsValid_andLastAnalysis() {
    settings.setProperty(ScmStatsConstants.URL, URL);

    assertThat(sensor.shouldExecuteOnProject(myProject), is(true));
  }

  @Test
  public void testShouldNotExecuteOnProject_WhenUrlIsNotValid() {
    assertThat(sensor.shouldExecuteOnProject(myProject), is(false));
  }

  @Test
  public void testShouldNotExecuteOnProject_WhenPluginIsNotEnabled() {
    settings.setProperty(ScmStatsConstants.ENABLED, false);
    assertThat(sensor.shouldExecuteOnProject(myProject), is(false));
  }

  @Test
  public void shouldHaveDebugName() {
    String debugName = sensor.toString();
    assertThat(debugName).isEqualTo("ScmStatsSensor");
  }

  @Test
  public void shouldGetProjectDateSetting() {
    settings.setProperty(CoreProperties.PROJECT_DATE_PROPERTY, "2013-01-31");
    DateTime projectDate = this.sensor.getProjectDateProperty();
    assertThat(projectDate).isEqualTo("2013-01-31");
  }

  @Test
  public void shouldSetPerforceClientSpecName() {
    settings.setProperty(ScmStatsConstants.URL, URL);
    settings.setProperty(ScmStatsConstants.PERIOD_1, 0);
    settings.setProperty(ScmStatsConstants.PERFORCE_CLIENTSPEC, "client");
    settings.setProperty(CoreProperties.PROJECT_DATE_PROPERTY, "2013-10-10");
    when(adapter.getChangeLog((Project) anyObject(), (DateRange) anyObject())).thenReturn(holder);

    sensor.analyse(myProject, context);
    assertThat(System.getProperty("maven.scm.perforce.clientspec.name")).isEqualTo("client");
  }

  @Test
  public void should_not_analyze_other_periods_when_days_are_zero() {
    settings.setProperty(ScmStatsConstants.URL, URL);
    settings.setProperty(ScmStatsConstants.PERIOD_1, 0);
    settings.setProperty(ScmStatsConstants.PERIOD_2, 0);
    settings.setProperty(ScmStatsConstants.PERIOD_3, 0);
    settings.setProperty(CoreProperties.PROJECT_DATE_PROPERTY, "2013-10-10");
    when(adapter.getChangeLog((Project) anyObject(), (DateRange) anyObject())).thenReturn(holder);

    sensor.analyse(myProject, context);
    verify(holder, times(1)).generateMeasures();
    verify(holder, times(1)).saveMeasures(context, ScmStatsConstants.PERIOD_1);
    verify(holder, times(0)).saveMeasures(context, ScmStatsConstants.PERIOD_2);
    verify(holder, times(0)).saveMeasures(context, ScmStatsConstants.PERIOD_3);
  }

  @Test
  public void should_analyze_other_periods_where_days_are_great_than_zero() {
    settings.setProperty(ScmStatsConstants.URL, URL);
    settings.setProperty(ScmStatsConstants.PERIOD_1, 0);
    settings.setProperty(ScmStatsConstants.PERIOD_2, 3);
    settings.setProperty(CoreProperties.PROJECT_DATE_PROPERTY, "2013-10-10");
    when(adapter.getChangeLog((Project) anyObject(), (DateRange) anyObject())).thenReturn(holder);

    sensor.analyse(myProject, context);
    verify(holder, times(2)).generateMeasures();
    verify(holder, times(1)).saveMeasures(context, ScmStatsConstants.PERIOD_1);
    verify(holder, times(1)).saveMeasures(context, ScmStatsConstants.PERIOD_2);
  }

}
