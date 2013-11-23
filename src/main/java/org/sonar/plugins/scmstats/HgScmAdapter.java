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

import java.util.Collections;
import org.sonar.plugins.scmstats.utils.DateRange;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.Project;
import org.sonar.plugins.scmstats.ScmConfiguration;
import org.sonar.plugins.scmstats.ScmStatsConstants;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import org.sonar.plugins.scmstats.utils.MapUtils;
import org.tmatesoft.hg.core.HgChangeset;
import org.tmatesoft.hg.core.HgDate;
import org.tmatesoft.hg.core.HgException;
import org.tmatesoft.hg.core.HgLogCommand;
import org.tmatesoft.hg.core.HgRepoFacade;
import org.tmatesoft.hg.core.HgRepositoryNotFoundException;

public class HgScmAdapter extends AbstractScmAdapter {
  
  private static final Logger LOG = LoggerFactory.getLogger(HgScmAdapter.class);
  private final HgRepoFacade hgRepo;
  
  public HgScmAdapter(HgRepoFacade hgRepoFacade, ScmConfiguration configuration) {
    super(configuration);
    hgRepo = hgRepoFacade;
  }
  @Override
  public boolean isResponsible(String scmType) {
    return "hg".equals(scmType);
  }
  @Override
  public ChangeLogHandler getChangeLog(Project project, DateRange dateRange) {
    List<HgChangeset> hgChangeSets = getHgChangeLog(project);
    if ( hgChangeSets.isEmpty()) {
      return null;
    }
    ChangeLogHandler holder = createChangeLogHolder();
    for (HgChangeset hgChangeSet : hgChangeSets) {
      String author = hgChangeSet.getUser();
      HgDate changeSetDate = hgChangeSet.getDate();
      if (author != null && hgChangeSet.getDate() != null && 
          dateRange.isDateInRange(new DateTime(changeSetDate.getRawTime())) && 
          !getConfiguration().getIgnoreAuthorsList().contains(author)) {
        holder.addChangeLog(author, new Date(changeSetDate.getRawTime()), createActivityMap(hgChangeSet));
      }
    }
    return holder;
  }

  private List<HgChangeset> getHgChangeLog(Project project) {
    try {
      LOG.info("Getting change log information for %s\n", 
              project.getFileSystem().getBasedir().getAbsolutePath());
      if (!hgRepo.initFrom(project.getFileSystem().getBasedir())) {
        throw new HgRepositoryNotFoundException(
                String.format("Can't find repository in: %s\n", 
                        project.getFileSystem().getBasedir().getAbsolutePath()));
      }
      HgLogCommand cmd = hgRepo.createLogCommand();
      return cmd.execute();

    } catch (HgException ex) {
      LOG.error("Error getting changelog!" + ex.getMessage(), ex);
    }
    return Collections.EMPTY_LIST;
  }

  private Map<String, Integer> createActivityMap(HgChangeset changeSet) {
    Map<String, Integer> fileStatus = new HashMap<String, Integer>();
    for (int i = 0; i < changeSet.getAddedFiles().size(); i++) {
      fileStatus = MapUtils.updateMap(fileStatus, ScmStatsConstants.ACTIVITY_ADD);
    }

    for (int i = 0; i < changeSet.getModifiedFiles().size(); i++) {
      fileStatus = MapUtils.updateMap(fileStatus, ScmStatsConstants.ACTIVITY_MODIFY);
    }

    for (int i = 0; i < changeSet.getRemovedFiles().size(); i++) {
      fileStatus = MapUtils.updateMap(fileStatus, ScmStatsConstants.ACTIVITY_DELETE);
    }

    return fileStatus;
  }


}
