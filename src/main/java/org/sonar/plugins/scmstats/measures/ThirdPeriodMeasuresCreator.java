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
package org.sonar.plugins.scmstats.measures;

import java.util.List;
import java.util.Map;
import org.sonar.api.batch.SensorContext;
import org.sonar.plugins.scmstats.ScmStatsConstants;

public class ThirdPeriodMeasuresCreator extends AbstractPeriodMeasuresCreator{

  public ThirdPeriodMeasuresCreator(SensorContext context) {
    super(context);
  }

  @Override
  public String getPeriod() {
    return ScmStatsConstants.PERIOD_3;
  }

  @Override
  public CommitsPerUserMeasure getCommitsPerUserMeasure(Map<String, List<Integer>> map) {
    return new CommitsPerUserMeasure(ScmStatsMetrics.SCM_COMMITS_PER_USER_3, map, getContext());
  }

  @Override
  public AbstractScmStatsMeasure getCommitsPerWeekDayMeasure(Map<String, Integer> map) {
    return new CommitsPerWeekDayMeasure(ScmStatsMetrics.SCM_COMMITS_PER_WEEKDAY_3, map, getContext());
  }

  @Override
  public AbstractScmStatsMeasure getCommitsPerMonthMeasure(Map<String, Integer> map) {
    return new CommitsPerMonthMeasure(ScmStatsMetrics.SCM_COMMITS_PER_MONTH_3, map, getContext());
  }

  @Override
  public AbstractScmStatsMeasure getCommitsPerClockHourMeasure(Map<String, Integer> map) {
    return new CommitsPerClockHourMeasure(ScmStatsMetrics.SCM_COMMITS_PER_CLOCKTIME_3, map, getContext());
  }
}
