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
import java.util.List;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;
import org.sonar.plugins.scmstats.measures.ScmStatsMetrics;

@Properties({
  @Property(key = ScmStatsConstants.ENABLED,
  defaultValue = "" + ScmStatsPlugin.ENABLED_DEFAULT,
  name = "Activation of SCM Stats plugin",
  description = "This property can be set to false in order to deactivate the SCM Stats plugin.",
  module = true,
  project = true,
  global = true),
  @Property(key = ScmStatsConstants.PERIOD_1,
  type = org.sonar.api.PropertyType.INTEGER,
  defaultValue = "0",
  name = "Period 1",
  description = "Period (in number of days before analysis) used to collect SCM Stats.Changing this property only takes effect after subsequent project inspections. Set to 0 to collect all Scm Stats from the beginning of the project",
  module = true,
  project = true,
  global = true),
  @Property(key = ScmStatsConstants.PERIOD_2,
  type = org.sonar.api.PropertyType.INTEGER,
  name = "Period 2",
  description = "See Period 1. If it's set to a non-positive value, then it's ignored",
  module = true,
  project = true,
  global = true),
  @Property(key = ScmStatsConstants.PERIOD_3,
  type = org.sonar.api.PropertyType.INTEGER,
  name = "Period 3",
  description = "See Period 1. If it's set to a non-positive value, then it's ignored",
  module = true,
  project = true,
  global = true)
})
public final class ScmStatsPlugin extends SonarPlugin {

  public static final boolean ENABLED_DEFAULT = true;

  public List getExtensions() {
    return ImmutableList.of(
            UrlChecker.class,
            SonarScmManager.class,
            ScmFacade.class,
            MavenScmConfiguration.class,
            ScmConfiguration.class,
            ScmStatsSensor.class,
            ScmStatsMetrics.class,
            ScmStatsDashboard.class,
            // Widgets
            ScmStatsCommitsPerClockHourWidget.class,
            ScmStatsCommitsPerMonthWidget.class,
            ScmStatsCommitsPerWeekDayWidget.class,
            ScmStatsCommitsPerUserWidget.class,
            ScmStatsAuthorsActivityWidget.class,
            // Charts
            StackedBarChart3D.class,
            PieChart3D.class);
  }
}