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

package org.sonar.plugins.scmstats.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.scm.provider.ScmUrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;

public class UrlChecker implements BatchExtension {
  public static final String PARAMETER_MESSAGE = String.format("SCM Stats Plugin will not run.Please check the parameter SCM URL or the <scm> section of Maven pom.");
  public static final String FAILURE_BLANK = "SCM URL must not be blank";
  public static final String FAILURE_FORMAT = "URL does not respect the SCM URL format described in http://maven.apache.org/scm/scm-url-format.html: [%s]";
  public static final String FAILURE_NOT_SUPPORTED = "Unsupported SCM: [%s]. Check compatibility at http://docs.codehaus.org/display/SONAR/SCM+Stats+Plugin";
  private static final Logger LOG = LoggerFactory.getLogger(UrlChecker.class);

  public boolean check(String url) {
    if (StringUtils.isBlank(url)) {
      warn(FAILURE_BLANK);
      return false;
    }
    if (!ScmUrlUtils.isValid(url)) {
      warn(FAILURE_FORMAT,url);
      return false;
    }
    if (!isSupported(url)) {
      warn(FAILURE_NOT_SUPPORTED, ScmUrlUtils.getProvider(url));
      return false;
    }
    return true;
  }

  private static boolean isSupported(String url) {
    for (SupportedScm scm : SupportedScm.values()) {
      if (ScmUrlUtils.getProvider(url).equals(scm.getType())) {
        return true;
      }
    }
    return false;
  }

  private static void warn(String format, Object... args) {
    LOG.warn (String.format(format, args) + ". " + PARAMETER_MESSAGE);
  }
}
