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

import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Date;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.changelog.ChangeLogScmRequest;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.repository.ScmRepository;
import static org.fest.assertions.Assertions.assertThat;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;

public class ScmFacadeTest {

  private final Settings settings = new Settings();
  private final SonarScmManager scmManager = mock(SonarScmManager.class);
  private final ScmUrlGuess scmUrlGuess = mock(ScmUrlGuess.class);
  private final ChangeLogScmResult changeLogScmResult = mock(ChangeLogScmResult.class);
  private final ScmConfiguration configuration = new ScmConfiguration(settings, scmUrlGuess);
  private ScmFacade scmFacade = new ScmFacade(scmManager, configuration);

  @Before
  public void setUp() {
    settings.setProperty(ScmStatsConstants.ENABLED, true);
    when(scmUrlGuess.guess()).thenReturn("scm:svn:http://");
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
    Date fromDate = new DateTime().toDate();
    Date toDate = new DateTime().toDate();
    
    when(scmManager.changeLog((ChangeLogScmRequest) any())).thenReturn(changeLogScmResult);
    assertThat(scmFacade.getChangeLog(new File("/"), fromDate, toDate)).isEqualTo(changeLogScmResult);
  }


  @Test
  public void shouldDetermineDefaultDatePattern() {
    initScmRepository("scm:perforce:perforce:1666://path");
    assertThat(scmFacade.determineChangeLogDatePattern()).isNullOrEmpty();
  }

  @Test
  public void shouldGetUserDatePattern() {
    String datePattern = "dd/MM/yyyy";
    settings.setProperty(ScmStatsConstants.CHANGELOG_DATE_PATTERN, datePattern);

    initScmRepository("scm:hg:https://mercurial.scm/hgproject");
    assertThat(datePattern).isEqualTo(scmFacade.determineChangeLogDatePattern());
  }

  private void initScmRepository(String url) {
    settings.setProperty(ScmStatsConstants.URL, url);
    when(scmUrlGuess.guess()).thenReturn(url);
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings,scmUrlGuess);
    scmFacade = new ScmFacade(new SonarScmManager(), scmConfiguration);
  }
}