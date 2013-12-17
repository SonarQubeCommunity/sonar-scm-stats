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
import java.util.List;
import static org.fest.assertions.Assertions.assertThat;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import org.tmatesoft.hg.core.HgRepoFacade;
import static org.mockito.Mockito.*;
import org.sonar.api.scan.filesystem.FileExclusions;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.plugins.scmstats.utils.DateRange;
import org.tmatesoft.hg.core.HgChangeset;
import org.tmatesoft.hg.core.HgDate;
import org.tmatesoft.hg.core.HgException;
import org.tmatesoft.hg.core.HgFileRevision;
import org.tmatesoft.hg.core.HgLogCommand;
import org.tmatesoft.hg.core.HgRepositoryNotFoundException;
import org.tmatesoft.hg.repo.HgRuntimeException;
import org.tmatesoft.hg.util.Path;

public class HgScmAdapterTest {
  
  private final File projectBaseDir = new File(".");
  private final Settings settings = new Settings();
  private final ScmUrlGuess scmUrlGuess = mock(ScmUrlGuess.class);
  private final FileExclusions fileExclusions = mock(FileExclusions.class);
  private final ScmConfiguration config = new ScmConfiguration(settings, scmUrlGuess);
  private final HgRepoFacade hgRepo = mock(HgRepoFacade.class);
  private final HgLogCommand hgLogCommand = mock(HgLogCommand.class);
  private final ModuleFileSystem moduleFileSystem = mock(ModuleFileSystem.class);
  private final List<HgChangeset> changeSets = new ArrayList<HgChangeset>();
  private final List<HgFileRevision> listWithOneItem = mock(List.class);
  private final List<HgFileRevision> listWithTwoItems = mock(List.class);
  private final List<Path> listWithThreeItems = mock(List.class);
  
  @Before
  public void init() throws HgRepositoryNotFoundException, HgException {
    when(moduleFileSystem.baseDir()).thenReturn(projectBaseDir);
    when(hgRepo.initFrom(projectBaseDir)).thenReturn(Boolean.TRUE);
    when(hgRepo.createLogCommand()).thenReturn(hgLogCommand);
    when(hgLogCommand.execute()).thenReturn(changeSets);
    
    when(listWithOneItem.size()).thenReturn(1);
    when(listWithTwoItems.size()).thenReturn(2);
    when(listWithThreeItems.size()).thenReturn(3);
    
    when(fileExclusions.sourceExclusions()).thenReturn(new String[0]);
    when(fileExclusions.sourceInclusions()).thenReturn(new String[0]);    
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
    
    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config, fileExclusions, moduleFileSystem);
    ChangeLogHandler result = hgScmAdapter.getChangeLog(dateRange);
    
    assertThat(result.getChangeLogs()).hasSize(0);
    
  }
  
  @Test
  public void shouldGetEmptyLogWhenHgRepoIsNotFound() throws HgRepositoryNotFoundException {
    
    when(hgRepo.initFrom(projectBaseDir)).thenThrow(HgRepositoryNotFoundException.class);
    DateRange dateRange = mock(DateRange.class);
    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config, fileExclusions, moduleFileSystem);
    ChangeLogHandler result = hgScmAdapter.getChangeLog(dateRange);
    
    assertThat(result).isNull();
    
  }
  
  @Test
  public void shouldGetEmptyLogWhenCannotExecuteLogCommand() throws HgException {
    when(hgRepo.initFrom(projectBaseDir)).thenReturn(Boolean.FALSE);
    DateRange dateRange = mock(DateRange.class);
    HgScmAdapter hgScmAdapter = new HgScmAdapter(hgRepo, config, fileExclusions, moduleFileSystem);
    ChangeLogHandler result = hgScmAdapter.getChangeLog(dateRange);
    
    assertThat(result).isNull();
  }

  
  HgChangeset createChangeSet2(DateTime date2, List<HgFileRevision> addedFiles, List<HgFileRevision> modifiedFiles, List<Path> removedFiles) throws HgRuntimeException {
    HgChangeset changeSet2 = mock(HgChangeset.class);
    when(changeSet2.getUser()).thenReturn("user2");
    when(changeSet2.getDate()).thenReturn(new HgDate(date2.getMillis(), 0));
    when(changeSet2.getAddedFiles()).thenReturn(addedFiles);
    when(changeSet2.getModifiedFiles()).thenReturn(modifiedFiles);
    when(changeSet2.getRemovedFiles()).thenReturn(removedFiles);
    return changeSet2;
  }
  
  HgChangeset createChangeSet1(DateTime date1, List<HgFileRevision> addedFiles, List<HgFileRevision> modifiedFiles, List<Path> removedFiles) throws HgRuntimeException {
    HgChangeset changeSet1 = mock(HgChangeset.class);
    when(changeSet1.getUser()).thenReturn("user1");
    when(changeSet1.getDate()).thenReturn(new HgDate(date1.getMillis(), 0));
    when(changeSet1.getAddedFiles()).thenReturn(addedFiles);
    when(changeSet1.getModifiedFiles()).thenReturn(modifiedFiles);
    when(changeSet1.getRemovedFiles()).thenReturn(removedFiles);
    return changeSet1;
  }
}
