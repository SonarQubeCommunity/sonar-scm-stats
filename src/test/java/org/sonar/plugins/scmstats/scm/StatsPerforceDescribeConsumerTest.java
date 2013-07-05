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
package org.sonar.plugins.scmstats.scm;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;
import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmException;
import org.junit.Test;

public class StatsPerforceDescribeConsumerTest {
  
  @Test
  public void shouldGetModifications() throws ScmException {
    
    String revisionLine = "Change 100 by user@client on 2013/06/26 05:51:53";
    String lineWithAddedClass = "... //repo/path/NewClass.java#1 add";
    String lineWithEditedClass = "... //repo/path/EditClass.java#1 edit";
    String lineWithDeletedClass = "... //repo/path/DeletedClass.java#1 move/delete";
    StatsPerforceDescribeConsumer consumer = new StatsPerforceDescribeConsumer("//repo/path", "yyyy/MM/dd:HH:mm:ss", null);
    consumer.setStatus(1);
    consumer.consumeLine(revisionLine);
    consumer.setStatus(6);
    consumer.consumeLine(lineWithAddedClass);
    consumer.consumeLine("");
    consumer.setStatus(6);
    consumer.consumeLine(lineWithEditedClass);
    consumer.consumeLine("");
    consumer.setStatus(6);
    consumer.consumeLine(lineWithDeletedClass);
    consumer.consumeLine("");
    
    List<ChangeSet> changes = consumer.getModifications();
    
    assertThat(changes).hasSize(3);
    
  }
}