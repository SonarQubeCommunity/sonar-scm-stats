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

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.ListUtils;
import static org.fest.assertions.Assertions.assertThat;
import org.fest.assertions.MapAssert;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sonar.api.config.Settings;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import org.tmatesoft.hg.core.HgRepoFacade;
import static org.mockito.Mockito.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sonar.api.scan.filesystem.FileExclusions;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.plugins.scmstats.model.ChangeLogInfo;
import org.sonar.plugins.scmstats.utils.DateRange;
import org.tmatesoft.hg.core.HgChangeset;
import org.tmatesoft.hg.core.HgDate;
import org.tmatesoft.hg.core.HgException;
import org.tmatesoft.hg.core.HgFileRevision;
import org.tmatesoft.hg.core.HgLogCommand;
import org.tmatesoft.hg.core.HgRepositoryNotFoundException;
import org.tmatesoft.hg.repo.HgRuntimeException;
import org.tmatesoft.hg.util.Path;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HgFileRevision.class, Path.class, HgDate.class})
public class HgScmAdapterPowerMockTest {

  private final File projectBaseDir = new File(".");
  private final Settings settings = new Settings();
  private final ScmUrlGuess scmUrlGuess = mock(ScmUrlGuess.class);
  private final FileExclusions fileExclusions = mock(FileExclusions.class);
  private final ScmConfiguration config = new ScmConfiguration(settings, scmUrlGuess);
  private final HgRepoFacade hgRepo = mock(HgRepoFacade.class);
  private final HgLogCommand hgLogCommand = mock(HgLogCommand.class);
  private final ModuleFileSystem moduleFileSystem = mock(ModuleFileSystem.class);
  private final List<HgChangeset> changeSets = new ArrayList<HgChangeset>();

  @Before
  public void init() throws HgRepositoryNotFoundException, HgException {
    when(moduleFileSystem.baseDir()).thenReturn(projectBaseDir);
    when(hgRepo.initFrom(projectBaseDir)).thenReturn(Boolean.TRUE);
    when(hgRepo.createLogCommand()).thenReturn(hgLogCommand);

    when(fileExclusions.sourceExclusions()).thenReturn(new String[0]);
    when(fileExclusions.sourceInclusions()).thenReturn(new String[0]);

    when(hgLogCommand.execute()).thenReturn(changeSets);
  }

