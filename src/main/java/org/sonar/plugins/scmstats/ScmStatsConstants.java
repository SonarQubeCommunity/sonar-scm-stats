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

public final class ScmStatsConstants {

  private ScmStatsConstants() {
  }
  
  // Properties provided by this plugin
  public static final String ENABLED = "sonar.scm-stats.enabled";

  // Properties provided by the SCM Activity plugin
  public static final String URL = "sonar.scm.url";
  public static final String USER = "sonar.scm.user.secured";
  public static final String PASSWORD = "sonar.scm.password.secured";
  
}
