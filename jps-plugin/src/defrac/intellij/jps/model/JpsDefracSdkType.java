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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.java.JpsJavaSdkTypeWrapper;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

/**
 *
 */
public final class JpsDefracSdkType extends JpsSdkType<JpsSimpleElement<JpsDefracSdkProperties>> implements JpsJavaSdkTypeWrapper {
  @NotNull
  public static final JpsDefracSdkType INSTANCE = new JpsDefracSdkType();

  @Override
  public String getJavaSdkName(@NotNull JpsElement properties) {
    return ((JpsDefracSdkProperties)((JpsSimpleElement<?>)properties).getData()).getJdkName();
  }
}
