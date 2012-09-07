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
package org.sonar.plugins.scmstats.model;

import java.util.*;
import org.joda.time.DateTime;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.plugins.scmstats.ScmStatsMetrics;

public class ChangeLogInfoHolder {
  private final Map<String, Integer> commitsPerUser = new HashMap<String, Integer>();
  private final Map<String, Integer> commitsPerClockHour = new HashMap<String, Integer>();
  private final Map<String, Integer> commitsPerWeekDay = new HashMap<String, Integer>();
  private final Map<String, Integer> commitsPerMonth = new HashMap<String, Integer>();
  private final List<ChangeLogInfo> changeLogs = new ArrayList<ChangeLogInfo>();

  
  public ChangeLogInfoHolder(){
    initClockHourMap();
    initMonthMap();
    initDayOfWeekMap();
  }
  
  public final void addChangeLog(String authorName, Date commitDate, String revision) {
    changeLogs.add(new ChangeLogInfo(authorName, commitDate, revision));
  }

  public void generateMeasures(SensorContext context) {

    for (ChangeLogInfo changeLogInfo : changeLogs) {
      updateMap(commitsPerUser, changeLogInfo.getAuthor());
      
      DateTime dt = new DateTime(changeLogInfo.getCommitDate());
      
      updateMap(commitsPerClockHour,String.format("%2d", dt.getHourOfDay()).replace(' ', '0') );
      updateMap(commitsPerWeekDay, dt.dayOfWeek().getAsString());
      updateMap(commitsPerMonth,String.format("%2d", dt.getMonthOfYear()).replace(' ', '0') );
    }

    final PropertiesBuilder<String, Integer> commitsPerUserMeasure =
            propertiesBuilder(ScmStatsMetrics.SCM_COMMITS_PER_USER);
    final PropertiesBuilder<String, Integer> commitsPerClockHourMeasure =
            propertiesBuilder(ScmStatsMetrics.SCM_COMMITS_PER_CLOCKTIME);
    final PropertiesBuilder<String, Integer> commitsPerWeekDayMeasure =
            propertiesBuilder(ScmStatsMetrics.SCM_COMMITS_PER_WEEKDAY);
    final PropertiesBuilder<String, Integer> commitsPerMonthMeasure =
            propertiesBuilder(ScmStatsMetrics.SCM_COMMITS_PER_MONTH);
    
    commitsPerUserMeasure.addAll(commitsPerUser);
    commitsPerClockHourMeasure.addAll(commitsPerClockHour);
    commitsPerWeekDayMeasure.addAll(commitsPerWeekDay);
    commitsPerMonthMeasure.addAll(commitsPerMonth);
    
    context.saveMeasure(commitsPerUserMeasure.build());
    context.saveMeasure(commitsPerClockHourMeasure.build());
    context.saveMeasure(commitsPerWeekDayMeasure.build());
    context.saveMeasure(commitsPerMonthMeasure.build());

  }

  private void updateMap(final Map<String, Integer> map, final String key) {
    if (map.containsKey(key)) {
      map.put(key, map.get(key) + 1);
    } else {
      map.put(key, 1);
    }
  }

  private static PropertiesBuilder<String, Integer> propertiesBuilder(Metric metric) {
    return new PropertiesBuilder<String, Integer>(metric);
  }
  
  private void initClockHourMap() {
    for (int i = 0; i < 24; i++) {
      commitsPerClockHour.put(String.format("%2d", i).replace(' ', '0'), 0);
    }
  }

  private void initMonthMap() {
    for (int i = 1; i <= 12; i++) {
      DateTime dt = new DateTime (2012,i,1,0,0);
      commitsPerMonth.put(String.format("%2d", dt.getMonthOfYear()).replace(' ', '0'), 0);
    }
  }
  private void initDayOfWeekMap() {
    for (int i = 1; i <= 7; i++) {
      DateTime dt = new DateTime (2012,1,i,0,0);
      commitsPerWeekDay.put(dt.dayOfWeek().getAsString(), 0);
    }
  }

  public Map<String, Integer> getCommitsPerClockHour() {
    return commitsPerClockHour;
  }
  
  
}