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
import java.util.List;
import java.util.Map;
import static org.hamcrest.Matchers.*;
import org.joda.time.DateTime;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.sonar.plugins.scmstats.ScmStatsConstants;
import org.sonar.plugins.scmstats.model.ChangeLogInfo;

public class ChangeLogHandlerTest {
  ChangeLogHandler instance = new ChangeLogHandler();
  
  @Test
  public void testAddChangeLog() {
    
    final DateTime dt = new DateTime(2012,10,1,14,0);
    final Map<String,Integer> activity = new HashMap<String, Integer>();
    activity.put("Adding", 2);
    activity.put("Deleting", 1);
    instance.addChangeLog("author", dt.toDate(), activity);
    instance.addChangeLog("author", dt.toDate(), activity);
    instance.generateMeasures();
    
    assertThat (instance.getCommitsPerUser().get("author").size(), is(4));
    assertThat (instance.getCommitsPerUser().get("author").get(0), is(2));
    assertThat (instance.getCommitsPerUser().get("author").get(1), is(4));
    assertThat (instance.getCommitsPerUser().get("author").get(2), is(0));
    assertThat (instance.getCommitsPerUser().get("author").get(3), is(2));
    assertThat (instance.getCommitsPerClockHour().get("14"), is(2));
    assertThat (instance.getCommitsPerMonth().get("10"), is(2));
    assertThat (instance.getCommitsPerWeekDay().get("1"), is(2));
    
  }
  @Test
  public void shouldUpdateAuthorActivity() {
    Map<String, List<Integer>> authorsActivity = new HashMap<String, List<Integer>>();
    authorsActivity.put("author", Arrays.asList(1,1,2,0));
    Map<String, Integer> activity = new HashMap<String, Integer>();
    activity.put(ScmStatsConstants.ACTIVITY_ADD, 1);
    activity.put(ScmStatsConstants.ACTIVITY_MODIFY, 2);
    ChangeLogInfo changeLogInfo = new ChangeLogInfo("author", null, activity);
    authorsActivity = instance.updateAuthorActivity(authorsActivity, changeLogInfo);
    
    assertThat(authorsActivity.get("author").get(0),is(2));
    assertThat(authorsActivity.get("author").get(1),is(2));
    assertThat(authorsActivity.get("author").get(2),is(4));
    assertThat(authorsActivity.get("author").get(3),is(0));
  }
}
