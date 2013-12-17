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

import org.sonar.plugins.scmstats.utils.DateRange;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import static org.fest.assertions.Assertions.assertThat;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import static org.mockito.Mockito.*;
import org.sonar.api.scan.filesystem.FileExclusions;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.plugins.scmstats.model.ChangeLogInfo;

public class GenericScmAdapterTest {

  private final File projectBaseDir = new File(".");
  private final Settings settings = new Settings();
  private final ScmUrlGuess scmUrlGuess = mock(ScmUrlGuess.class);
  private final FileExclusions fileExclusions = mock(FileExclusions.class);
  private final ScmConfiguration config = new ScmConfiguration(settings, scmUrlGuess);
  private final ScmFacade scmFacade = mock(ScmFacade.class);
  private final ModuleFileSystem moduleFileSystem = mock(ModuleFileSystem.class);
  private final ChangeLogScmResult changeLog = mock(ChangeLogScmResult.class);
  private final ChangeLogSet changeLogSet = mock(ChangeLogSet.class);
  private final List<ChangeSet> changeSets = new ArrayList();
  private final DateTime datefrom = new DateTime();
  private final DateTime dateTo = new DateTime();

  @Before
  public void init() throws ScmException {
    when(moduleFileSystem.baseDir()).thenReturn(projectBaseDir);
    when(changeLog.getChangeLog()).thenReturn(changeLogSet);
    when(changeLogSet.getChangeSets()).thenReturn(changeSets);
    when(scmFacade.getChangeLog(projectBaseDir, datefrom.toDate(), dateTo.toDate())).thenReturn(changeLog);
    when(fileExclusions.sourceExclusions()).thenReturn(new String[0]);
    when(fileExclusions.sourceInclusions()).thenReturn(new String[0]);
    
  }

  @Test
  public void shouldBeResponsible() {
    GenericScmAdapter genericScmAdapter = new GenericScmAdapter(scmFacade, config, fileExclusions, moduleFileSystem);
    assertThat(genericScmAdapter.isResponsible("git")).isTrue();
  }

  @Test
  public void shouldNotBeResponsible() {
    GenericScmAdapter genericScmAdapter = new GenericScmAdapter(scmFacade, config, fileExclusions, moduleFileSystem);
    assertThat(genericScmAdapter.isResponsible("hg")).isFalse();
  }

  @Test
  public void shouldGetEmptyHolderWhenIsNotSuccesful() {
    DateRange dateRange = new DateRange(datefrom, dateTo);

    when(changeLog.isSuccess()).thenReturn(Boolean.FALSE);
    GenericScmAdapter genericScmAdapter = new GenericScmAdapter(scmFacade, config, fileExclusions, moduleFileSystem);
    ChangeLogHandler changeLogHandler = genericScmAdapter.getChangeLog(dateRange);
    assertThat(changeLogHandler.getChangeLogs()).isEmpty();

  }
  @Test
  public void shouldGetEmptyHolderWhenExceptionIsThrown() throws ScmException {
    DateRange dateRange = new DateRange(datefrom, dateTo);
    when(scmFacade.getChangeLog(projectBaseDir, datefrom.toDate(), dateTo.toDate())).thenThrow(ScmException.class);
    GenericScmAdapter genericScmAdapter = new GenericScmAdapter(scmFacade, config, fileExclusions, moduleFileSystem);
    ChangeLogHandler changeLogHandler = genericScmAdapter.getChangeLog(dateRange);
    assertThat(changeLogHandler.getChangeLogs()).isEmpty();

  }
  @Test
  public void shouldGetChangeLog() {
    DateTime date1 = new DateTime();
    DateTime date2 = new DateTime();
    List<ChangeFile> files = createChangeFilesList();
    changeSets.add(new ChangeSet(date2.toDate(), null, "user2", files));
    changeSets.add(new ChangeSet(date1.toDate(), null, "user1", files));

    when(changeLog.isSuccess()).thenReturn(Boolean.TRUE);
    DateRange dateRange = new DateRange(datefrom, dateTo);

    GenericScmAdapter genericScmAdapter = new GenericScmAdapter(scmFacade, config, fileExclusions, moduleFileSystem);
    Map<String, Integer> activity = createActivityMap();
    ChangeLogInfo changeLogInfo1 = new ChangeLogInfo("user1", date1.toDate(), activity);
    ChangeLogInfo changeLogInfo2 = new ChangeLogInfo("user2", date2.toDate(), activity);

    ChangeLogHandler result = genericScmAdapter.getChangeLog(dateRange);

    assertThat(result.getChangeLogs().get(0).getAuthor()).isEqualTo(changeLogInfo2.getAuthor());
    assertThat(result.getChangeLogs().get(0).getCommitDate()).isEqualTo(changeLogInfo2.getCommitDate());
    assertThat(result.getChangeLogs().get(0).getActivity()).hasSize(3);
    assertThat(result.getChangeLogs().get(0).getActivity().get(ScmStatsConstants.ACTIVITY_ADD)).isEqualTo(1);
    assertThat(result.getChangeLogs().get(0).getActivity().get(ScmStatsConstants.ACTIVITY_MODIFY)).isEqualTo(1);
    assertThat(result.getChangeLogs().get(0).getActivity().get(ScmStatsConstants.ACTIVITY_DELETE)).isEqualTo(2);

    assertThat(result.getChangeLogs().get(1).getAuthor()).isEqualTo(changeLogInfo1.getAuthor());
    assertThat(result.getChangeLogs().get(1).getCommitDate()).isEqualTo(changeLogInfo1.getCommitDate());
    assertThat(result.getChangeLogs().get(1).getActivity()).hasSize(3);
    assertThat(result.getChangeLogs().get(1).getActivity().get(ScmStatsConstants.ACTIVITY_ADD)).isEqualTo(1);
    assertThat(result.getChangeLogs().get(1).getActivity().get(ScmStatsConstants.ACTIVITY_MODIFY)).isEqualTo(1);
    assertThat(result.getChangeLogs().get(1).getActivity().get(ScmStatsConstants.ACTIVITY_DELETE)).isEqualTo(2);
  }

  private List<ChangeFile> createChangeFilesList() {
    List<ChangeFile> files = new ArrayList<ChangeFile>();
    ChangeFile changeFile1 = new ChangeFile("ChangeFile1");
    changeFile1.setAction(ScmFileStatus.ADDED);
    ChangeFile changeFile2 = new ChangeFile("ChangeFile2");
    changeFile2.setAction(ScmFileStatus.MODIFIED);
    ChangeFile changeFile3 = new ChangeFile("ChangeFile3");
    changeFile3.setAction(ScmFileStatus.DELETED);
    ChangeFile changeFile4 = new ChangeFile("ChangeFile4");
    changeFile4.setAction(ScmFileStatus.DELETED);
    files.add(changeFile4);
    files.add(changeFile3);
    files.add(changeFile2);
    files.add(changeFile1);
    return files;
  }

  Map<String, Integer> createActivityMap() {
    Map<String, Integer> activity1 = new HashMap<String, Integer>();
    activity1.put(ScmStatsConstants.ACTIVITY_ADD, 1);
    activity1.put(ScmStatsConstants.ACTIVITY_MODIFY, 2);
    activity1.put(ScmStatsConstants.ACTIVITY_DELETE, 1);
    return activity1;
  }
}
