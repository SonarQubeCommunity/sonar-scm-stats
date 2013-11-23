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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.fest.assertions.Assertions.assertThat;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ProjectFileSystem;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import org.tmatesoft.hg.core.HgRepoFacade;
import static org.mockito.Mockito.*;
import org.sonar.plugins.scmstats.model.ChangeLogInfo;
import org.sonar.plugins.scmstats.utils.DateRange;
import org.tmatesoft.hg.core.HgChangeset;
import org.tmatesoft.hg.core.HgDate;
import org.tmatesoft.hg.core.HgException;
import org.tmatesoft.hg.core.HgFileRevision;
import org.tmatesoft.hg.core.HgLogCommand;
import org.tmatesoft.hg.core.HgRepositoryNotFoundException;
import org.tmatesoft.hg.repo.HgRepository;
import org.tmatesoft.hg.repo.HgRuntimeException;
import org.tmatesoft.hg.util.Path;

public class HgScmAdapterTest {

  private final File projectBaseDir = new File(".");
  private final Project myProject = mock(Project.class);
  private final Settings settings = new Settings();
  private final ScmUrlGuess scmUrlGuess = mock(ScmUrlGuess.class);
  private final ScmConfiguration config = new ScmConfiguration(settings, scmUrlGuess);
  private final HgRepoFacade hgRepo = mock(HgRepoFacade.class);
  private final HgLogCommand hgLogCommand = mock(HgLogCommand.class);
  private final ProjectFileSystem projectFileSystem = mock(ProjectFileSystem.class);
  private final List<HgChangeset> changeSets = new ArrayList<HgChangeset>();
  private final List<HgFileRevision> listWithOneItem = mock(List.class);
  private final List<HgFileRevision> listWithTwoItems = mock(List.class);
  private final List<Path> listWithThreeItems = mock(List.class);

  @Before
  public void init() throws HgRepositoryNotFoundException, HgException {
    when(projectFileSystem.getBasedir()).thenReturn(projectBaseDir);
    when(myProject.getFileSystem()).thenReturn(projectFileSystem);
    when(hgRepo.initFrom(projectBaseDir)).thenReturn(Boolean.TRUE);
    when(hgRepo.createLogCommand()).thenReturn(hgLogCommand);
    when(hgLogCommand.execute()).thenReturn(changeSets);

    when(listWithOneItem.size()).thenReturn(1);
    when(listWithTwoItems.size()).thenReturn(2);
    when(listWithThreeItems.size()).thenReturn(3);
  }

  @Test
  public void shouldGetCompleteChangeLog() {
    DateRange dateRange = mock(DateRange.class);
    when(dateRange.isDateInRange(any(DateTime.class))).thenReturn(Boolean.TRUE);

    DateTime date1 = new DateTime();
    HgChangeset changeSet1 = createChangeSet1(date1, listWithOneItem, listWithTwoItems, listWithThreeItems);

    DateTime date2 = new DateTime();
    HgChangeset changeSet2 = createChangeSet2(date2, listWithOneItem, listWithTwoItems, listWithThreeItems);

    changeSets.add(changeSet2);
    changeSets.add(changeSet1);

    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config);
    Map<String, Integer> activity1 = createActivityMap();
    ChangeLogInfo changeLogInfo1 = new ChangeLogInfo("user1", date1.toDate(), activity1);
    ChangeLogInfo changeLogInfo2 = new ChangeLogInfo("user2", date2.toDate(), activity1);
    ChangeLogHandler result = hgScmAdapter.getChangeLog(myProject, dateRange);

    assertThat(result.getChangeLogs().get(0).getAuthor()).isEqualTo(changeLogInfo2.getAuthor());
    assertThat(result.getChangeLogs().get(0).getCommitDate()).isEqualTo(changeLogInfo2.getCommitDate());
    assertThat(result.getChangeLogs().get(0).getActivity()).hasSize(3);
    assertThat(result.getChangeLogs().get(0).getActivity().get(ScmStatsConstants.ACTIVITY_ADD)).isEqualTo(1);
    assertThat(result.getChangeLogs().get(0).getActivity().get(ScmStatsConstants.ACTIVITY_MODIFY)).isEqualTo(2);
    assertThat(result.getChangeLogs().get(0).getActivity().get(ScmStatsConstants.ACTIVITY_DELETE)).isEqualTo(3);

