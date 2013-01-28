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

package org.sonar.plugins.scmstats.utils;

import java.util.HashMap;
import java.util.Map;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class MapUtilsTest {
  
  /**
   * Test of updateMap method, of class MapUtils.
   */
  @Test
  public void testUpdateMap() {
    
    Map<String, Integer> map = new HashMap<String, Integer>();
    map.put("KEY1", 10);
    Map result = MapUtils.updateMap(map, "KEY1");
    result = MapUtils.updateMap(result, "KEY2");
    assertThat(result.size(),is(2));
    assertThat(result.containsKey("KEY1"),is(true));
    assertThat(result.containsKey("KEY2"),is(true));
    assertThat((Integer)result.get("KEY1"),equalTo(11));
    assertThat((Integer)result.get("KEY2"),is(1));
    
  }
}
