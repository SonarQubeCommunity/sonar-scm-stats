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
import org.joda.time.DateTime;
import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.sonar.plugins.scmstats.ScmStatsConstants;
import org.sonar.plugins.scmstats.model.ChangeLogInfo;
import org.sonar.plugins.scmstats.model.CommitsList;

public class ChangeLogHandlerTest {
  
  private final ChangeLogHandler instance = new ChangeLogHandler(new ArrayList<String>(),new ArrayList<String>());
  
  @Test
  public void testAddChangeLog() {
    
    final DateTime dt = new DateTime(2012,10,1,14,0);
    final Map<String,Integer> activity = new HashMap<String, Integer>();
    activity.put("Adding", 2);
    activity.put("Deleting", 1);
    instance.addChangeLog("author", dt.toDate(), activity);
    instance.addChangeLog("author", dt.toDate(), activity);
    instance.generateMeasures();
    
    assertThat (instance.getCommitsPerUser().get("author").getCommits()).hasSize(4);
    assertThat (instance.getCommitsPerUser().get("author").getCommits().get(0)).isEqualTo(2);
    assertThat (instance.getCommitsPerUser().get("author").getCommits().get(1)).isEqualTo(4);
    assertThat (instance.getCommitsPerUser().get("author").getCommits().get(2)).isEqualTo(0);
    assertThat (instance.getCommitsPerUser().get("author").getCommits().get(3)).isEqualTo(2);
    assertThat (instance.getCommitsPerClockHour().get("14")).isEqualTo(2);
    assertThat (instance.getCommitsPerMonth().get("10")).isEqualTo(2);
    assertThat (instance.getCommitsPerWeekDay().get("1")).isEqualTo(2);
    
  }
  
  @Test
  public void shouldUpdateAuthorActivity() {
    Map<String, CommitsList> authorsActivity = new HashMap<String, CommitsList>();
    authorsActivity.put("author", new CommitsList(Arrays.asList(1,1,2,0)));
    Map<String, Integer> activity = new HashMap<String, Integer>();
    activity.put(ScmStatsConstants.ACTIVITY_ADD, 1);
    activity.put(ScmStatsConstants.ACTIVITY_MODIFY, 2);
    ChangeLogInfo changeLogInfo = new ChangeLogInfo("author", null, activity);
    authorsActivity = instance.updateAuthorActivity(authorsActivity, changeLogInfo);
    
    assertThat(authorsActivity.get("author").getCommits().get(0)).isEqualTo(2);
    assertThat(authorsActivity.get("author").getCommits().get(1)).isEqualTo(2);
    assertThat(authorsActivity.get("author").getCommits().get(2)).isEqualTo(4);
    assertThat(authorsActivity.get("author").getCommits().get(3)).isEqualTo(0);
  }

  @Test
  public void shouldNotAddChangeLogWhenNoFilesAreAffected() {
    Map<String, CommitsList> authorsActivity = new HashMap<String, CommitsList>();
    Map<String, Integer> activity = new HashMap<String, Integer>();
    activity.put(ScmStatsConstants.ACTIVITY_ADD, 0);
    activity.put(ScmStatsConstants.ACTIVITY_MODIFY, 0);
    activity.put(ScmStatsConstants.ACTIVITY_DELETE, 0);
    instance.addChangeLog("author", null, activity);
    
    assertThat(authorsActivity).hasSize(0);
  }

  @Test
  public void shouldGetMergedAuthor() {
    List<String> mergedAuthors = new ArrayList<String>();
    mergedAuthors.add("author1=author;author11;author111");
    mergedAuthors.add("author2=author22");
    ChangeLogHandler changeLogHandler = new ChangeLogHandler(new ArrayList<String>(),mergedAuthors);
    
    assertThat(changeLogHandler.getBasicAuthor("author")).isEqualTo("author1");
    assertThat(changeLogHandler.getBasicAuthor("author1")).isEqualTo("author1");
    assertThat(changeLogHandler.getBasicAuthor("author11")).isEqualTo("author1");
    assertThat(changeLogHandler.getBasicAuthor("author111")).isEqualTo("author1");
    assertThat(changeLogHandler.getBasicAuthor("author2")).isEqualTo("author2");
    assertThat(changeLogHandler.getBasicAuthor("author22")).isEqualTo("author2");
  }

  @Test
  public void shouldGetMergedAuthorWithNoConfiguration() {
    ChangeLogHandler changeLogHandler = new ChangeLogHandler(new ArrayList<String>(),
            new ArrayList<String>());
    
    assertThat(changeLogHandler.getBasicAuthor("author")).isEqualTo("author");
  }

  @Test
  public void shouldGetMergedAuthorWithWrongConfiguration() {
    List<String> mergedAuthors = new ArrayList<String>();
    mergedAuthors.add("author1:author;author11;author111");
    ChangeLogHandler changeLogHandler = new ChangeLogHandler(new ArrayList<String>(),mergedAuthors);
    
    assertThat(changeLogHandler.getBasicAuthor("author")).isEqualTo("author");
  }

}
