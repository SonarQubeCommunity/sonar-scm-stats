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
import static org.junit.Assert.*;

import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
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

}
