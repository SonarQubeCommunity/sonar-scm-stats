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
import java.util.Collections;
import org.sonar.plugins.scmstats.utils.DateRange;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.scan.filesystem.FileExclusions;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import org.tmatesoft.hg.core.HgChangeset;
import org.tmatesoft.hg.core.HgDate;
import org.tmatesoft.hg.core.HgException;
import org.tmatesoft.hg.core.HgFileRevision;
import org.tmatesoft.hg.core.HgLogCommand;
import org.tmatesoft.hg.core.HgRepoFacade;
import org.tmatesoft.hg.core.HgRepositoryNotFoundException;
import org.tmatesoft.hg.util.Path;

public class HgScmAdapter extends AbstractScmAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(HgScmAdapter.class);
  private final HgRepoFacade hgRepo;

  public HgScmAdapter(HgRepoFacade hgRepoFacade, ScmConfiguration configuration, 
          FileExclusions fileExclusions, ModuleFileSystem moduleFileSystem) {
    super(configuration, fileExclusions, moduleFileSystem);
    hgRepo = hgRepoFacade;
  }

  @Override
  public boolean isResponsible(String scmType) {
    return "hg".equals(scmType);
  }

  @Override
  public ChangeLogHandler getChangeLog(DateRange dateRange) {
    List<HgChangeset> hgChangeSets = getHgChangeLog();
    if (hgChangeSets.isEmpty()) {
      return null;
    }
    ChangeLogHandler holder = createChangeLogHolder();

    for (HgChangeset hgChangeSet : hgChangeSets) {
      String author = hgChangeSet.getUser();
      HgDate changeSetDate = hgChangeSet.getDate();

      if (author != null && hgChangeSet.getDate() != null
              && dateRange.isDateInRange(new DateTime(changeSetDate.getRawTime()))
              && !getConfiguration().getIgnoreAuthorsList().contains(author)) {
        holder.addChangeLog(author, new Date(changeSetDate.getRawTime()), createActivityMap(hgChangeSet));
      }
    }
    return holder;
  }

  private List<HgChangeset> getHgChangeLog() {
    
    File baseDir = getModuleFileSystem().baseDir();
    
    try {
      LOG.info("Getting change log information for %s\n", baseDir.getAbsolutePath());
      if (!hgRepo.initFrom(baseDir)) {
        throw new HgRepositoryNotFoundException(
                String.format("Can't find repository in: %s\n",baseDir.getAbsolutePath()));
      }
      HgLogCommand cmd = hgRepo.createLogCommand();
      return cmd.execute();

    } catch (HgException ex) {
      LOG.error("Error getting changelog!" + ex.getMessage(), ex);
    }
    return Collections.EMPTY_LIST;
  }

  Map<String, Integer> createActivityMap(HgChangeset changeSet) {
    Map<String, Integer> fileStatus = new HashMap<String, Integer>();

    for (HgFileRevision hgFileRevision : changeSet.getAddedFiles()) {
      fileStatus = updateActivity(hgFileRevision.getPath().toString(), fileStatus, ScmStatsConstants.ACTIVITY_ADD);
    }

    for (HgFileRevision hgFileRevision : changeSet.getModifiedFiles()) {
      fileStatus = updateActivity(hgFileRevision.getPath().toString(), fileStatus, ScmStatsConstants.ACTIVITY_MODIFY);
    }

    for (Path path : changeSet.getRemovedFiles()) {
      fileStatus = updateActivity(path.toString(), fileStatus, ScmStatsConstants.ACTIVITY_DELETE);
    }
    return fileStatus;
  }


}
