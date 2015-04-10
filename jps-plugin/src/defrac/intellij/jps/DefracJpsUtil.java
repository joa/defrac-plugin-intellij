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

package defrac.intellij.jps;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import com.intellij.openapi.util.io.FileUtil;
import defrac.intellij.jps.model.JpsDefracModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.serialization.JpsModelSerializationDataService;

import java.io.File;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 *
 */
public final class DefracJpsUtil {
  public static final Function<JpsModule, JpsDefracModuleExtension> MODULE_TO_EXTENSION = new Function<JpsModule, JpsDefracModuleExtension>() {
    @Override
    public JpsDefracModuleExtension apply(final JpsModule module) {
      return getExtension(module);
    }
  };

  @Nullable
  public static File getFileByRelativeProjectPath(@NotNull final JpsProject project,
                                                  @Nullable final String relativePath) {
    if(isNullOrEmpty(relativePath)) {
      return null;
    }

    final File projectBaseDir = JpsModelSerializationDataService.getBaseDirectory(project);

    if(projectBaseDir != null) {
      final String absPath = FileUtil.toSystemDependentName(projectBaseDir.getAbsolutePath() + File.separatorChar + relativePath);
      return new File(absPath);
    }

    return null;
  }

  @Nullable
  public static JpsDefracModuleExtension getExtension(@NotNull JpsModule module) {
    return module.getContainer().getChild(JpsDefracModuleExtension.KIND);
  }


  @NotNull
  public static File[] getJavaOutputRootsForModuleAndDependencies(@NotNull final JpsModule rootModule) {
    final Set<File> result = Sets.newHashSet();

    for(final JpsModule module : getRuntimeModuleDeps(rootModule)) {
      final File outputDir = JpsJavaExtensionService.getInstance().getOutputDirectory(module, false);

      if(outputDir != null) {
        result.add(outputDir);
      }

      /*final JpsDefracModuleExtension extension = getExtension(module);

      if (extension != null && extension.isPackTestCode()) {
        final File testOutputDir = JpsJavaExtensionService.getInstance().getOutputDirectory(module, true);
        if (testOutputDir != null) {
          result.add(testOutputDir);
        }
      }*/
    }

    return result.toArray(new File[result.size()]);
  }

  private static Set<JpsModule> getRuntimeModuleDeps(JpsModule rootModule) {
    return JpsJavaExtensionService.getInstance().enumerateDependencies(
        Collections.singletonList(rootModule)).recursively().runtimeOnly().getModules();
  }

  private DefracJpsUtil() {}
}
