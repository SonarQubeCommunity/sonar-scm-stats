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

import java.util.List;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.plugins.scmstats.model.ChangeLogInfoHolder;

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
    return project.isLatestAnalysis() && configuration.isEnabled();
  }

  public void analyse(Project project, SensorContext context) {
    urlChecker.check(configuration.getUrl());
    try {
      ChangeLogScmResult changeLogScmResult = scmFacade.getChangeLog(project.getFileSystem().getBasedir());
      if (changeLogScmResult.isSuccess()) {
        List<ChangeSet> changeSets = changeLogScmResult.getChangeLog().getChangeSets();
        ChangeLogInfoHolder holder = new ChangeLogInfoHolder();
        for (ChangeSet changeSet : changeSets) {
          holder.addChangeLog(changeSet.getAuthor(),changeSet.getDate(),changeSet.getRevision());
        }
        holder.generateMeasures(context);
      }else{
        LOG.warn(String.format("Fail to retrieve SCM info. Reason: %s%n%s",changeLogScmResult.getProviderMessage(), changeLogScmResult.getCommandOutput()));
      }
    } catch (ScmException e) {
      LOG.warn(String.format("Fail to retrieve SCM info."), e); // See SONARPLUGINS-368. Can occur on generated source
    }
  }
  
}
