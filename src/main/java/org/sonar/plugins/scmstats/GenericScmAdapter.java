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
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.resources.Project;
import org.sonar.plugins.scmstats.ScmConfiguration;
import org.sonar.plugins.scmstats.ScmFacade;
import org.sonar.plugins.scmstats.ScmStatsConstants;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import org.sonar.plugins.scmstats.utils.MapUtils;

public class GenericScmAdapter extends AbstractScmAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(HgScmAdapter.class);
  private final ScmFacade scmFacade;

  public GenericScmAdapter(ScmFacade scmFacade, ScmConfiguration configuration) {
    super(configuration);
    this.scmFacade = scmFacade;
  }
  @Override
  public boolean isResponsible(String scmType) {
    return !"hg".equals(scmType);
  }
  @Override
  public ChangeLogHandler getChangeLog(Project project, DateRange dateRange) {
    LOG.info("Getting change log information for %s\n",
            project.getFileSystem().getBasedir().getAbsolutePath());
    ChangeLogHandler holder = createChangeLogHolder();
    try {
      ChangeLogScmResult changeLogScmResult = scmFacade.getChangeLog(
                    project.getFileSystem().getBasedir(), 
                    dateRange.getFrom().toDate(), 
                    dateRange.getTo().toDate());
      
      if (changeLogScmResult.isSuccess()) {
        for (ChangeSet changeSet : changeLogScmResult.getChangeLog().getChangeSets()) {
          holder = addChangeLogToHolder(changeSet, holder);
        }
      } else {
        LOG.warn(String.format("Fail to retrieve SCM info. Reason: %s%n%s",
                changeLogScmResult.getProviderMessage(),
                changeLogScmResult.getCommandOutput()));
      }
    } catch (ScmException ex) {
        LOG.warn(String.format("Fail to retrieve SCM info. Reason: %s\n",
                ex.getMessage()),ex);
    }
    return holder;
  }

  private ChangeLogHandler addChangeLogToHolder(ChangeSet changeSet, ChangeLogHandler holder) {
    if (changeSet.getAuthor() != null && changeSet.getDate() != null
            && !getConfiguration().getIgnoreAuthorsList().contains(changeSet.getAuthor())) {
      holder.addChangeLog(changeSet.getAuthor(), changeSet.getDate(), createActivityMap(changeSet));
    }
    return holder;
  }

  private Map<String, Integer> createActivityMap(ChangeSet changeSet) {
    Map<String, Integer> fileStatus = new HashMap<String, Integer>();
    for (ChangeFile changeFile : changeSet.getFiles()) {
      if (changeFile.getAction() == ScmFileStatus.ADDED) {
        fileStatus = MapUtils.updateMap(fileStatus, ScmStatsConstants.ACTIVITY_ADD);
      } else if (changeFile.getAction() == ScmFileStatus.MODIFIED) {
        fileStatus = MapUtils.updateMap(fileStatus, ScmStatsConstants.ACTIVITY_MODIFY);
      } else if (changeFile.getAction() == ScmFileStatus.DELETED) {
        fileStatus = MapUtils.updateMap(fileStatus, ScmStatsConstants.ACTIVITY_DELETE);
      }
    }
    return fileStatus;
  }
}
