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

public final class MapUtils {

  private MapUtils() {
  }
  public static Map<String, Integer> updateMap(final Map<String, Integer> map, final String key) {

    final Map<String, Integer> updatedMap = new HashMap<String, Integer>();
    updatedMap.putAll(map);
    if (updatedMap.containsKey(key)) {
      updatedMap.put(key, updatedMap.get(key) + 1);
    } else {
      updatedMap.put(key, 1);
    }

    return updatedMap;
  }
}
