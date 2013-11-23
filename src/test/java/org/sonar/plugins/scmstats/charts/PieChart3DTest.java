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
package org.sonar.plugins.scmstats.charts;

import org.sonar.plugins.scmstats.charts.PieChart3D;
import java.util.HashMap;
import java.util.Map;
import static org.fest.assertions.Assertions.assertThat;

import org.jfree.chart.plot.PiePlot3D;
import org.junit.Test;
import org.sonar.api.charts.ChartParameters;

public class PieChart3DTest {

  private final PieChart3D chart = new PieChart3D();

  @Test
  public void testGetKey() {
    assertThat(chart.getKey()).isEqualTo("pieChart3D");
  }

  @Test
  public void testGetPlot() {
    Map<String, String> params = new HashMap<String, String>();
    params.put("v", "author1=1.0;author2=4.0;author3=7.0");
    ChartParameters chartParams = new ChartParameters(params);

    PiePlot3D result = (PiePlot3D) chart.getPlot(chartParams);
    assertThat(result.getDataset().getKeys()).contains("author1", "author2", "author3");
    assertThat(result.getDataset().getValue("author1")).isEqualTo(1.0);
    assertThat(result.getDataset().getValue("author2")).isEqualTo(4.0);
    assertThat(result.getDataset().getValue("author3")).isEqualTo(7.0);

  }
}