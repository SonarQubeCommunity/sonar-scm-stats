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
package org.sonar.plugins.scmstats;

import org.junit.*;
import static org.junit.Assert.*;
import org.sonar.api.utils.SonarException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
public class UrlCheckerTest {

  @Test
  public void CheckForEmptyUrl() {
    String url = "";
    UrlChecker instance = new UrlChecker();
    try {
      instance.check(url);
    } catch (SonarException ex) {
      assertThat (ex.getLocalizedMessage(),equalTo(UrlChecker.FAILURE_BLANK + ". " + UrlChecker.PARAMETER_MESSAGE));
    }
  }

  @Test
  public void CheckForInvalidUrl() {
    String url = "scm:svn:svn:\\";
    UrlChecker instance = new UrlChecker();
    try {
      instance.check(url);
    } catch (SonarException ex) {
      assertThat (ex.getLocalizedMessage(),equalTo(UrlChecker.FAILURE_FORMAT + ". " + UrlChecker.PARAMETER_MESSAGE));
    }
  }

  @Test
  public void CheckForUnSupportedUrl() {
    String url = "scm:jazz:https://";
    UrlChecker instance = new UrlChecker();
    try {
      instance.check(url);
    } catch (SonarException ex) {
      assertThat (ex.getLocalizedMessage(),equalTo("Unsupported SCM: [jazz]. Check compatibility at http://docs.codehaus.org/display/SONAR/SCM+Stats+Plugin" + ". " + UrlChecker.PARAMETER_MESSAGE));
    }
  }
}
