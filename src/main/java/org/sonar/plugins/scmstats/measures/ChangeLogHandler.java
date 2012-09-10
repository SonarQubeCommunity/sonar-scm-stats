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

import java.util.*;
import org.joda.time.DateTime;
import org.sonar.api.batch.SensorContext;
import org.sonar.plugins.scmstats.model.ChangeLogInfo;

public class ChangeLogHandler {

  private final Map<String, Integer> commitsPerUser = new HashMap<String, Integer>();
  private final Map<String, Integer> commitsPerClockHour = new HashMap<String, Integer>();
  private final Map<String, Integer> commitsPerWeekDay = new HashMap<String, Integer>();
  private final Map<String, Integer> commitsPerMonth = new HashMap<String, Integer>();
  private final List<ChangeLogInfo> changeLogs = new ArrayList<ChangeLogInfo>();

  public final void addChangeLog(String authorName, Date commitDate, String revision) {
    changeLogs.add(new ChangeLogInfo(authorName, commitDate, revision));
  }

  public void generateMeasures() {
    for (ChangeLogInfo changeLogInfo : changeLogs) {
      updateMap(commitsPerUser, changeLogInfo.getAuthor());

      DateTime dt = new DateTime(changeLogInfo.getCommitDate());

      updateMap(commitsPerClockHour, String.format("%2d", dt.getHourOfDay()).replace(' ', '0'));
      updateMap(commitsPerWeekDay, dt.dayOfWeek().getAsString());
      updateMap(commitsPerMonth, String.format("%2d", dt.getMonthOfYear()).replace(' ', '0'));
    }


  }
  
  public void saveMeasures(SensorContext context){
    new CommitsPerMonthMeasure(commitsPerMonth, context).save();
    new CommitsPerWeekDayMeasure(commitsPerWeekDay, context).save();
    new CommitsPerClockHourMeasure(commitsPerClockHour, context).save();
    new CommitsPerUserMeasure(commitsPerUser, context).save();
    
  }

  private void updateMap(final Map<String, Integer> map, final String key) {
    if (map.containsKey(key)) {
      map.put(key, map.get(key) + 1);
    } else {
      map.put(key, 1);
    }
  }

  public Map<String, Integer> getCommitsPerClockHour() {
    return commitsPerClockHour;
  }

  public Map<String, Integer> getCommitsPerMonth() {
    return commitsPerMonth;
  }

  public Map<String, Integer> getCommitsPerUser() {
    return commitsPerUser;
  }

  public Map<String, Integer> getCommitsPerWeekDay() {
    return commitsPerWeekDay;
  }
  
  
}