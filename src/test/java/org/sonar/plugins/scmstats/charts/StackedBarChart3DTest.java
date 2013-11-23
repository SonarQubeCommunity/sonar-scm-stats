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

import org.sonar.plugins.scmstats.charts.StackedBarChart3D;
import java.util.HashMap;
import java.util.Map;
import static org.fest.assertions.Assertions.assertThat;

import org.jfree.chart.plot.CategoryPlot;
import org.junit.Test;
import org.sonar.api.charts.ChartParameters;

public class StackedBarChart3DTest {
  private final StackedBarChart3D chart = new StackedBarChart3D();
  
  @Test
  public void testGetKey() {
    assertThat(chart.getKey()).isEqualTo("stackedBarChart3D");
  }

  @Test
  public void testGetPlot() {
    Map<String,String> params = new HashMap<String,String>();
    params.put("v", "author1=1. 2. 3;author2=4. 5. 6;author3=7. 8. 9");
    ChartParameters chartParams = new ChartParameters(params);
    
    CategoryPlot result = (CategoryPlot) chart.getPlot(chartParams);
    
    assertThat( result).isNotNull();
    assertThat(result.getDataset().getRowKeys()).contains("Adding", "Modifying", "Deleting");
    assertThat(result.getDataset().getColumnKeys()).contains("author1", "author2", "author3");
    assertThat(result.getDataset().getValue("Adding", "author1")).isEqualTo(1.0);
    assertThat(result.getDataset().getValue("Deleting", "author3")).isEqualTo(9.0);
    
  }
}