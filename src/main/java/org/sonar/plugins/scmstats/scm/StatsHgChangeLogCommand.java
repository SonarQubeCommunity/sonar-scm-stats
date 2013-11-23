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

import org.apache.maven.scm.ChangeSet;
import org.apache.maven.scm.ScmBranch;
import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.command.changelog.ChangeLogSet;
import org.apache.maven.scm.provider.ScmProviderRepository;
import org.apache.maven.scm.provider.hg.HgUtils;
import org.apache.maven.scm.provider.hg.command.HgCommandConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.maven.scm.provider.hg.command.changelog.HgChangeLogCommand;

public class StatsHgChangeLogCommand extends HgChangeLogCommand {

  private Locale locale = null;
  
  public StatsHgChangeLogCommand(){
    super();
  }
  
  public StatsHgChangeLogCommand(Locale locale){
    super();
    this.locale = locale;
  }
  
  @Override
  protected ChangeLogScmResult executeChangeLogCommand(ScmProviderRepository scmProviderRepository,
          ScmFileSet fileSet, Date startDate, Date endDate,
          ScmBranch branch, String datePattern)
          throws ScmException {
    return executeChangeLogCommand(fileSet, startDate, endDate, datePattern, null);
  }

  private ChangeLogScmResult executeChangeLogCommand(ScmFileSet fileSet, Date startDate, Date endDate,
          String datePattern, Integer limit)
          throws ScmException {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    StringBuilder dateInterval = new StringBuilder();
    // TRICK: Mercurial 1.9.3 don't accept 1970-01-01
    dateInterval.append(
            dateFormat.format(startDate == null ? new Date(1000L * 60 * 60 * 24) : startDate)); // From 2. Jan 1970
    dateInterval.append(" to ");
    dateInterval.append(dateFormat.format(endDate == null ? new Date() : endDate)); // Upto now

    List<String> cmd = new ArrayList<String>();
    cmd.addAll(Arrays.asList(HgCommandConstants.LOG_CMD, HgCommandConstants.VERBOSE_OPTION,
            HgCommandConstants.NO_MERGES_OPTION, HgCommandConstants.DATE_OPTION,
            dateInterval.toString()));

    if (limit != null && limit > 0) {
      cmd.add(HgCommandConstants.LIMIT_OPTION);
      cmd.add(Integer.toString(limit));
    }

    StatsHgChangeLogConsumer consumer = new StatsHgChangeLogConsumer(getLogger(), datePattern, locale);
    ScmResult result = HgUtils.execute(consumer, getLogger(), fileSet.getBasedir(), cmd.toArray(new String[cmd.size()]));

    List<ChangeSet> logEntries = consumer.getModifications();
    ChangeLogSet changeLogSet = new ChangeLogSet(logEntries, startDate, endDate);
    return new ChangeLogScmResult(changeLogSet, result);
  }

}