  @Test
  public void shouldGetCompleteChangeLog() {
    DateRange dateRange = mock(DateRange.class);
    when(dateRange.isDateInRange(any(DateTime.class))).thenReturn(Boolean.TRUE);

    changeSets.add(getMockedChangeSet());

    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config, fileExclusions, moduleFileSystem);
    ChangeLogHandler result = hgScmAdapter.getChangeLog(dateRange);
    assertThat(result.getChangeLogs()).hasSize(1);
    assertThat(result.getChangeLogs().get(0).getAuthor()).isEqualTo("Author");
    assertThat(result.getChangeLogs().get(0).getCommitDate()).isEqualTo(new DateTime(2013, 12, 31, 0, 0).toDate());
    assertThat(result.getChangeLogs().get(0).getActivity()).hasSize(3);

  }

  @Test
  public void shouldCreateActivityMapWithAffectedFiles() throws HgRepositoryNotFoundException {
    when(hgRepo.initFrom(projectBaseDir)).thenReturn(Boolean.FALSE);
    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config, fileExclusions, moduleFileSystem);
    HgChangeset hgChangeSet = getMockedChangeSet();

    Map<String, Integer> activities = hgScmAdapter.createActivityMap(hgChangeSet);
    assertThat(activities).includes(MapAssert.entry(ScmStatsConstants.ACTIVITY_ADD, 2),
            MapAssert.entry(ScmStatsConstants.ACTIVITY_MODIFY, 1),
            MapAssert.entry(ScmStatsConstants.ACTIVITY_DELETE, 1));

  }

  private HgChangeset getMockedChangeSet() throws HgRuntimeException {
    HgChangeset hgChangeSet = mock(HgChangeset.class);
    Path path = mock(Path.class);
    HgFileRevision rev1 = mock(HgFileRevision.class);
    HgFileRevision rev2 = mock(HgFileRevision.class);
    when(rev1.getPath()).thenReturn(path);
    when(rev2.getPath()).thenReturn(path);
    List<HgFileRevision> addedFiles = ImmutableList.of(rev1, rev2);
    List<Path> removedFiles = ImmutableList.of(path);
    List<HgFileRevision> modifiedFiles = ImmutableList.of(rev1);

    HgDate hgDate = mock(HgDate.class);
    when(hgDate.getRawTime()).thenReturn(new DateTime(2013, 12, 31, 0, 0).getMillis());
    when(hgChangeSet.getUser()).thenReturn("Author");
    when(hgChangeSet.getDate()).thenReturn(hgDate);
    when(hgChangeSet.getAddedFiles()).thenReturn(addedFiles);
    when(hgChangeSet.getModifiedFiles()).thenReturn(modifiedFiles);
    when(hgChangeSet.getRemovedFiles()).thenReturn(removedFiles);
    return hgChangeSet;
  }

  @Test
  public void shouldCreateActivityMapWithoutAffectedFiles() throws HgRepositoryNotFoundException {
    when(hgRepo.initFrom(projectBaseDir)).thenReturn(Boolean.FALSE);
    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config, fileExclusions, moduleFileSystem);
    HgChangeset hgChangeSet = mock(HgChangeset.class);

    when(hgChangeSet.getAddedFiles()).thenReturn(ListUtils.EMPTY_LIST);
    when(hgChangeSet.getModifiedFiles()).thenReturn(ListUtils.EMPTY_LIST);
    when(hgChangeSet.getRemovedFiles()).thenReturn(ListUtils.EMPTY_LIST);

    Map<String, Integer> activities = hgScmAdapter.createActivityMap(hgChangeSet);
    assertThat(activities).isEmpty();

  }

  @Test
  public void should_not_add_changelog_when_author_isempty() {
    DateRange dateRange = mock(DateRange.class);
    when(dateRange.isDateInRange(any(DateTime.class))).thenReturn(Boolean.TRUE);
    HgChangeset hgChangeSet = mock(HgChangeset.class);
    Path path = mock(Path.class);
    List<Path> removedFiles = ImmutableList.of(path);

    HgDate hgDate = mock(HgDate.class);
    when(hgDate.getRawTime()).thenReturn(new DateTime(2013, 12, 31, 0, 0).getMillis());
    when(hgChangeSet.getUser()).thenReturn(null);
    when(hgChangeSet.getDate()).thenReturn(hgDate);
    when(hgChangeSet.getRemovedFiles()).thenReturn(removedFiles);

    changeSets.add(hgChangeSet);
    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config, fileExclusions, moduleFileSystem);
    ChangeLogHandler handler = hgScmAdapter.getChangeLog(dateRange);

    assertThat(handler.getChangeLogs()).hasSize(0);
  }
  @Test
  public void should_not_add_changelog_when_changeDate_isempty() {
    DateRange dateRange = mock(DateRange.class);
    when(dateRange.isDateInRange(any(DateTime.class))).thenReturn(Boolean.TRUE);
    HgChangeset hgChangeSet = mock(HgChangeset.class);
    Path path = mock(Path.class);
    List<Path> removedFiles = ImmutableList.of(path);

    when(hgChangeSet.getUser()).thenReturn("Author");
    when(hgChangeSet.getDate()).thenReturn(null);
    when(hgChangeSet.getRemovedFiles()).thenReturn(removedFiles);

    changeSets.add(hgChangeSet);
    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config, fileExclusions, moduleFileSystem);
    ChangeLogHandler handler = hgScmAdapter.getChangeLog(dateRange);

    assertThat(handler.getChangeLogs()).hasSize(0);
  }
}
