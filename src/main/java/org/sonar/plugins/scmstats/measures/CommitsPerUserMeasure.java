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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.plugins.scmstats.model.CommitsList;

public class CommitsPerUserMeasure {

  private Measure measure;
  private final Map<String, CommitsList> dataMap = new HashMap<String, CommitsList>();
  private final SensorContext context;

  public CommitsPerUserMeasure(
          final Metric metric,
          final Map<String, CommitsList> map,
          final SensorContext context) {
    this.context = context;
    dataMap.putAll(map);
    final SortedSet<Entry<String,CommitsList>> sortedSet = entriesSortedByValues(map);
    final PropertiesBuilder<String, List<Integer>> pBuilder = new
            PropertiesBuilder<String, List<Integer>>(metric);
    int counter = 1;
    for (Entry<String,CommitsList> entry : sortedSet){

      pBuilder.add(String.format ("%03d" , counter) + "." + entry.getKey(), entry.getValue().getCommits());
      counter++;
    }
    measure = pBuilder.build();
  }

  public void save() {
    context.saveMeasure(measure);
  }

  public Map<String, CommitsList> getDataMap() {
    return dataMap;
  }

  @VisibleForTesting
  protected final <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(final Map<K,V> map) {
    final SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
      new Comparator<Map.Entry<K,V>>() {
        @Override
        public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
          int res = e1.getValue().compareTo(e2.getValue());
          return res != 0 ? res : 1;
        }
        @Override
        public boolean equals(Object obj) {
          return super.equals(obj);
        }
      }
    );
    sortedEntries.addAll(map.entrySet());
    return sortedEntries;
  }
}