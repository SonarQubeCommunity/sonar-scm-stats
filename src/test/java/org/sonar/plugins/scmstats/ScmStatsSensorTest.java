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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmFileStatus;
import org.junit.*;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import static org.mockito.Mockito.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;

public class ScmStatsSensorTest {

  private ScmStatsSensor sensor;
  private final Project myProject = new Project("myProject");
  private final Settings settings = new Settings();
  private UrlChecker checker;

  @Before
  public void setUp() {
   settings.setProperty(ScmStatsConstants.ENABLED, true);
    checker = mock(UrlChecker.class);
    
    when(checker.check(null)).thenReturn(Boolean.FALSE);
    
    ScmConfiguration scmConfiguration = new ScmConfiguration(settings);
    sensor = new ScmStatsSensor(scmConfiguration, checker, new ScmFacade(null, scmConfiguration));
  }

  @Test
  public void testShouldExecuteOnProject_WhenUrlIsValid_andLastAnalysis() {
    when(checker.check(null)).thenReturn(Boolean.TRUE);
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
  public void shouldCreateActiviyMap(){
    
    List<ChangeFile> files = createChangeLogFiles();
    Map<String,Integer> activityMap = sensor.createActivityMap(new ChangeSet(null, null, null, files));
    
    assertThat(activityMap.containsKey(ScmStatsConstants.ACTIVITY_ADD),is(true));
    assertThat(activityMap.containsKey(ScmStatsConstants.ACTIVITY_MODIFY),is(true));
    assertThat(activityMap.containsKey(ScmStatsConstants.ACTIVITY_DELETE),is(true));
    assertThat(activityMap.get(ScmStatsConstants.ACTIVITY_ADD),is(1));
    assertThat(activityMap.get(ScmStatsConstants.ACTIVITY_MODIFY),is(2));
    assertThat(activityMap.get(ScmStatsConstants.ACTIVITY_DELETE),is(1));
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
    List<ChangeFile> files = Arrays.asList(changeFile1,changeFile2,changeFile3,changeFile4,changeFile5);
    return files;
  }
  
  @Test
  public void shouldNotAddChangeLogToHolderIfAuthorIsNull(){
    ChangeLogHandler holder = new ChangeLogHandler();
    List<ChangeFile> files = createChangeLogFiles();
    
    ChangeSet changeSet = new ChangeSet(null, null, null, files);
    
    holder = sensor.addChangeLogToHolder(changeSet, holder);
    assertThat(holder.getChangeLogs().isEmpty(),is(true));
    
  }
  @Test
  public void shouldNotAddChangeLogToHolderIfDateIsNull(){
    ChangeLogHandler holder = new ChangeLogHandler();
    List<ChangeFile> files = createChangeLogFiles();
    
    ChangeSet changeSet = new ChangeSet(null, null, "author", files);
    
    holder = sensor.addChangeLogToHolder(changeSet, holder);
    assertThat(holder.getChangeLogs().isEmpty(),is(true));
    
  }

  @Test
  public void shouldAddChangeLogToHolder(){
    ChangeLogHandler holder = new ChangeLogHandler();
    List<ChangeFile> files = createChangeLogFiles();
    
    Date someDate = Calendar.getInstance().getTime();
    
    ChangeSet changeSet = new ChangeSet(someDate, null, "author", files);
    
    holder = sensor.addChangeLogToHolder(changeSet, holder);
    assertThat(holder.getChangeLogs().isEmpty(),is(false));
    assertThat(holder.getChangeLogs().size(),is(1));
    assertThat(holder.getChangeLogs().get(0).getAuthor(),is("author"));
    assertThat(holder.getChangeLogs().get(0).getCommitDate(),is(someDate));
    assertThat(holder.getChangeLogs().get(0).getActivity().size(),is(3));
    
  }
}
