/*
 * Copyright 2014 defrac inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package defrac.intellij.jps.model;

import defrac.intellij.DefracPlatform;
import defrac.intellij.sdk.DefracVersion;
import defrac.intellij.util.Names;

/**
 *
 */
public final class JpsDefracModuleProperties {
  public String PLATFORM = DefracPlatform.GENERIC.name;
  public String SETTINGS_FILE_RELATIVE_PATH = "/"+Names.default_settings;
  public String DEFRAC_VERSION = DefracVersion.LATEST;
  public boolean IS_MACRO_LIBRARY = false;

  public JpsDefracModuleProperties() {}
}
