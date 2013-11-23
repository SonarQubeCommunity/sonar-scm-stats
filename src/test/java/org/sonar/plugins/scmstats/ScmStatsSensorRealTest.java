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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.junit.*;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import static org.mockito.Mockito.*;
import org.sonar.api.CoreProperties;

import org.sonar.api.batch.SensorContext;
import org.sonar.api.test.SimpleProjectFileSystem;
import org.tmatesoft.hg.core.HgChangeset;
import org.tmatesoft.hg.core.HgException;
import org.tmatesoft.hg.core.HgLogCommand;
import org.tmatesoft.hg.core.HgRepoFacade;
import org.tmatesoft.hg.core.HgRepositoryNotFoundException;

public class ScmStatsSensorRealTest {

  private ScmStatsSensor sensor;
  Project myProject = mock(Project.class);
  private final Settings settings = new Settings();
  private UrlChecker checker;
  private SensorContext context = mock(SensorContext.class);
  private ScmUrlGuess scmUrlGuess = mock(ScmUrlGuess.class);
  private final ScmConfiguration scmConfiguration = new ScmConfiguration(settings, scmUrlGuess);
  private final SonarScmManager scmManager = new SonarScmManager();
  private final ScmFacade scmFacade = new ScmFacade(scmManager, scmConfiguration);
  private final ScmAdapterFactory adapterFactory = new ScmAdapterFactory(scmConfiguration, scmFacade);

  @Before
  public void setUp() {
    settings.setProperty(ScmStatsConstants.ENABLED, true);
    checker = mock(UrlChecker.class);
    when(checker.check(null)).thenReturn(Boolean.FALSE);
    sensor = new ScmStatsSensor(scmConfiguration, checker, adapterFactory);
  }


  @Test
  public void realHgTest() {
   
    settings.setProperty(ScmStatsConstants.URL, "scm:hg:https://hg.keepitcloud.com/weather.marine.travel");
    settings.setProperty(CoreProperties.PROJECT_DATE_PROPERTY, "2013-12-31");
    Project realProject = new Project("weather.marine.travel");
    realProject.setFileSystem(new SimpleProjectFileSystem(new File("C:\\dev\\kic\\weather.marine.travel")));
    sensor.analyse(realProject, context);
  }

  @Test
  @Ignore
  public void realMavenTest() {
    //settings.setProperty(ScmStatsConstants.URL, "scm:hg:https://hg.keepitcloud.com/MarineUiTesting");
    MavenProject mavenProject = new MavenProject();
    Scm scm = new Scm();
    scm.setConnection("scm:svn:http://svn.codehaus.org/sonar-plugins/trunk/widget-lab");
    scm.setDeveloperConnection("scm:svn:https://svn.codehaus.org/sonar-plugins/trunk/widget-lab");
    scm.setUrl("http://svn.codehaus.org/sonar-plugins/trunk/widget-lab");

    mavenProject.setScm(scm);
    MavenScmConfiguration mavenScmConfiguration = new MavenScmConfiguration(mavenProject);
    Project realProject = new Project("MyProject");
    realProject.setFileSystem(new SimpleProjectFileSystem(new File("C:\\dev\\sonar\\plugins\\widget-lab")));

    sensor.analyse(realProject, context);
  }

  @Test
  @Ignore
  public void realGitMavenTest() {
    settings.setProperty("sonar.inclusions", "org/sonar/plugins/scmstats/scm/*.java");
    MavenProject mavenProject = new MavenProject();
    Scm scm = new Scm();
    scm.setConnection("scm:git:git@github.com:SonarCommunity/sonar-scm-stats");
    scm.setDeveloperConnection("scm:git:git@github.com:SonarCommunity/sonar-scm-stats");
    scm.setUrl("https://github.com/SonarCommunity/sonar-scm-stats");

    mavenProject.setScm(scm);
    MavenScmConfiguration mavenScmConfiguration = new MavenScmConfiguration(mavenProject);
    Project realProject = new Project("MyProject");
    realProject.setFileSystem(new SimpleProjectFileSystem(new File("C:\\dev\\sonar\\plugins\\sonar-scm-stats")));

    sensor.analyse(realProject, context);
  }
}
