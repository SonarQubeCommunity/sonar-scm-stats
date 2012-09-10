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

import static org.hamcrest.Matchers.is;
import org.joda.time.DateTime;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class ChangeLogHandlerTest {
  ChangeLogHandler instance = new ChangeLogHandler();
  
  @Test
  public void testAddChangeLog() {
    
    final DateTime dt = new DateTime(2012,10,1,14,0);
    
    instance.addChangeLog("author", dt.toDate(), "1");
    instance.addChangeLog("author", dt.toDate(), "2");
    instance.generateMeasures();
    
    assertThat (instance.getCommitsPerClockHour().get("14"), is(2));
    assertThat (instance.getCommitsPerUser().get("author"), is(2));
    assertThat (instance.getCommitsPerMonth().get("10"), is(2));
    assertThat (instance.getCommitsPerWeekDay().get("1"), is(2));
    
  }

}
