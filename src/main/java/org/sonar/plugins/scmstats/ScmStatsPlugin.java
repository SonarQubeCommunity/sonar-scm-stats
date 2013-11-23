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
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.plugins.scmstats.measures.ScmStatsMetrics;

public final class ScmStatsPlugin extends SonarPlugin {

  public static final String ENABLED_DEFAULT = "true";
  public static final String SCMSTATS_CATEGORY = "Scm Stats";
  public static final String COMMON_SUBCATEGORY = "Common";
  public static final String PERFORCE_SUBCATEGORY = "Perforce";

  public List getExtensions() {
    return ImmutableList.of(
              PropertyDefinition.builder(ScmStatsConstants.ENABLED).
              defaultValue(ScmStatsPlugin.ENABLED_DEFAULT).
              name("Activation of SCM Stats plugin").
              description("This property can be set to false in order to deactivate the SCM Stats plugin.").
              index(0).
              onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).
              category(SCMSTATS_CATEGORY).
              type(PropertyType.BOOLEAN).
              build(),
            
            PropertyDefinition.builder(ScmStatsConstants.PERIOD_1).
              defaultValue("0").
              name("Period #1").
              description("Period (in number of days before analysis) used to collect SCM Stats."
              + "Changing this property only takes effect after subsequent project inspections. Set to 0 to collect all Scm Stats from the beginning of the project").
              index(1).
              onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).
              category(SCMSTATS_CATEGORY).
              type(PropertyType.INTEGER).
              build(),
            
            PropertyDefinition.builder(ScmStatsConstants.PERIOD_2).
              defaultValue("0").
              name("Period #2").
              description("See description of Period #1 property. If it's set to a non-positive value, then it's ignored").
              index(2).
              onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).
              category(SCMSTATS_CATEGORY).
              type(PropertyType.INTEGER).
              build(),

            PropertyDefinition.builder(ScmStatsConstants.PERIOD_3).
              defaultValue("0").
              name("Period #2").
              description("See description of Period #1 property. If it's set to a non-positive value, then it's ignored").
              index(3).
              onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).
              category(SCMSTATS_CATEGORY).
              type(PropertyType.INTEGER).
              build(),

            PropertyDefinition.builder(ScmStatsConstants.IGNORE_AUTHORS_LIST).
              name("Ignore author(s) list").
              description("A list of authors names to be ignored when computing and displaying stats.").
              index(4).
              onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).
              category(SCMSTATS_CATEGORY).
              type(PropertyType.STRING).
              multiValues(true).
              build(),
            
            PropertyDefinition.builder(ScmStatsConstants.CHANGELOG_DATE_PATTERN).
              name("Change log date pattern").
              description("A date pattern to be used when parsing change log dates according Oracle's <a target=\"_blank\" "
              + "href=\"http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html\">Java Doc</a>. "
              + "The default date pattern is 'yyyy-MM-dd HH:mm:ss Z' for all supported SCMs and "
              + "'EEE MMM dd HH:mm:ss yyyy Z' for Mercurial.").
              index(5).
              onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).
              category(SCMSTATS_CATEGORY).
              type(PropertyType.STRING).
              build(),
            
            PropertyDefinition.builder(ScmStatsConstants.MERGE_AUTHORS_LIST).
              name("Merge author(s) list").
              description("A list of authors names to be merged when computing and displaying stats. Example:"
              + "author.name=Author.Name;authorName;Author.name;author.NAME").
              index(4).
              onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).
              category(SCMSTATS_CATEGORY).
              type(PropertyType.STRING).
              multiValues(true).
              build(),
            
            PropertyDefinition.builder(ScmStatsConstants.PERFORCE_CLIENTSPEC).
              name("Client Spec name").
              description("The Client Spec name which defines your workspace location, the depot files you plan to work with, "
              + "and where they will be located in your workspace when you invoke Perforce commands").
              index(0).
              onQualifiers(Qualifiers.PROJECT).
              category(SCMSTATS_CATEGORY).
              subCategory(PERFORCE_SUBCATEGORY).
              type(PropertyType.STRING).
              build(),

            UrlChecker.class,
            ScmUrlGuess.class,
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