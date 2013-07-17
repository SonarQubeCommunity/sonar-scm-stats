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
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.scm.provider.ScmUrlUtils;
import org.sonar.api.BatchExtension;
import org.sonar.api.config.Settings;

public class ScmConfiguration implements BatchExtension {

  private final Settings settings;
  private final MavenScmConfiguration mavenConfiguration;
  private final Supplier<String> url;

  public ScmConfiguration(Settings settings, MavenScmConfiguration mavenConfiguration) {
    this.settings = settings;
    this.mavenConfiguration = mavenConfiguration;
    url = Suppliers.memoize(new UrlSupplier());
  }

  public ScmConfiguration(Settings settings) {
    this(settings,null /** not in maven environment*/);
  }

  public String getScmProvider() {
    if (StringUtils.isBlank(getUrl())) {
      return null;
    }
    return ScmUrlUtils.getProvider(getUrl());
  }

  public boolean isEnabled() {
    return settings.getBoolean(ScmStatsConstants.ENABLED);
  }

  public String getUser() {
    return settings.getString(ScmStatsConstants.USER);
  }
  
  public String getPassword() {
    return settings.getString(ScmStatsConstants.PASSWORD);
  }

  public Integer getFirstPeriod() {
    return settings.getInt(ScmStatsConstants.PERIOD_1);
  }

  public Integer getSecondPeriod() {
    return settings.getInt(ScmStatsConstants.PERIOD_2);
  }

  public Integer getThirdPeriod() {
    return settings.getInt(ScmStatsConstants.PERIOD_3);
  }

  public List<String> getIgnoreAuthorsList() {
    return Arrays.asList(settings.getStringArray(ScmStatsConstants.IGNORE_AUTHORS_LIST));
  }

  public String getPerforceClientSpec() {
    return settings.getString(ScmStatsConstants.PERFORCE_CLIENTSPEC);
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

      String urlPropertyFromScmActivity = settings.getString(ScmStatsConstants.URL);
      if (!StringUtils.isBlank(urlPropertyFromScmActivity)) {
        return urlPropertyFromScmActivity;
      }

      return null;
    }

    private String getMavenUrl() {
      if (mavenConfiguration == null) {
        return null;
      }
      if (StringUtils.isNotBlank(mavenConfiguration.getDeveloperUrl()) && StringUtils.isNotBlank(getUser())) {
        return mavenConfiguration.getDeveloperUrl();
      }
      return mavenConfiguration.getUrl();
    }

  }
  
  public Settings getSettings(){
    return settings;
  }
}
