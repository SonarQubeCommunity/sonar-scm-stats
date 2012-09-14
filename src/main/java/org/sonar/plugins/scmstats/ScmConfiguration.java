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

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.scm.provider.ScmUrlUtils;
import org.sonar.api.BatchExtension;

public class ScmConfiguration implements BatchExtension {

  private final Configuration configuration;
  private final MavenScmConfiguration mavenConfonfiguration;
  private final Supplier<String> url;

  public ScmConfiguration(Configuration configuration, MavenScmConfiguration mavenConfiguration) {
    this.configuration = configuration;
    this.mavenConfonfiguration = mavenConfiguration;
    url = Suppliers.memoize(new UrlSupplier());
  }

  public ScmConfiguration(Configuration configuration) {
    this(configuration,null /** not in maven environment*/);
  }

  public String getScmProvider() {
    if (StringUtils.isBlank(getUrl())) {
      return null;
    }
    return ScmUrlUtils.getProvider(getUrl());
  }

  public boolean isEnabled() {
    return configuration.getBoolean(ScmStatsPlugin.ENABLED, ScmStatsPlugin.ENABLED_DEFAULT);
  }

  public String getUrl() {
    return url.get();
  }

  private class UrlSupplier implements Supplier<String> {

    public String get() {
      String mavenUrl = getMavenUrl();
      if (!StringUtils.isBlank(mavenUrl)) {
        return mavenUrl;
      }

      String urlPropertyFromScmActivity = configuration.getString("sonar.scm.url");
      if (!StringUtils.isBlank(urlPropertyFromScmActivity)) {
        return urlPropertyFromScmActivity;
      }

      return null;
    }

    private String getMavenUrl() {
      if (mavenConfonfiguration == null) {
        return null;
      }
//      if (StringUtils.isNotBlank(mavenConfonfiguration.getDeveloperUrl()) && StringUtils.isNotBlank(getUser())) {
//        return mavenConfonfiguration.getDeveloperUrl();
//      }
      return mavenConfonfiguration.getUrl();
    }

  }
}
