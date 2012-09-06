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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.plugins.scmstats.ScmStatsMetrics;

public class ChangeLogInfoHolder {
  private final Map<String, Integer> commitsPerUser = new HashMap<String, Integer>();
  private final Map<String, Integer> commitsPerClockHour = new HashMap<String, Integer>();
  private final List<ChangeLogInfo> changeLogs = new ArrayList<ChangeLogInfo>();

  
  public ChangeLogInfoHolder(){
    initClockHourMap();
  }
  
  public final void addChangeLog(String authorName, String clockHour, String revision) {
    changeLogs.add(new ChangeLogInfo(authorName, clockHour, revision));
  }

  public void generateMeasures(SensorContext context) {

    for (ChangeLogInfo changeLogInfo : changeLogs) {
      updateMap(commitsPerUser, changeLogInfo.getAuthor());
      updateMap(commitsPerClockHour, changeLogInfo.getClockHour());
    }

    final PropertiesBuilder<String, Integer> commitsPerUserMeasure =
            propertiesBuilder(ScmStatsMetrics.SCM_COMMITS_PER_USER);
    final PropertiesBuilder<String, Integer> commitsPerClockHourMeasure =
            propertiesBuilder(ScmStatsMetrics.SCM_COMMITS_PER_CLOCKTIME);
    
    commitsPerUserMeasure.addAll(commitsPerUser);
    commitsPerClockHourMeasure.addAll(commitsPerClockHour);
    
    context.saveMeasure(commitsPerUserMeasure.build());
    context.saveMeasure(commitsPerClockHourMeasure.build());

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

  public Map<String, Integer> getCommitsPerClockHour() {
    return commitsPerClockHour;
  }
  
  
}