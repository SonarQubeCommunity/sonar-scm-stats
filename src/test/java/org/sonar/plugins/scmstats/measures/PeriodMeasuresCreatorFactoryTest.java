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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.sonar.api.batch.SensorContext;
import org.sonar.plugins.scmstats.ScmStatsConstants;
public class PeriodMeasuresCreatorFactoryTest {
  
  private final PeriodMeasuresCreatorFactory factory = new PeriodMeasuresCreatorFactory();
  private SensorContext context;
  
  @Before
  public void mockObjects(){
    context = mock (SensorContext.class);
  }

  @Test
  public void shouldReturnFirstPeriodCreator(){
    
    assertThat (factory.getPeriodMeasureCreator(context,ScmStatsConstants.PERIOD_1), 
            instanceOf(FirstPeriodMeasuresCreator.class));
    
  }
  @Test
  public void shouldReturnSecondPeriodCreator(){
    
    assertThat (factory.getPeriodMeasureCreator(context,ScmStatsConstants.PERIOD_2), 
            instanceOf(SecondPeriodMeasuresCreator.class));
    
  }
  @Test
  public void shouldReturnThirdPeriodCreator(){
    
    assertThat (factory.getPeriodMeasureCreator(context,ScmStatsConstants.PERIOD_3), 
            instanceOf(ThirdPeriodMeasuresCreator.class));
    
  }

  @Test
  public void shouldReturnNull(){
    
    assertThat (factory.getPeriodMeasureCreator(context,""), 
            nullValue());
    
  }

}
