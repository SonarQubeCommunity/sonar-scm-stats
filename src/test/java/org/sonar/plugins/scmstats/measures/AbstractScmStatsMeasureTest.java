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
package org.sonar.plugins.scmstats.measures;

import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import static org.mockito.Mockito.*;
import org.sonar.api.measures.Measure;

public class AbstractScmStatsMeasureTest {

  private final SensorContext mockedContext = mock(SensorContext.class);
  private final static Map<String, Integer> dataMap = new HashMap<String, Integer>();
  private AbstractScmStatsMeasureImpl measure;
  private AbstractScmStatsMeasureImpl spy;
  private Measure sonarMeasure = new Measure();

  @Before
  public void setUp() {
    dataMap.put("01", 1);
    dataMap.put("02", 2);
    dataMap.put("03", 3);
    measure = new AbstractScmStatsMeasureImpl(dataMap, mockedContext);
    spy = spy(measure);
    when(spy.getMeasure()).thenReturn(sonarMeasure);
    when(mockedContext.saveMeasure(sonarMeasure)).thenReturn(sonarMeasure);

  }

  @Test
  public void testDefaultConstructor() {
    assertThat(measure.getDataMap().containsKey("01"), is(true));
    assertThat(measure.getDataMap().containsKey("02"), is(true));
    assertThat(measure.getDataMap().containsKey("03"), is(true));
    assertThat(measure.getDataMap().size(), is(3));

  }

  @Test
  public void testSave() {
    
    spy.save();
    verify(mockedContext).saveMeasure(sonarMeasure);
    
  }

  public class AbstractScmStatsMeasureImpl extends AbstractScmStatsMeasure {

    public AbstractScmStatsMeasureImpl(final Map<String, Integer> map,
            final SensorContext context) {
      super(ScmStatsMetrics.SCM_COMMITS_PER_CLOCKTIME, map, context);
    }

    @Override
    protected void init() {
    }
  }
}
