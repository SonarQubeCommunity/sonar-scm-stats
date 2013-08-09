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

import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.apache.maven.scm.provider.svn.svnexe.SvnExeScmProvider;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNull;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;

public class ScmConfigurationTest {
  private final Settings settings = new Settings();
  private static final String URL = "scm:svn:http://";
  private static final String MAVEN_URL = "scm:svn:http://someUrl";
  private static final String CLIENT_SPEC = "clientSpec";
  private static final String IGNORE_AUTHORS_LIST = "author1,author2";
  private static final String MERGE_AUTHORS_LIST = "mergeauthor1:author1;AUTHOR1,mergeauthor2:mergeauthor";
  @Before
  public void setUp() {
    settings.setProperty(ScmStatsConstants.ENABLED, true);
    settings.setProperty(ScmStatsConstants.PERIOD_1, 0);
    settings.setProperty(ScmStatsConstants.PERIOD_2, 30);
    settings.setProperty(ScmStatsConstants.PERIOD_3, 90);
    settings.setProperty(ScmStatsConstants.PERFORCE_CLIENTSPEC, CLIENT_SPEC);
    settings.setProperty(ScmStatsConstants.IGNORE_AUTHORS_LIST, IGNORE_AUTHORS_LIST);
    settings.setProperty(ScmStatsConstants.MERGE_AUTHORS_LIST, MERGE_AUTHORS_LIST);
  }

  @Test
  public void testMavenConfiguration() {
    
    MavenProject mvnProject = new MavenProject();
    final Scm scm = new Scm();
    scm.setConnection(URL);
    scm.setDeveloperConnection(URL);
    mvnProject.setScm(scm);
    MavenScmConfiguration mavenConfonfiguration = new MavenScmConfiguration(mvnProject);
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings, mavenConfonfiguration);
    
    assertThat ( scmConfiguration.isEnabled() , is(true));
    assertThat ( scmConfiguration.getUrl() , is(URL));
    assertThat ( scmConfiguration.getScmProvider() , is(new SvnExeScmProvider().getScmType()));
  }

  @Test
  public void testNonMavenConfiguration() {
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    
    assertThat ( scmConfiguration.isEnabled() , is(true));
    assertNull ( scmConfiguration.getUrl());
    assertNull ( scmConfiguration.getScmProvider());
  }

  @Test
  public void testConfigurationOfSCMActivityPlugin() {
    settings.setProperty("sonar.scm.url", URL);
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    
    assertThat ( scmConfiguration.isEnabled() , is(true));
    assertThat ( scmConfiguration.getUrl() , is(URL));
    assertThat ( scmConfiguration.getScmProvider() , is(new SvnExeScmProvider().getScmType()));
  }
  
  @Test
  public void testPeriodsConfiguration() {
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    
    assertThat ( scmConfiguration.getFirstPeriod() , equalTo(0));
    assertThat ( scmConfiguration.getSecondPeriod(), equalTo(30));
    assertThat ( scmConfiguration.getThirdPeriod(), equalTo(90));
  }

  @Test
  public void testPerforceConfiguration() {
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    assertThat ( scmConfiguration.getPerforceClientSpec() , equalTo(CLIENT_SPEC));
  }

  @Test
  public void testMergedAuthorsListConfiguration() {
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    List<String> mergeAuthors = scmConfiguration.getMergeAuthorsList();
    
    assertThat (mergeAuthors, hasItem("mergeauthor1:author1;AUTHOR1"));
    assertThat (mergeAuthors, hasItem("mergeauthor2:mergeauthor"));
  }
  
  @Test
  public void testIgnotedAuthorsListConfiguration() {
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    List<String> ignoredAuthors = scmConfiguration.getIgnoreAuthorsList();
    
    assertThat (ignoredAuthors, hasItem("author1"));
    assertThat (ignoredAuthors, hasItem("author2"));
  }

  @Test
  public void shouldIgnoreMavenConfiguration() {
    settings.setProperty("sonar.scm.url", URL);
    MavenProject mvnProject = new MavenProject();
    final Scm scm = new Scm();
    scm.setConnection(MAVEN_URL);
    scm.setDeveloperConnection(MAVEN_URL);
    mvnProject.setScm(scm);
    MavenScmConfiguration mavenConfonfiguration = new MavenScmConfiguration(mvnProject);
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings,mavenConfonfiguration);
    
    assertThat ( scmConfiguration.getUrl() , is(URL));
  }

}
