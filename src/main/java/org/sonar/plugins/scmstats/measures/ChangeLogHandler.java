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
import org.joda.time.DateTime;
import org.sonar.api.batch.SensorContext;
import org.sonar.plugins.scmstats.ScmStatsConstants;
import org.sonar.plugins.scmstats.model.ChangeLogInfo;
import org.sonar.plugins.scmstats.model.CommitsList;
import org.sonar.plugins.scmstats.utils.MapUtils;

public class ChangeLogHandler {

  private Map<String, CommitsList> commitsPerUser = new HashMap<String, CommitsList>();
  private Map<String, Integer> commitsPerClockHour = new HashMap<String, Integer>();
  private Map<String, Integer> commitsPerWeekDay = new HashMap<String, Integer>();
  private Map<String, Integer> commitsPerMonth = new HashMap<String, Integer>();
  private final List<ChangeLogInfo> changeLogs = new ArrayList<ChangeLogInfo>();
  private final List<String> ignoredAuthors;
  private final List<String> mergedAuthors;

  public ChangeLogHandler(List<String> ignoredAuthors, List<String> mergedAuthors) {
    this.ignoredAuthors = ignoredAuthors;
    this.mergedAuthors = mergedAuthors;
  }

  public final void addChangeLog(String authorName, Date commitDate, Map<String, Integer> fileStatus) {
    if (org.apache.commons.collections.MapUtils.getIntValue(fileStatus, ScmStatsConstants.ACTIVITY_ADD) > 0
            || org.apache.commons.collections.MapUtils.getIntValue(fileStatus, ScmStatsConstants.ACTIVITY_MODIFY) > 0
            || org.apache.commons.collections.MapUtils.getIntValue(fileStatus, ScmStatsConstants.ACTIVITY_DELETE) > 0) {

      changeLogs.add(new ChangeLogInfo(authorName, commitDate, fileStatus));
    }
  }

  public void generateMeasures() {
    for (ChangeLogInfo changeLogInfo : changeLogs) {

      if (!ignoredAuthors.contains(changeLogInfo.getAuthor())) {
        commitsPerUser = updateAuthorActivity(commitsPerUser, changeLogInfo);

        DateTime dt = new DateTime(changeLogInfo.getCommitDate());

        commitsPerClockHour = MapUtils.updateMap(commitsPerClockHour, String.format("%2d", dt.getHourOfDay()).replace(' ', '0'));
        commitsPerWeekDay = MapUtils.updateMap(commitsPerWeekDay, dt.dayOfWeek().getAsString());
        commitsPerMonth = MapUtils.updateMap(commitsPerMonth, String.format("%2d", dt.getMonthOfYear()).replace(' ', '0'));
      }
    }
  }

  public void saveMeasures(SensorContext context, String period) {
    PeriodMeasuresCreatorFactory factory = new PeriodMeasuresCreatorFactory();
    AbstractPeriodMeasuresCreator measuresCreator
            = factory.getPeriodMeasureCreator(context, period);

    measuresCreator.getCommitsPerMonthMeasure(commitsPerMonth).save();
    measuresCreator.getCommitsPerWeekDayMeasure(commitsPerWeekDay).save();
    measuresCreator.getCommitsPerClockHourMeasure(commitsPerClockHour).save();
    measuresCreator.getCommitsPerUserMeasure(commitsPerUser).save();

  }

  @VisibleForTesting
  protected Map<String, CommitsList> updateAuthorActivity(
          final Map<String, CommitsList> map,
          final ChangeLogInfo changeLogInfo) {

    final String author = getBasicAuthor(changeLogInfo.getAuthor());

    final Map<String, CommitsList> authorActivity = new HashMap<String, CommitsList>();
    authorActivity.putAll(map);

    final Map<String, Integer> activity = changeLogInfo.getActivity();
    List<Integer> stats = authorActivity.get(author) == null ? getInitialActivity(activity) : authorActivity.get(author).getCommits();

    if (authorActivity.containsKey(author)) {
      final Integer commits = stats.get(0) + 1;
      stats.set(0, commits);
      updateActivity(stats, activity, 1, ScmStatsConstants.ACTIVITY_ADD);
      updateActivity(stats, activity, 2, ScmStatsConstants.ACTIVITY_MODIFY);
      updateActivity(stats, activity, 3, ScmStatsConstants.ACTIVITY_DELETE);
    }
    authorActivity.put(author, new CommitsList(stats));
    return authorActivity;
  }

  private void updateActivity(final List<Integer> stats,
          final Map<String, Integer> activityType,
          final int pos,
          final String action) {
    final Integer actions = stats.get(pos)
            + org.apache.commons.collections.MapUtils.getInteger(activityType, action, 0);
    stats.set(pos, actions);
  }

  private List<Integer> getInitialActivity(final Map<String, Integer> activity) {
    return Arrays.asList(1,
            org.apache.commons.collections.MapUtils.getInteger(activity, ScmStatsConstants.ACTIVITY_ADD, 0),
            org.apache.commons.collections.MapUtils.getInteger(activity, ScmStatsConstants.ACTIVITY_MODIFY, 0),
            org.apache.commons.collections.MapUtils.getInteger(activity, ScmStatsConstants.ACTIVITY_DELETE, 0));
  }

  @VisibleForTesting
  protected String getBasicAuthor(String author) {
    if (mergedAuthors != null && !mergedAuthors.isEmpty()) {
      for (String mergeList : mergedAuthors) {
        String[] mergeConfiguration = mergeList.split("=");
        if (mergeConfiguration.length == 2) {
          String basicAuthor = mergeConfiguration[0];
          if (author.equals(basicAuthor)) {
            return basicAuthor;
          }
          String secondaryAuthorsList = mergeConfiguration[1];
          List<String> secondaryAuthors = Arrays.asList(secondaryAuthorsList.split(";"));
          if (secondaryAuthors.contains(author)) {
            return basicAuthor;
          }
        }
      }
    }
    return author;
  }

  public Map<String, Integer> getCommitsPerClockHour() {
    return commitsPerClockHour;
  }

  public Map<String, Integer> getCommitsPerMonth() {
    return commitsPerMonth;
  }

  public Map<String, CommitsList> getCommitsPerUser() {
    return commitsPerUser;
  }

  public Map<String, Integer> getCommitsPerWeekDay() {
    return commitsPerWeekDay;
  }

  public List<ChangeLogInfo> getChangeLogs() {
    return changeLogs;
  }
}
