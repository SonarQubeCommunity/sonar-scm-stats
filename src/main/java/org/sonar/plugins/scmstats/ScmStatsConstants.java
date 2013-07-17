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

public final class ScmStatsConstants {

  private ScmStatsConstants() {
  }
  // Properties provided by this plugin
  public static final String ENABLED = "sonar.scm-stats.enabled";
  public static final String PERIOD_1 = "sonar.scm-stats.period1";
  public static final String PERIOD_2 = "sonar.scm-stats.period2";
  public static final String PERIOD_3 = "sonar.scm-stats.period3";
  public static final String IGNORE_AUTHORS_LIST = "sonar.scm-stats.authors.ignore";
  public static final String PERFORCE_CLIENTSPEC = "sonar.scm-stats.perforce.clientspec";
  // Properties provided by the SCM Activity plugin
  public static final String URL = "sonar.scm.url";
  public static final String USER = "sonar.scm.user.secured";
  public static final String PASSWORD = "sonar.scm.password.secured";
  // Constants used in the plugin
  public static final String ACTIVITY_ADD = "Adding";
  public static final String ACTIVITY_MODIFY = "Modifying";
  public static final String ACTIVITY_DELETE = "Deleting";

  public static List<String> getAsList() {
    return ImmutableList.of(ENABLED, PERIOD_1, PERIOD_2, PERIOD_3, URL, USER, PASSWORD,
            PERFORCE_CLIENTSPEC, ACTIVITY_ADD, ACTIVITY_MODIFY, ACTIVITY_DELETE);
  }

  public static List<String> getPeriodsAsList() {
    return ImmutableList.of(PERIOD_1, PERIOD_2, PERIOD_3);
  }
}
