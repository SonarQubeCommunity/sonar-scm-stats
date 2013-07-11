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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.File;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.changelog.ChangeLogScmRequest;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.repository.ScmRepository;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;

public class ScmFacadeTest {

  private final Settings settings = new Settings();
  private ScmFacade scmFacade;

  @Before
  public void setUp() {
    settings.setProperty(ScmStatsConstants.ENABLED, true);
  }

  @Test
  public void shouldGetGitScmRepository() {
    initScmRepository("scm:git:git@github.com:SonarCommunity/sonar-scm-stats");
    ScmRepository result = scmFacade.getScmRepository();
    assertThat(result).isInstanceOf(ScmRepository.class);
  }

  @Test
  public void shouldGetSvnScmRepository() {
    initScmRepository("scm:svn:http://svn.codehaus.org/sonar-plugins/trunk/useless-code-tracker");
    ScmRepository result = scmFacade.getScmRepository();
    assertThat(result).isInstanceOf(ScmRepository.class);
  }

  @Test
  public void shouldGetPerforceScmRepository() {
    initScmRepository("scm:perforce:perforce:1666://path");
    ScmRepository result = scmFacade.getScmRepository();
    assertThat(result).isInstanceOf(ScmRepository.class);
  }

  @Test
  public void shouldGetMercurialScmRepository() {
    initScmRepository("scm:hg:http://host/v3");
    ScmRepository result = scmFacade.getScmRepository();
    assertThat(result).isInstanceOf(ScmRepository.class);
  }

  @Test
  public void shouldGetCvsScmRepository() {
    initScmRepository("scm:cvs:pserver:anoncvs:@cvs.apache.org:/cvs/root:module");
    assertThat(scmFacade.getScmRepository()).isInstanceOf(ScmRepository.class);
  }
  @Test
  public void shouldGetJazzScmRepository() {
    initScmRepository("scm:jazz:username;password@https://server.name:9443/jazz:workspace");
    assertThat(scmFacade.getScmRepository()).isInstanceOf(ScmRepository.class);
  }

  @Test
  public void shouldGetChangeLog() throws ScmException {
    ScmConfiguration scmConfiguration = mock(ScmConfiguration.class);
    ChangeLogScmResult changeLogScmResult = new ChangeLogScmResult("", null);
    when(scmConfiguration.getSettings()).thenReturn(settings);
    when(scmConfiguration.getScmProvider()).thenReturn("scm");
    SonarScmManager scmManager = mock(SonarScmManager.class);
    ScmRepository scmRepository = mock(ScmRepository.class);
    when(scmManager.makeScmRepository("scm:url")).thenReturn(scmRepository);
    when(scmManager.changeLog((ChangeLogScmRequest) any())).thenReturn(changeLogScmResult);
    scmFacade = new ScmFacade(scmManager, scmConfiguration);
    ScmFacade spied = spy(scmFacade);
    when(spied.getScmRepository()).thenReturn(scmRepository);
    assertThat(spied.getChangeLog(new File(""), 0)).isEqualTo(changeLogScmResult);
  }

  @Test
  public void shouldGetChangeLogOfDays() throws ScmException {
    settings.setProperty(ScmStatsConstants.URL, "scm:url");
    settings.setProperty(ScmStatsConstants.USER, "user");
    settings.setProperty(ScmStatsConstants.PASSWORD, "password");
    ScmConfiguration scmConfiguration = mock(ScmConfiguration.class);
    ChangeLogScmResult changeLogScmResult = new ChangeLogScmResult("", null);
    when(scmConfiguration.getSettings()).thenReturn(settings);
    when(scmConfiguration.getScmProvider()).thenReturn("scm");
    SonarScmManager scmManager = mock(SonarScmManager.class);
    ScmRepository scmRepository = mock(ScmRepository.class);
    when(scmManager.makeScmRepository("scm:url")).thenReturn(scmRepository);
    when(scmManager.changeLog((ChangeLogScmRequest) any())).thenReturn(changeLogScmResult);
    scmFacade = new ScmFacade(scmManager, scmConfiguration);
    ScmFacade spied = spy(scmFacade);
    when(spied.getScmRepository()).thenReturn(scmRepository);
    assertThat(spied.getChangeLog(new File(""), 0)).isEqualTo(changeLogScmResult);

  }

  private void initScmRepository(String url) {
    settings.setProperty(ScmStatsConstants.URL, url);
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    SonarScmManager scmManager = new SonarScmManager();
    scmFacade = new ScmFacade(scmManager, scmConfiguration);
  }
}