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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import static org.fest.assertions.Assertions.assertThat;
import org.junit.*;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import static org.mockito.Mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;

public class ScmStatsSensorTest {

  private ScmStatsSensor sensor;
  Project myProject = mock(Project.class);
  private ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);
  private ScmFacade scmFacade = mock(ScmFacade.class);

  private final Settings settings = new Settings();
  private UrlChecker checker;
  private SensorContext context = mock(SensorContext.class);
  
  @Before
  public void setUp() {
    settings.setProperty(ScmStatsConstants.ENABLED, true);
    settings.setProperty(ScmStatsConstants.URL,"scm:svn:http://svn.codehaus.org/sonar-plugins/trunk/useless-code-tracker");
    checker = mock(UrlChecker.class);

    when(checker.check(null)).thenReturn(Boolean.FALSE);

    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    sensor = new ScmStatsSensor(scmConfiguration, checker, new ScmFacade(new SonarScmManager(), scmConfiguration));
  }

  @Test
  public void testShouldExecuteOnProject_WhenUrlIsValid_andLastAnalysis() {
    when(checker.check("scm:svn:http://svn.codehaus.org/sonar-plugins/trunk/useless-code-tracker")).thenReturn(Boolean.TRUE);
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
  public void shouldCreateActiviyMap() {

    List<ChangeFile> files = createChangeLogFiles();
    Map<String, Integer> activityMap = sensor.createActivityMap(new ChangeSet(null, null, null, files));

    assertThat(activityMap.containsKey(ScmStatsConstants.ACTIVITY_ADD), is(true));
    assertThat(activityMap.containsKey(ScmStatsConstants.ACTIVITY_MODIFY), is(true));
    assertThat(activityMap.containsKey(ScmStatsConstants.ACTIVITY_DELETE), is(true));
    assertThat(activityMap.get(ScmStatsConstants.ACTIVITY_ADD), is(1));
    assertThat(activityMap.get(ScmStatsConstants.ACTIVITY_MODIFY), is(2));
    assertThat(activityMap.get(ScmStatsConstants.ACTIVITY_DELETE), is(1));
  }

  private List<ChangeFile> createChangeLogFiles() {
    ChangeFile changeFile1 = new ChangeFile("FileName1");
    changeFile1.setAction(ScmFileStatus.ADDED);
    ChangeFile changeFile2 = new ChangeFile("FileName2");
    changeFile2.setAction(ScmFileStatus.MODIFIED);
    ChangeFile changeFile3 = new ChangeFile("FileName3");
    changeFile3.setAction(ScmFileStatus.MODIFIED);
    ChangeFile changeFile4 = new ChangeFile("FileName4");
    changeFile4.setAction(ScmFileStatus.DELETED);
    ChangeFile changeFile5 = new ChangeFile("FileName5");
    changeFile5.setAction(ScmFileStatus.CONFLICT);
    List<ChangeFile> files = Arrays.asList(changeFile1, changeFile2, changeFile3, changeFile4, changeFile5);
    return files;
  }

  @Test
  public void shouldNotAddChangeLogToHolderIfAuthorIsNull() {
    ChangeLogHandler holder = new ChangeLogHandler();
    List<ChangeFile> files = createChangeLogFiles();

    ChangeSet changeSet = new ChangeSet(null, null, null, files);

    holder = sensor.addChangeLogToHolder(changeSet, holder);
    assertThat(holder.getChangeLogs().isEmpty(), is(true));

  }

  @Test
  public void shouldNotAddChangeLogToHolderIfDateIsNull() {
    ChangeLogHandler holder = new ChangeLogHandler();
    List<ChangeFile> files = createChangeLogFiles();

    ChangeSet changeSet = new ChangeSet(null, null, "author", files);

    holder = sensor.addChangeLogToHolder(changeSet, holder);
    assertThat(holder.getChangeLogs().isEmpty(), is(true));

  }

  @Test
  public void shouldAddChangeLogToHolder() {
    ChangeLogHandler holder = new ChangeLogHandler();
    List<ChangeFile> files = createChangeLogFiles();

    Date someDate = Calendar.getInstance().getTime();

    ChangeSet changeSet = new ChangeSet(someDate, null, "author", files);

    holder = sensor.addChangeLogToHolder(changeSet, holder);
    assertThat(holder.getChangeLogs().isEmpty(), is(false));
    assertThat(holder.getChangeLogs().size(), is(1));
    assertThat(holder.getChangeLogs().get(0).getAuthor(), is("author"));
    assertThat(holder.getChangeLogs().get(0).getCommitDate(), is(someDate));
    assertThat(holder.getChangeLogs().get(0).getActivity().size(), is(3));

  }

  @Test
  public void shouldGatherStatsForDefaultPeriod() throws ScmException {
    when(myProject.getFileSystem()).thenReturn(projectFileSystem);
    when(projectFileSystem.getBasedir()).thenReturn(new File(""));
    when(checker.check("scm:url")).thenReturn(true);
    ChangeLogScmResult scmResult = new ChangeLogScmResult("", null);
    when(scmFacade.getChangeLog(projectFileSystem.getBasedir(), 0)).thenReturn(scmResult);
    
    settings.setProperty(ScmStatsConstants.ENABLED, true);
    settings.setProperty(ScmStatsConstants.URL,"scm:url");
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    ScmStatsSensor newSensor = new ScmStatsSensor(scmConfiguration, checker, scmFacade);
    ScmStatsSensor spiedSensor = spy(newSensor);
    doNothing().when(spiedSensor).generateAndSaveMeasures((ChangeLogScmResult)any(),(SensorContext) any(), (String) any());

    spiedSensor.analyse(myProject, context);
    verify(spiedSensor, times(3)).analyseChangeLog((Project)any(),(SensorContext) any(), (String) any());
    verify(spiedSensor, times(1)).generateAndSaveMeasures((ChangeLogScmResult)any(),(SensorContext) any(), (String) any());

  }

  @Test
  public void shouldGatherStatsForAllPeriods() throws ScmException {
    when(myProject.getFileSystem()).thenReturn(projectFileSystem);
    when(projectFileSystem.getBasedir()).thenReturn(new File(""));
    when(checker.check("scm:url")).thenReturn(true);
    ChangeLogScmResult scmResult = new ChangeLogScmResult("", null);
    when(scmFacade.getChangeLog(projectFileSystem.getBasedir(), 0)).thenReturn(scmResult);
    when(scmFacade.getChangeLog(projectFileSystem.getBasedir(), 10)).thenReturn(scmResult);
    when(scmFacade.getChangeLog(projectFileSystem.getBasedir(), 20)).thenReturn(scmResult);
    
    settings.setProperty(ScmStatsConstants.ENABLED, true);
    settings.setProperty(ScmStatsConstants.PERIOD_2, 10);
    settings.setProperty(ScmStatsConstants.PERIOD_3, 20);
    settings.setProperty(ScmStatsConstants.URL,"scm:url");
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    ScmStatsSensor newSensor = new ScmStatsSensor(scmConfiguration, checker, scmFacade);
    ScmStatsSensor spiedSensor = spy(newSensor);
    doNothing().when(spiedSensor).generateAndSaveMeasures((ChangeLogScmResult)any(),(SensorContext) any(), (String) any());

    spiedSensor.analyse(myProject, context);
    verify(spiedSensor, times(3)).analyseChangeLog((Project)any(),(SensorContext) any(), (String) any());
    verify(spiedSensor, times(3)).generateAndSaveMeasures((ChangeLogScmResult)any(),(SensorContext) any(), (String) any());

  }

  @Test
  public void shouldFailWhenGatheringStats() throws ScmException {
    when(myProject.getFileSystem()).thenReturn(projectFileSystem);
    when(projectFileSystem.getBasedir()).thenReturn(new File(""));
    when(checker.check("scm:url")).thenReturn(true);
    ChangeLogScmResult scmResult = new ChangeLogScmResult(null, new ScmResult(null, null, null, false));
    when(scmFacade.getChangeLog(projectFileSystem.getBasedir(), 0)).thenReturn(scmResult);
    
    settings.setProperty(ScmStatsConstants.ENABLED, true);
    settings.setProperty(ScmStatsConstants.URL,"scm:url");
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    ScmStatsSensor newSensor = new ScmStatsSensor(scmConfiguration, checker, scmFacade);
    ScmStatsSensor spiedSensor = spy(newSensor);
    doNothing().when(spiedSensor).generateAndSaveMeasures((ChangeLogScmResult)any(),(SensorContext) any(), (String) any());

    spiedSensor.analyse(myProject, context);
    verify(spiedSensor, times(3)).analyseChangeLog((Project)any(),(SensorContext) any(), (String) any());
    verify(spiedSensor, times(0)).generateAndSaveMeasures((ChangeLogScmResult)any(),(SensorContext) any(), (String) any());

  }
  
  
  @Test
  public void shouldGatherStatsForPerforceScm() throws ScmException {
    when(myProject.getFileSystem()).thenReturn(projectFileSystem);
    when(projectFileSystem.getBasedir()).thenReturn(new File(""));
    when(checker.check("scm:url")).thenReturn(true);
    ChangeLogScmResult scmResult = new ChangeLogScmResult("", null);
    when(scmFacade.getChangeLog(projectFileSystem.getBasedir(), 0)).thenReturn(scmResult);

    settings.setProperty(ScmStatsConstants.ENABLED, true);
    settings.setProperty(ScmStatsConstants.URL,"scm:url");
    settings.setProperty(ScmStatsConstants.PERFORCE_CLIENTSPEC,"myClient");
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    ScmStatsSensor newSensor = new ScmStatsSensor(scmConfiguration, checker, scmFacade);
    ScmStatsSensor spiedSensor = spy(newSensor);
    doNothing().when(spiedSensor).generateAndSaveMeasures((ChangeLogScmResult)any(),(SensorContext) any(), (String) any());
    spiedSensor.analyse(myProject, context);
    
    assertThat(System.getProperty("maven.scm.perforce.clientspec.name")).isEqualTo("myClient");

  }


  
  @Test
  public void shouldHaveDebugName() {
    String debugName = sensor.toString();

    assertThat(debugName).isEqualTo("ScmStatsSensor");
  }
}
