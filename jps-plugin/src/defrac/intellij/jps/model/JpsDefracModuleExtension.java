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

import com.intellij.util.xmlb.XmlSerializerUtil;
import defrac.intellij.DefracPlatform;
import defrac.intellij.jps.DefracJpsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.ex.JpsElementBase;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.File;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 */
public final class JpsDefracModuleExtension extends JpsElementBase<JpsDefracModuleExtension> {
  @NotNull
  public static final JpsElementChildRoleBase<JpsDefracModuleExtension> KIND =
      JpsElementChildRoleBase.create("defrac extension");

  @NotNull
  private final JpsDefracModuleProperties properties;

  public JpsDefracModuleExtension(@NotNull final JpsDefracModuleProperties properties) {
    this.properties = properties;
  }

  @NotNull
  @Override
  public JpsDefracModuleExtension createCopy() {
    return new JpsDefracModuleExtension(XmlSerializerUtil.createCopy(properties));
  }

  @Override
  public void applyChanges(@NotNull JpsDefracModuleExtension modified) {
    XmlSerializerUtil.copyBean(modified.properties, properties);
    fireElementChanged();
  }

  @NotNull
  public JpsModule getModule() {
    return (JpsModule)getParent();
  }

  @NotNull
  public JpsProject getProject() {
    return getModule().getProject();
  }

  @NotNull
  public JpsDefracModuleProperties getProperties() {
    return properties;
  }

  @Nullable
  public DefracPlatform getPlatform() {
    final String stringValue = getProperties().PLATFORM;
    return isNullOrEmpty(stringValue)
        ? null
        : checkNotNull(DefracPlatform.byName(stringValue));
  }

  @Nullable
  public String getSettingsFileRelativePath() {
    return getProperties().SETTINGS_FILE_RELATIVE_PATH;
  }

  @Nullable
  public File getSettingsFile() {
    return DefracJpsUtil.getFileByRelativeProjectPath(getProject(), getSettingsFileRelativePath());
  }

  @Nullable
  public String getDefracVersion() {
    return getProperties().DEFRAC_VERSION;
  }

  public boolean isMacroLibrary() {
    return getProperties().IS_MACRO_LIBRARY;
  }

  public boolean skipJavac() {
    return getProperties().SKIP_JAVAC;
  }
}
