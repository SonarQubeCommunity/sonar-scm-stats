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

import org.apache.maven.scm.CommandParameters;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;

/**
 * Overriding the default perforce provider in order to use the
 * StatsPerforceChangeLogCommand to retrieve the changelog information and the
 * StatsPerforceDescribeConsumer to get action information on changed files.
 *
 * @since 0.3
 */
public class StatsPerforceScmProvider extends PerforceScmProvider {

  @Override
  protected ChangeLogScmResult changelog(ScmProviderRepository repository, ScmFileSet fileSet,
          CommandParameters parameters)
          throws ScmException {
    StatsPerforceChangeLogCommand command = new StatsPerforceChangeLogCommand();
    command.setLogger(getLogger());
    return (ChangeLogScmResult) command.execute(repository, fileSet, parameters);
  }
}
