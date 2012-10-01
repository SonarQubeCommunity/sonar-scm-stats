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

import org.sonar.plugins.scmstats.measures.ScmStatsMetrics;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.SonarPlugin;

@Properties({
  @Property(key = ScmStatsPlugin.ENABLED,
  defaultValue = "" + ScmStatsPlugin.ENABLED_DEFAULT,
  name = "Activation of SCM Stats plugin",
  description = "This property can be set to false in order to deactivate the SCM Stats plugin.",
  module = true,
  project = true,
  global = true)
 })
public final class ScmStatsPlugin extends SonarPlugin {

  public static final String ENABLED = "sonar.scm-stats.enabled";
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
            ScmStatsCommitsPerClockHourWidget.class,
            ScmStatsCommitsPerMonthWidget.class,
            ScmStatsCommitsPerWeekDayWidget.class,
            ScmStatsCommitsPerUserWidget.class,
            PieChart3D.class);
  }
}