    assertThat(result.getChangeLogs().get(1).getAuthor()).isEqualTo(changeLogInfo1.getAuthor());
    assertThat(result.getChangeLogs().get(1).getCommitDate()).isEqualTo(changeLogInfo1.getCommitDate());
    assertThat(result.getChangeLogs().get(1).getActivity()).hasSize(3);
    assertThat(result.getChangeLogs().get(1).getActivity().get(ScmStatsConstants.ACTIVITY_ADD)).isEqualTo(1);
    assertThat(result.getChangeLogs().get(1).getActivity().get(ScmStatsConstants.ACTIVITY_MODIFY)).isEqualTo(2);
    assertThat(result.getChangeLogs().get(1).getActivity().get(ScmStatsConstants.ACTIVITY_DELETE)).isEqualTo(3);
  }

  @Test
  public void shouldGetChangeLogWithinDates() {
    DateTime date1 = new DateTime();
    DateTime date2 = new DateTime();
    DateRange dateRange = mock(DateRange.class);
    when(dateRange.isDateInRange(any(DateTime.class))).thenReturn(Boolean.FALSE);

    HgChangeset changeSet1 = createChangeSet1(date1, listWithOneItem, listWithTwoItems, listWithThreeItems);
    HgChangeset changeSet2 = createChangeSet2(date2, listWithOneItem, listWithTwoItems, listWithThreeItems);

    changeSets.add(changeSet2);
    changeSets.add(changeSet1);

    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config);
    ChangeLogHandler result = hgScmAdapter.getChangeLog(myProject, dateRange);

    assertThat(result.getChangeLogs()).hasSize(0);

  }

  @Test
  public void shouldGetEmptyLogWhenHgRepoIsNotFound() throws HgRepositoryNotFoundException {

    when(hgRepo.initFrom(projectBaseDir)).thenThrow(HgRepositoryNotFoundException.class);
    DateRange dateRange = mock(DateRange.class);
    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config);
    ChangeLogHandler result = hgScmAdapter.getChangeLog(myProject, dateRange);

    assertThat(result).isNull();

  }

  @Test
  public void shouldGetEmptyLogWhenCannotExecuteLogCommand() throws HgException {
    when(hgRepo.initFrom(projectBaseDir)).thenReturn(Boolean.FALSE);
    DateRange dateRange = mock(DateRange.class);
    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config);
    ChangeLogHandler result = hgScmAdapter.getChangeLog(myProject, dateRange);

    assertThat(result).isNull();

  }

  Map<String, Integer> createActivityMap() {
    Map<String, Integer> activity1 = new HashMap<String, Integer>();
    activity1.put(ScmStatsConstants.ACTIVITY_ADD, 1);
    activity1.put(ScmStatsConstants.ACTIVITY_MODIFY, 2);
    activity1.put(ScmStatsConstants.ACTIVITY_DELETE, 3);
    return activity1;
  }

  HgChangeset createChangeSet2(DateTime date2, List<HgFileRevision> listWithOneItem, List<HgFileRevision> listWithTwoItems, List<Path> listWithThreeItems) throws HgRuntimeException {
    HgChangeset changeSet2 = mock(HgChangeset.class);
    when(changeSet2.getUser()).thenReturn("user2");
    when(changeSet2.getDate()).thenReturn(new HgDate(date2.getMillis(), 0));
    when(changeSet2.getAddedFiles()).thenReturn(listWithOneItem);
    when(changeSet2.getModifiedFiles()).thenReturn(listWithTwoItems);
    when(changeSet2.getRemovedFiles()).thenReturn(listWithThreeItems);
    return changeSet2;
  }

  HgChangeset createChangeSet1(DateTime date1, List<HgFileRevision> listWithOneItem, List<HgFileRevision> listWithTwoItems, List<Path> listWithThreeItems) throws HgRuntimeException {
    HgChangeset changeSet1 = mock(HgChangeset.class);
    when(changeSet1.getUser()).thenReturn("user1");
    when(changeSet1.getDate()).thenReturn(new HgDate(date1.getMillis(), 0));
    when(changeSet1.getAddedFiles()).thenReturn(listWithOneItem);
    when(changeSet1.getModifiedFiles()).thenReturn(listWithTwoItems);
    when(changeSet1.getRemovedFiles()).thenReturn(listWithThreeItems);
    return changeSet1;
  }
}
