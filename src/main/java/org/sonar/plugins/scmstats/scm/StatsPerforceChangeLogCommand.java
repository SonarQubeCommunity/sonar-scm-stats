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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.maven.scm.*;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.perforce.PerforceScmProvider;
import org.apache.maven.scm.provider.perforce.command.changelog.PerforceChangeLogCommand;
import static org.apache.maven.scm.provider.perforce.command.changelog.PerforceChangeLogCommand.createCommandLine;
import org.apache.maven.scm.provider.perforce.command.changelog.PerforceChangesConsumer;
import org.apache.maven.scm.provider.perforce.repository.PerforceScmProviderRepository;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

/**
 * Overriding the default perforce changelog command in order to improve logging
 * and command line execution.
 *
 * @since 0.3
 */
public class StatsPerforceChangeLogCommand extends PerforceChangeLogCommand {

  private static final int SUCCESS_EXIT_CODE = 0;

  @Override
  protected ChangeLogScmResult executeChangeLogCommand(ScmProviderRepository repo, ScmFileSet fileSet,
          Date startDate, Date endDate, ScmBranch branch,
          String datePattern, ScmVersion startVersion,
          ScmVersion endVersion)
          throws ScmException {

    PerforceScmProviderRepository p4repo = (PerforceScmProviderRepository) repo;
    String clientspec = PerforceScmProvider.getClientspecName(getLogger(), p4repo, fileSet.getBasedir());
    Commandline commandLine = createCommandLine(p4repo, fileSet.getBasedir(), clientspec, null, startDate, endDate, startVersion, endVersion);

    String location = PerforceScmProvider.getRepoPath(getLogger(), p4repo, fileSet.getBasedir());

    List<String> changes = getChanges(commandLine);
    StatsPerforceDescribeConsumer describeConsumer =
            new StatsPerforceDescribeConsumer(location, datePattern, getLogger());

    long changesCount = changes.size();
    CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();

    for (int i = 0; i <= changes.size(); i = i + 100) {
      int fromChange = i;
      int toChange = (int) Math.min(changesCount - 1, i + 100);
      commandLine = PerforceScmProvider.createP4Command(p4repo, fileSet.getBasedir());
      commandLine.createArg().setValue("describe");
      commandLine.createArg().setValue("-s");
      for (int j = fromChange; j <= toChange; j++) {
        if (toChange < changesCount) {
          commandLine.createArg().setValue(changes.get(j));
        }
      }

      try {
        getLogger().info(PerforceScmProvider.clean("Executing " + commandLine.toString()));
        int exitCode = CommandLineUtils.executeCommandLine(commandLine, describeConsumer, err);
        throwExceptionOnFailure(exitCode, commandLine, err);
      } catch (CommandLineException e) {
        if (getLogger().isErrorEnabled()) {
          getLogger().error("CommandLineException " + e.getMessage(), e);
        }
      }
    }

    ChangeLogSet cls = new ChangeLogSet(describeConsumer.getModifications(), null, null);
    cls.setStartVersion(startVersion);
    cls.setEndVersion(endVersion);
    return new ChangeLogScmResult(commandLine.toString(), cls);
  }

  public static Commandline createCommandLine(PerforceScmProviderRepository repo, File workingDirectory,
          String clientspec,
          ScmBranch branch, Date startDate, Date endDate,
          ScmVersion startVersion, ScmVersion endVersion) {

    Commandline command = PerforceScmProvider.createP4Command(repo, workingDirectory);

    if (clientspec != null) {
      command.createArg().setValue("-c");
      command.createArg().setValue(clientspec);
    }
    command.createArg().setValue("changes");
    command.createArg().setValue("-t");

    StringBuilder fileSpec = new StringBuilder(repo.getPath());
    fileSpec.append("...");
    fileSpec.append(addDateRange(startDate, endDate));
    fileSpec.append(addVersionRange(startVersion, endVersion));

    command.createArg().setValue(fileSpec.toString());

    return command;
  }

  private void throwExceptionOnFailure(int exitCode, Commandline commandLine,
          CommandLineUtils.StringStreamConsumer err) throws CommandLineException {
    if (exitCode != SUCCESS_EXIT_CODE) {
      String cmdLine = CommandLineUtils.toString(commandLine.getCommandline());

      StringBuilder msg = new StringBuilder("Exit code: " + exitCode + " - " + err.getOutput());
      msg.append('\n');
      msg.append("Command line was:");
      msg.append(cmdLine);

      throw new CommandLineException(msg.toString());
    }
  }

  private List<String> getChanges(Commandline commandLine) throws ScmException {
    PerforceChangesConsumer consumer = new PerforceChangesConsumer(getLogger());
    try {
      getLogger().info(PerforceScmProvider.clean("Executing " + commandLine.toString()));
      CommandLineUtils.StringStreamConsumer err = new CommandLineUtils.StringStreamConsumer();
      int exitCode = CommandLineUtils.executeCommandLine(commandLine, consumer, err);
      throwExceptionOnFailure(exitCode, commandLine, err);
    } catch (CommandLineException e) {
      if (getLogger().isErrorEnabled()) {
        getLogger().error("CommandLineException " + e.getMessage(), e);
      }
    }
    return consumer.getChanges();
  }

  private static String addDateRange(Date startDate, Date endDate) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd:HH:mm:ss", Locale.getDefault());
    StringBuilder fileSpec = new StringBuilder("");
    if (startDate != null) {
      fileSpec.append("@")
              .append(dateFormat.format(startDate))
              .append(",");

      if (endDate == null) {
        fileSpec.append("now");
      } else {
        fileSpec.append(dateFormat.format(endDate));
      }
    }
    return fileSpec.toString();
  }

  private static String addVersionRange(ScmVersion startVersion, ScmVersion endVersion) {
    StringBuilder fileSpec = new StringBuilder();
    if (startVersion != null) {
      fileSpec.append("@").append(startVersion.getName()).append(",");

      if (endVersion == null) {
        fileSpec.append("now");
      } else {
        fileSpec.append(endVersion.getName());
      }
    }
    return fileSpec.toString();
  }
}
