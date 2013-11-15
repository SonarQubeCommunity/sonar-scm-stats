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
import java.util.ArrayList;
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
import org.sonar.plugins.scmstats.utils.FileUtils;
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

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return configuration.isEnabled() && urlChecker.check(configuration.getUrl());
  }

  @Override
  public void analyse(Project project, SensorContext context) {
    String perfoceClientSpec = configuration.getPerforceClientSpec();
    if (perfoceClientSpec != null) {
      System.setProperty("maven.scm.perforce.clientspec.name", perfoceClientSpec);
    }
    List<String> periods = ScmStatsConstants.getPeriodsAsList();
    for (String period : periods) {
      analyseChangeLog(project, context, period);
    }
  }

  @VisibleForTesting
  protected void analyseChangeLog(Project project, SensorContext context, String period) {
    int numDays = configuration.getSettings().getInt(period);

    if (numDays > 0 || period.equals(ScmStatsConstants.PERIOD_1)) {
      try {
        LOG.info("Collection SCM Change log for the last " + numDays + " days");

        ChangeLogScmResult changeLogScmResult
                = scmFacade.getChangeLog(project.getFileSystem().getBasedir(), numDays);
        if (changeLogScmResult.isSuccess()) {
          List<String> filesToProcess = new FileUtils().getFilesToProcess(project,configuration.getSettings());
          filterFiles(changeLogScmResult, filesToProcess);
          generateAndSaveMeasures(changeLogScmResult, context, period);
        } else {
          LOG.warn(String.format("Fail to retrieve SCM info. Reason: %s%n%s",
                  changeLogScmResult.getProviderMessage(),
                  changeLogScmResult.getCommandOutput()));
        }
      } catch (ScmException e) {
        LOG.warn(String.format("Fail to retrieve SCM info."), e);
      }
    }
  }

  private void filterFiles(ChangeLogScmResult changeLogScmResult, List<String> filesToProcess) {
    if (changeLogScmResult.getChangeLog() != null && filesToProcess != null) {
      List<ChangeSet> changeSets = new ArrayList<ChangeSet>(changeLogScmResult.getChangeLog().getChangeSets());
      for (ChangeSet changeSet : changeSets) {
        LOG.debug("Processing changeSet:" + changeSet.getDateFormatted() + " by " + changeSet.getAuthor());
        List<ChangeFile> changeSetFiles = new ArrayList<ChangeFile>(changeSet.getFiles());
        for (ChangeFile changeFile : changeSetFiles) {
          if (!filesToProcess.contains(changeFile.getName())) {
            LOG.debug(changeFile.getName() + " file will be dropped!");
            changeSet.getFiles().remove(changeFile);
          }
        }
        if (changeSet.getFiles().isEmpty()) {
          LOG.debug("Removing changeSet:" + changeSet.getDateFormatted() + " by " + changeSet.getAuthor());
          changeLogScmResult.getChangeLog().getChangeSets().remove(changeSet);
        }
        
      }
    }
  }
 

  @VisibleForTesting
  protected void generateAndSaveMeasures(ChangeLogScmResult changeLogScmResult, SensorContext context, String period) {
    ChangeLogHandler holder = new ChangeLogHandler(
            configuration.getIgnoreAuthorsList(),
            configuration.getMergeAuthorsList());
    for (ChangeSet changeSet : changeLogScmResult.getChangeLog().getChangeSets()) {
      holder = addChangeLogToHolder(changeSet, holder);
    }
    holder.generateMeasures();
    holder.saveMeasures(context, period);
  }

  @VisibleForTesting
  protected ChangeLogHandler addChangeLogToHolder(ChangeSet changeSet, ChangeLogHandler holder) {
    if (changeSet.getAuthor() != null && changeSet.getDate() != null
            && !configuration.getIgnoreAuthorsList().contains(changeSet.getAuthor())) {
      holder.addChangeLog(changeSet.getAuthor(), changeSet.getDate(), createActivityMap(changeSet));
    }
    return holder;
  }

  @VisibleForTesting
  protected Map<String, Integer> createActivityMap(ChangeSet changeSet) {
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

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
