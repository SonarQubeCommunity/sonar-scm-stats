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

import org.sonar.plugins.scmstats.utils.UrlChecker;
import com.google.common.annotations.VisibleForTesting;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;
import org.sonar.plugins.scmstats.measures.ChangeLogHandler;
import org.sonar.plugins.scmstats.utils.DateRange;

public class ScmStatsSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(ScmStatsSensor.class);
  private final ScmConfiguration configuration;
  private final UrlChecker urlChecker;
  private final ScmAdapterFactory scmAdapterFactory;

  public ScmStatsSensor(ScmConfiguration configuration, 
          UrlChecker urlChecker,
          ScmAdapterFactory scmAdapterFactory) {
    this.configuration = configuration;
    this.urlChecker = urlChecker;
    this.scmAdapterFactory = scmAdapterFactory;
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
      LOG.info("Collection SCM Change log for the last " + numDays + " days");
      LOG.info("sonar.projectDate setting " + getProjectDateProperty());
      ChangeLogHandler holder = scmAdapterFactory.getScmAdapter().
              getChangeLog(project, DateRange.getDateRange(
                                numDays, getProjectDateProperty()));
      
      holder.generateMeasures();
      holder.saveMeasures(context, period);
    }
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  DateTime getProjectDateProperty() {
    Date date = configuration.getSettings().getDate(CoreProperties.PROJECT_DATE_PROPERTY);
    return new DateTime(date.getTime());
  }
}
