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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.sonar.plugins.scmstats.ScmStatsConstants;
import org.sonar.plugins.scmstats.model.CommitsList;

public class FirstPeriodMeasuresCreatorTest {

  private final FirstPeriodMeasuresCreator creator = new FirstPeriodMeasuresCreator(null);
  
  @Test
  public void shouldGetPeriod(){
    assertThat ( creator.getPeriod(), is(ScmStatsConstants.PERIOD_1));
  }
  
  @Test
  public void shouldGetCommitsPerUserMeasure(){
    final Map<String,CommitsList> map = new HashMap<String, CommitsList>();
    final CommitsList list1 = new CommitsList(Arrays.asList(1,2,3));
    final CommitsList list2 = new CommitsList(Arrays.asList(4,5,6));
    map.put("key1", list1);
    map.put("key2", list2);
    
    final CommitsPerUserMeasure measure = creator.getCommitsPerUserMeasure(map);
    assertThat ( measure, instanceOf(CommitsPerUserMeasure.class));
    assertThat ( measure.getDataMap().size(), is(2));
    assertThat ( measure.getDataMap(), hasEntry("key1", list1));
    assertThat ( measure.getDataMap(), hasEntry("key2", list2));
  }

  @Test
  public void shouldGetCommitsPerClockHourMeasure(){
    
    AbstractScmStatsMeasure measure = creator.getCommitsPerClockHourMeasure(initMap());
    assertMeasure(measure, CommitsPerClockHourMeasure.class,26);
  }

  @Test
  public void shouldGetCommitsPerMOnthMeasure(){
    
    AbstractScmStatsMeasure measure = creator.getCommitsPerMonthMeasure(initMap());
    assertMeasure(measure, CommitsPerMonthMeasure.class,14);
  }
  @Test
  public void shouldGetCommitsPerWeekDayMeasure(){
    
    AbstractScmStatsMeasure measure = creator.getCommitsPerWeekDayMeasure(initMap());
    assertMeasure(measure, CommitsPerWeekDayMeasure.class,9);
  }
  
  private Map<String, Integer> initMap() {
    Map<String,Integer> map = new HashMap<String, Integer>();
    map.put("key1", 1);
    map.put("key2", 2);
    return map;
  }
  private void assertMeasure(AbstractScmStatsMeasure measure, Class instance, int mapSize) {
    assertThat ( measure, instanceOf(instance));
    assertThat ( measure.getDataMap().size(), is(mapSize));
    assertThat ( measure.getDataMap(), hasEntry("key1", 1));
    assertThat ( measure.getDataMap(), hasEntry("key2", 2));
    
  }
  
}
