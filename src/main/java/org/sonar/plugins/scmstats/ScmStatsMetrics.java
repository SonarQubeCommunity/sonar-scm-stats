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

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public final class ScmStatsMetrics implements Metrics {

  public static final Metric SCM_COMMITS_PER_USER = 
          new Metric.Builder("scm-commits-per-user", "Commits per user", 
          Metric.ValueType.DISTRIB)
      .setDescription("Commits per user")
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_SCM)
      .create();

  public static final Metric SCM_COMMITS_PER_CLOCKTIME = 
          new Metric.Builder("scm-commits-per-clockhour", "Commits per clock hour", 
          Metric.ValueType.DATA)
      .setDescription("Commits per clock hour")
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_SCM)
      .create();


  public List<Metric> getMetrics() {
    return ImmutableList.of(SCM_COMMITS_PER_USER,SCM_COMMITS_PER_CLOCKTIME);
  }

}
