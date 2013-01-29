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

import com.google.common.annotations.VisibleForTesting;
import java.util.*;
import org.apache.commons.lang.ObjectUtils;
import org.joda.time.DateTime;
import org.sonar.api.batch.SensorContext;
import org.sonar.plugins.scmstats.ScmStatsConstants;
import org.sonar.plugins.scmstats.model.ChangeLogInfo;
import org.sonar.plugins.scmstats.utils.MapUtils;

public class ChangeLogHandler {

  private Map<String, List<Integer>> commitsPerUser = new HashMap<String, List<Integer>>();
  private Map<String, Integer> commitsPerClockHour = new HashMap<String, Integer>();
  private Map<String, Integer> commitsPerWeekDay = new HashMap<String, Integer>();
  private Map<String, Integer> commitsPerMonth = new HashMap<String, Integer>();
  private final List<ChangeLogInfo> changeLogs = new ArrayList<ChangeLogInfo>();

  public final void addChangeLog(String authorName, Date commitDate, Map<String, Integer> fileStatus) {
    changeLogs.add(new ChangeLogInfo(authorName, commitDate, fileStatus));
  }

  public void generateMeasures() {
    for (ChangeLogInfo changeLogInfo : changeLogs) {
      commitsPerUser = updateAuthorActivity(commitsPerUser, changeLogInfo);

      DateTime dt = new DateTime(changeLogInfo.getCommitDate());

      commitsPerClockHour = MapUtils.updateMap(commitsPerClockHour, String.format("%2d", dt.getHourOfDay()).replace(' ', '0'));
      commitsPerWeekDay = MapUtils.updateMap(commitsPerWeekDay, dt.dayOfWeek().getAsString());
      commitsPerMonth = MapUtils.updateMap(commitsPerMonth, String.format("%2d", dt.getMonthOfYear()).replace(' ', '0'));
    }
  }

  public void saveMeasures(SensorContext context) {
    PeriodMeasuresCreatorFactory factory = new PeriodMeasuresCreatorFactory();
    AbstractPeriodMeasuresCreator measuresCreator = 
            factory.getPeriodMeasureCreator(context,ScmStatsConstants.PERIOD_1);
    
    measuresCreator.getCommitsPerMonthMeasure(commitsPerMonth).save();
    measuresCreator.getCommitsPerWeekDayMeasure(commitsPerWeekDay).save();
    measuresCreator.getCommitsPerClockHourMeasure(commitsPerClockHour).save();
    measuresCreator.getCommitsPerUserMeasure(commitsPerUser).save();

  }
  @VisibleForTesting
  Map<String, List<Integer>> updateAuthorActivity(
          final Map<String, List<Integer>> map, 
          final ChangeLogInfo changeLogInfo) {
    
    final String author = changeLogInfo.getAuthor();
    final Map<String, List<Integer>> authorActivity = new HashMap<String, List<Integer>>();
    authorActivity.putAll(map);
    
    final Map<String, Integer> activity = changeLogInfo.getActivity();
    final List<Integer> stats = (List<Integer>) 
            ObjectUtils.defaultIfNull(authorActivity.get(author),
                                      getInitialActivity(activity));
    if (authorActivity.containsKey(author)) {
      final Integer commits = stats.get(0) + 1;
      stats.set(0, commits);
      updateActivity( stats, activity, 1, ScmStatsConstants.ACTIVITY_ADD);
      updateActivity( stats, activity, 2, ScmStatsConstants.ACTIVITY_MODIFY);
      updateActivity( stats, activity, 3, ScmStatsConstants.ACTIVITY_DELETE);
    }    
    authorActivity.put(author, stats);
    return authorActivity;
  }

  private void updateActivity(final List<Integer> stats, 
          final Map<String, Integer> activityType, 
          final int pos,
          final String action) {
    final Integer actions = stats.get(pos) + 
            org.apache.commons.collections.MapUtils.getInteger(activityType, action, 0);
    stats.set(pos, actions);
  }

  private List<Integer> getInitialActivity(final Map<String,Integer> activity){
          return Arrays.asList(1,
            org.apache.commons.collections.MapUtils.getInteger(activity, ScmStatsConstants.ACTIVITY_ADD, 0),
            org.apache.commons.collections.MapUtils.getInteger(activity, ScmStatsConstants.ACTIVITY_MODIFY, 0),
            org.apache.commons.collections.MapUtils.getInteger(activity, ScmStatsConstants.ACTIVITY_DELETE, 0));
                  }
  public Map<String, Integer> getCommitsPerClockHour() {
    return commitsPerClockHour;
  }

  public Map<String, Integer> getCommitsPerMonth() {
    return commitsPerMonth;
  }

  public Map<String, List<Integer>> getCommitsPerUser() {
    return commitsPerUser;
  }

  public Map<String, Integer> getCommitsPerWeekDay() {
    return commitsPerWeekDay;
  }
  
  public List<ChangeLogInfo> getChangeLogs(){
    return changeLogs;
  }
  
}