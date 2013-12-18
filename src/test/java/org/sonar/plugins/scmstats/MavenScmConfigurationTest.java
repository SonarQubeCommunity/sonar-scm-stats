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

import java.io.File;
import org.apache.maven.model.Build;
import org.junit.*;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.fest.assertions.Assertions.assertThat;

public class MavenScmConfigurationTest {

  private final MavenProject mvnProject = new MavenProject();
  private final MavenProject mvnProjectNullScm = new MavenProject();
  private MavenScmConfiguration mvnConf;
  private final MavenScmConfiguration mvnConfNullScm = new MavenScmConfiguration(mvnProjectNullScm);

  
  @Before
  public void setUp() {
    final Scm scm = new Scm();
    scm.setConnection("url");
    scm.setDeveloperConnection("devurl");
    mvnProject.setScm(scm);
    mvnConf = new MavenScmConfiguration(mvnProject);
  }
  
  @Test
  public void GetDeveloperUrl_should_return_the_Correct_url() {
    assertEquals("devurl", mvnConf.getDeveloperUrl());
  }

  @Test
  public void GetUrl_should_return_the_Correct_url() {
    assertEquals("url", mvnConf.getUrl());
  }

  @Test
  public void GetDeveloperUrl_should_return_null_if_scm_is_null() {
    assertNull(mvnConfNullScm.getDeveloperUrl());
  }

  @Test
  public void GetUrl_should_return_null_if_scm_is_null() {
    assertNull(mvnConfNullScm.getUrl());
  }

  @Test
  public void should_get_sourceDir() {
    Build build = mock(Build.class);
    when(build.getSourceDirectory()).thenReturn("/dev/sonar/plugins/sonar-scm-stats/src/main/java");
    MavenProject mavenProject = mock(MavenProject.class);
    when(mavenProject.getBuild()).thenReturn(build);
    File baseDir = mock(File.class);
    when(baseDir.getAbsolutePath()).thenReturn("/dev/sonar/plugins/sonar-scm-stats");
    when(mavenProject.getBasedir()).thenReturn(baseDir);
    MavenScmConfiguration mavenConfig = new MavenScmConfiguration(mavenProject);

    String expectedDir = "src/main/java";
    String sourceDir = mavenConfig.getSourceDir();
    assertThat (sourceDir).isEqualTo(expectedDir);
  }

  @Test
  public void should_get_testDir() {
    Build build = mock(Build.class);
    when(build.getTestSourceDirectory()).thenReturn("C:\\dev\\sonar\\plugins\\sonar-scm-stats\\src\\test\\java");
    MavenProject mavenProject = mock(MavenProject.class);
    when(mavenProject.getBuild()).thenReturn(build);
    File baseDir = mock(File.class);
    when(baseDir.getAbsolutePath()).thenReturn("C:\\dev\\sonar\\plugins\\sonar-scm-stats");
    when(mavenProject.getBasedir()).thenReturn(baseDir);
    MavenScmConfiguration mavenConfig = new MavenScmConfiguration(mavenProject);

    String expectedDir = "src/test/java";
    String sourceDir = mavenConfig.getTestDir();
    assertThat (expectedDir).isEqualTo(sourceDir);
  }

}
