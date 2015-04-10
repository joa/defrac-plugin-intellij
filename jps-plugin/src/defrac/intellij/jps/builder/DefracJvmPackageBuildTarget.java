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

package defrac.intellij.jps.builder;

import defrac.intellij.DefracPlatform;
import defrac.intellij.jps.model.JpsDefracModuleExtension;
import defrac.intellij.util.Names;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.indices.IgnoredFileIndex;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.module.JpsModule;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class DefracJvmPackageBuildTarget extends DefracBuildTarget {
  public DefracJvmPackageBuildTarget(@NotNull JpsModule module) {
    super(TargetType.INSTANCE, module);
  }

  @NotNull
  @Override
  public List<BuildRootDescriptor> computePlatformSpecificRootDescriptors(final JpsModel model,
                                                                          final ModuleExcludeIndex index,
                                                                          final IgnoredFileIndex ignoredFileIndex,
                                                                          final BuildDataPaths dataPaths) {
    //TODO(joa): jars from sdk
    return Collections.emptyList();
  }

  public static class TargetType extends DefracBuildTargetType<DefracJvmPackageBuildTarget> {
    public static final TargetType INSTANCE = new TargetType();

    private TargetType() {
      super(Names.JVM_PACKAGE_BUILD_TARGET_TYPE_ID, "jvm:package", DefracPlatform.JVM);
    }

    @Override
    public DefracJvmPackageBuildTarget createBuildTarget(@NotNull JpsDefracModuleExtension extension) {
      return new DefracJvmPackageBuildTarget(extension.getModule());
    }
  }
}
