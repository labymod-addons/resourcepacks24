/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.labymod.addons.resourcepacks24.core.util;

import java.text.DecimalFormat;

public class FileSizeConverter {

  private static final int KB = 1000;
  private static final int MB = 1000 * KB;
  private static final int GB = 1000 * MB;
  private static final int[] UNIT_THRESHOLDS = new int[]{1, KB, MB, GB};
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
  private static final String[] UNIT_SUFFIXES = new String[]{" bytes", " KB", " MB", " GB"};

  private FileSizeConverter() {
    // utility class
  }

  public static String convertToHumanReadableString(long bytes) {
    for (int i = UNIT_THRESHOLDS.length - 1; i >= 0; i--) {
      if (bytes >= UNIT_THRESHOLDS[i]) {
        return DECIMAL_FORMAT.format(bytes / (double) UNIT_THRESHOLDS[i]) + UNIT_SUFFIXES[i];
      }
    }

    return bytes + UNIT_SUFFIXES[0];
  }
}
