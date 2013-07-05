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

import org.apache.maven.scm.AbstractScmVersion;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.provider.perforce.repository.PerforceScmProviderRepository;
import org.codehaus.plexus.util.cli.Commandline;
import org.junit.Test;
import org.joda.time.LocalDate;

public class StatsPerforceChangeLogCommandTest {

  private final String HOST = "p4gp";
  private final int PORT = 8888;
  private final String PATH = "//path";
  private final String USER = "user";
  private final String PWD = "password";
  private final String CLIENTSPEC = "myClientSpec";
  private final PerforceScmProviderRepository repo =
          new PerforceScmProviderRepository(HOST, PORT, PATH, USER, PWD);

  @Test
  public void shouldCreateSimpleCommandLine() {
    Commandline result = StatsPerforceChangeLogCommand.createCommandLine(repo, null, CLIENTSPEC, null, null, null, null, null);
    assertThat(result.toString()).contains("p4 -p p4gp:8888 -u user -P password -c myClientSpec changes -t //path...");
  }

  @Test
  public void shouldCreateCommandLineWithDateRange() {
    LocalDate fromDate = new LocalDate(2013, 5, 20);
    LocalDate toDate = new LocalDate(2013, 6, 30);
    Commandline result = StatsPerforceChangeLogCommand.createCommandLine(repo, null, CLIENTSPEC, null, fromDate.toDate(), toDate.toDate(), null, null);
    assertThat(result.toString()).contains("p4 -p p4gp:8888 -u user -P password -c myClientSpec changes -t //path...@2013/05/20:00:00:00,2013/06/30:00:00:00");
  }

  @Test
  public void shouldCreateCommandLineFromDateUntilNow() {
    LocalDate fromDate = new LocalDate(2013, 5, 20);
    Commandline result = StatsPerforceChangeLogCommand.createCommandLine(repo, null, CLIENTSPEC, null, fromDate.toDate(), null, null, null);
    assertThat(result.toString()).contains("p4 -p p4gp:8888 -u user -P password -c myClientSpec changes -t //path...@2013/05/20:00:00:00,now");
  }

  @Test
  public void shouldCreateCommandLineWithVersionRange() {
    ScmVersion fromVersion = new AbstractScmVersion("fromVersion") {
      @Override
      public String getType() {
        throw new UnsupportedOperationException("Not supported yet."); 
      }
    };
    ScmVersion toVersion = new AbstractScmVersion("toVersion") {
      @Override
      public String getType() {
        throw new UnsupportedOperationException("Not supported yet."); 
      }
    };
    Commandline result = StatsPerforceChangeLogCommand.createCommandLine(repo, null, CLIENTSPEC, null, null, null, fromVersion, toVersion);
    assertThat(result.toString()).contains("p4 -p p4gp:8888 -u user -P password -c myClientSpec changes -t //path...@fromVersion,toVersion");
  }

  @Test
  public void shouldCreateCommandLineFromVersionUntilNow() {
    ScmVersion fromVersion = new AbstractScmVersion("fromVersion") {
      @Override
      public String getType() {
        throw new UnsupportedOperationException("Not supported yet."); 
      }
    };
    Commandline result = StatsPerforceChangeLogCommand.createCommandLine(repo, null, CLIENTSPEC, null, null, null, fromVersion, null);
    assertThat(result.toString()).contains("p4 -p p4gp:8888 -u user -P password -c myClientSpec changes -t //path...@fromVersion,now");
  }
}