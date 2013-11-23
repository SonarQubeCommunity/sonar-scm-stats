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
import java.awt.Color;
import org.jfree.chart.plot.Plot;
import org.sonar.api.charts.AbstractChart;
import org.sonar.api.charts.ChartParameters;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import org.codehaus.plexus.util.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StackedBarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

public class StackedBarChart3D extends AbstractChart {

  public static final String PARAM_VALUES = "v";
  public static final String PARAM_COLORS = "c";

  public String getKey() {
    return "stackedBarChart3D";
  }

  @Override
  protected Plot getPlot(ChartParameters params) {
    JFreeChart chart = createChart(createDataset(params.getValue(PARAM_VALUES)));
    return chart.getPlot();
  }

  @VisibleForTesting
  private CategoryDataset createDataset(final String data) {
    final List<String> authors = Arrays.asList(StringUtils.split(data,";"));
    final DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
    
    for (String author : authors){
      String[] authorActivity = StringUtils.split(author,"=");
      authorActivity[1] = authorActivity[1].replace(".","");
      final String[] activities = StringUtils.split(authorActivity[1]);
      defaultcategorydataset.addValue(Long.parseLong(activities[0]), "Adding", authorActivity[0]);
      defaultcategorydataset.addValue(Long.parseLong(activities[1]), "Modifying", authorActivity[0]);
      defaultcategorydataset.addValue(Long.parseLong(activities[2]), "Deleting", authorActivity[0]);
    }
    return defaultcategorydataset;
  }

  private JFreeChart createChart(CategoryDataset categorydataset) {
    JFreeChart jfreechart = ChartFactory.createStackedBarChart3D(null, "Authors", "Activity", categorydataset, PlotOrientation.HORIZONTAL, true, true, false);
    CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
    
    NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
    numberaxis.setNumberFormatOverride(new DecimalFormat("0%"));

    StackedBarRenderer3D stackedbarrenderer3d = (StackedBarRenderer3D) categoryplot.getRenderer();
    stackedbarrenderer3d.setRenderAsPercentages(true);
    stackedbarrenderer3d.setDrawBarOutline(false);
    stackedbarrenderer3d.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{3}", NumberFormat.getIntegerInstance(), new DecimalFormat("0.0%")));
    stackedbarrenderer3d.setBaseItemLabelsVisible(true);
    stackedbarrenderer3d.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
    stackedbarrenderer3d.setBaseNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER));
    
    stackedbarrenderer3d.setSeriesPaint(0, Color.decode("#66CD00"));
    stackedbarrenderer3d.setSeriesPaint(1, Color.decode("#4F94CD"));
    stackedbarrenderer3d.setSeriesPaint(2, Color.decode("#FF4040"));
    return jfreechart;
  }
}
