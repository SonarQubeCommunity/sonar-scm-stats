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

import com.google.common.annotations.VisibleForTesting;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.scm.ChangeFile;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import org.sonar.plugins.scmstats.utils.MapUtils;

public class ScmStatsSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(ScmStatsSensor.class);
  private final ScmConfiguration configuration;
  private final UrlChecker urlChecker;
  private final ScmFacade scmFacade;

  public ScmStatsSensor(ScmConfiguration configuration, UrlChecker urlChecker, ScmFacade scmFacade) {
    this.configuration = configuration;
    this.urlChecker = urlChecker;
    this.scmFacade = scmFacade;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return project.isLatestAnalysis() && configuration.isEnabled()
            && urlChecker.check(configuration.getUrl());
  }

  public void analyse(Project project, SensorContext context) {

    List<String> periods = ScmStatsConstants.getPeriodsAsList();
    for (String period : periods) {
      analyseChangeLog(project, context, period);
    }
  }

  private void analyseChangeLog(Project project, SensorContext context, String period) {
    int numDays = configuration.getSettings().getInt(period);

    if (numDays > 0 || period.equals(ScmStatsConstants.PERIOD_1)) {
      try {
        LOG.info("Collection SCM Change log for the last " + numDays + " days");
        ChangeLogScmResult changeLogScmResult =
                scmFacade.getChangeLog(project.getFileSystem().getBasedir(), numDays);
        if (changeLogScmResult.isSuccess()) {
          generateAndSaveMeasures(changeLogScmResult, context, period);
        } else {
          LOG.warn(String.format("Fail to retrieve SCM info. Reason: %s%n%s",
                  changeLogScmResult.getProviderMessage(),
                  changeLogScmResult.getCommandOutput()));
        }
      } catch (ScmException e) {
        LOG.warn(String.format("Fail to retrieve SCM info."), e); // See SONARPLUGINS-368. Can occur on generated source
      }
    }
  }

  private void generateAndSaveMeasures(ChangeLogScmResult changeLogScmResult, SensorContext context, String period) {
    ChangeLogHandler holder = new ChangeLogHandler();
    for (ChangeSet changeSet : changeLogScmResult.getChangeLog().getChangeSets()) {
      holder = addChangeLogToHolder(changeSet, holder);
    }
    holder.generateMeasures();
    holder.saveMeasures(context, period);
  }

  @VisibleForTesting
  ChangeLogHandler addChangeLogToHolder(ChangeSet changeSet, ChangeLogHandler holder) {
    if (changeSet.getAuthor() != null && changeSet.getDate() != null) {
      holder.addChangeLog(changeSet.getAuthor(), changeSet.getDate(), createActivityMap(changeSet));
    }
    return holder;
  }

  @VisibleForTesting
  Map<String, Integer> createActivityMap(ChangeSet changeSet) {
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
