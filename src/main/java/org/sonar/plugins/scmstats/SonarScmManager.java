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

import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.manager.AbstractScmManager;
import org.apache.maven.scm.provider.ScmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;

public class SonarScmManager extends AbstractScmManager implements BatchExtension {
  
  public SonarScmManager() {
    registerProviders();
  }

  private void registerProviders() {
    for (SupportedScm supportedScm : SupportedScm.values()) {
      ScmProvider provider = supportedScm.getProvider();

      setScmProvider(provider.getScmType(), supportedScm.getProvider());
    }
  }

  @Override
  protected ScmLogger getScmLogger() {
    return new SonarScmLogger(LoggerFactory.getLogger(getClass()));
  }

  @VisibleForTesting
  static class SonarScmLogger implements ScmLogger {
    private final Logger log;

    SonarScmLogger(Logger log) {
      this.log = log;
    }

    public boolean isDebugEnabled() {
      return log.isDebugEnabled();
    }

    public void debug(String content) {
      log.debug(content);
    }

    public void debug(String content, Throwable error) {
      log.debug(content, error);
    }

    public void debug(Throwable error) {
      log.debug(error.getMessage(), error);
    }

    public boolean isInfoEnabled() {
      return log.isInfoEnabled();
    }

    public void info(String content) {
      log.info(content);
    }

    public void info(String content, Throwable error) {
      log.info(content, error);
    }

    public void info(Throwable error) {
      log.info(error.getMessage(), error);
    }

    public boolean isWarnEnabled() {
      return log.isWarnEnabled();
    }

    public void warn(String content) {
      log.warn(content);
    }

    public void warn(String content, Throwable error) {
      log.warn(content, error);
    }

    public void warn(Throwable error) {
      log.warn(error.getMessage(), error);
    }

    public boolean isErrorEnabled() {
      return log.isErrorEnabled();
    }

    public void error(String content) {
      log.error(content);
    }

    public void error(String content, Throwable error) {
      log.error(content, error);
    }

    public void error(Throwable error) {
      log.error(error.getMessage(), error);
    }
  }
}
