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

import com.google.common.annotations.VisibleForTesting;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.provider.hg.command.changelog.HgChangeLogConsumer;
import org.codehaus.plexus.util.StringUtils;

public class StatsHgChangeLogConsumer extends HgChangeLogConsumer {

  private final Locale locale;
  public StatsHgChangeLogConsumer(ScmLogger logger, String userDatePattern, Locale locale) {
    super(logger, userDatePattern);
    this.locale = locale;
  }

  /**
   * Overrides parseDate implementation of AbstractConsumer to use Locale 
   * when parsing dates with user date pattern.
   *
   * @return A date representing the timestamp of the log entry.
   */
  @Override
  @VisibleForTesting
  protected Date parseDate(String date, String userPattern, String defaultPattern, Locale locale) {
    DateFormat format;
    String patternUsed = "dd/MM/yyyy";
    Locale localeUsed = locale;
    if (StringUtils.isNotEmpty(userPattern)) {
      patternUsed = userPattern;
    } else if (StringUtils.isNotEmpty(defaultPattern)) {
      patternUsed = defaultPattern;
    }
    if ( this.locale != null ) {
      localeUsed = this.locale;
    }

    format = new SimpleDateFormat(patternUsed, localeUsed);

    try {
      return format.parse(date);
    } catch (ParseException e) {
      if (getLogger() != null && getLogger().isWarnEnabled()) {
        getLogger().warn(
                "skip ParseException: " + e.getMessage() + " during parsing date " + date
                + " with pattern " + patternUsed + " with Locale "
                + localeUsed, e);
      }

      return null;
    }
  }
}
