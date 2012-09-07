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

import org.sonar.api.web.Dashboard;
import org.sonar.api.web.DashboardLayout;
import org.sonar.api.web.DashboardTemplate;

public final class ScmStatsDashboard extends DashboardTemplate {

  @Override
  public String getName() {
    return "SCM Stats";
  }

  @Override
  public Dashboard createDashboard() {
    final Dashboard dashboard = Dashboard.create();
    dashboard.setLayout(DashboardLayout.TWO_COLUMNS);
    createLeftColumn(dashboard);
    createRightColumn(dashboard);
    return dashboard;
  }

  private void createLeftColumn(final Dashboard dashboard) {
    dashboard.addWidget("scm-stats-commits-per-user", 1);
    dashboard.addWidget("scm-stats-commits-per-clockhour", 1);
  }

  private void createRightColumn(final Dashboard dashboard) {
    dashboard.addWidget("scm-stats-commits-per-weekday", 2);
    dashboard.addWidget("scm-stats-commits-per-month", 2);
  }

}