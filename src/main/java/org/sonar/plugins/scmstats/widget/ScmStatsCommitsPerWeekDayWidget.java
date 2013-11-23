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

package org.sonar.plugins.scmstats.widget;

import org.sonar.api.web.*;

@UserRole(UserRole.USER)
@WidgetCategory("SCM")
@Description("SCM Stats Commits per Week Day")
@WidgetProperties(
{
  @WidgetProperty(key = "Period", type = WidgetPropertyType.INTEGER, defaultValue = "1",optional=false)
})

public final class ScmStatsCommitsPerWeekDayWidget extends AbstractRubyTemplate implements RubyRailsWidget {

  public String getId() {
    return "scm-stats-commits-per-weekday";
  }

  public String getTitle() {
    return "SCM Stats Commits per Week Day";
  }

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/scmstats/commits_per_weekday_widget.html.erb";
  }

}
