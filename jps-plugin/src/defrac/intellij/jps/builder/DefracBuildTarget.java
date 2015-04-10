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

import com.google.common.collect.Lists;
import defrac.intellij.jps.DefracJpsUtil;
import defrac.intellij.jps.model.JpsDefracModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.*;
import org.jetbrains.jps.builders.impl.BuildRootDescriptorImpl;
import org.jetbrains.jps.builders.java.JavaModuleBuildTargetType;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ModuleBuildTarget;
import org.jetbrains.jps.indices.IgnoredFileIndex;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public abstract class DefracBuildTarget extends BuildTarget<BuildRootDescriptor> {
  @NotNull
  private final DefracBuildTargetType<?> targetType;

  protected final JpsModule module;

  DefracBuildTarget(@NotNull final DefracBuildTargetType<?> targetType, @NotNull final JpsModule module) {
    super(targetType);

    this.targetType = targetType;
    this.module = module;
  }

  @NotNull
  public final JpsModule getModule() {
    return module;
  }

  @NotNull
  public final String getId() {
    return getModule().getName();
  }

  @NotNull
  @Override
  public List<BuildRootDescriptor> computeRootDescriptors(final JpsModel model,
                                                          final ModuleExcludeIndex index,
                                                          final IgnoredFileIndex ignoredFileIndex,
                                                          final BuildDataPaths dataPaths) {
    final JpsDefracModuleExtension extension = DefracJpsUtil.getExtension(module);

    if(extension == null || !targetType.isSuitablePlatform(extension.getPlatform())) {
      return Collections.emptyList();
    }

    final List<BuildRootDescriptor> roots = Lists.newArrayList();

    for(final File resourceRoot : DefracJpsUtil.getJavaOutputRootsForModuleAndDependencies(module)) {
      roots.add(new BuildRootDescriptorImpl(this, resourceRoot));
    }

    //TODO(joa): configured libs
    //TODO(joa): configured resources

    roots.addAll(computePlatformSpecificRootDescriptors(model, index, ignoredFileIndex, dataPaths));

    return roots;
  }

  @NotNull
  public abstract List<BuildRootDescriptor> computePlatformSpecificRootDescriptors(final JpsModel model,
                                                                                   final ModuleExcludeIndex index,
                                                                                   final IgnoredFileIndex ignoredFileIndex,
                                                                                   final BuildDataPaths dataPaths);

  @NotNull
  @Override
  public Collection<File> getOutputRoots(final CompileContext compileContext) {
    final JpsDefracModuleExtension extension = DefracJpsUtil.getExtension(module);

    if(extension == null) {
      return Collections.emptyList();
    }

    final File target =
        DefracJpsUtil.getFileByRelativeProjectPath(module.getProject(), "/target/"+targetType.getPlatform().name);

    if(target == null) {
      return Collections.emptyList();
    }

    return Collections.singletonList(target);
  }

  @Override
  public Collection<BuildTarget<?>> computeDependencies(final BuildTargetRegistry registry, TargetOutputIndex outputIndex) {
    final List<BuildTarget<?>> result = Lists.newArrayListWithExpectedSize(3);

    fillDependencies(result);
    result.add(new ModuleBuildTarget(module, JavaModuleBuildTargetType.PRODUCTION));

    /*
    final JpsDefracModuleExtension extension = DefracJpsUtil.getExtension(module);

    if(extension != null && extension.isPackTestCode()) {
      result.add(new ModuleBuildTarget(module, JavaModuleBuildTargetType.TEST));
    }*/

    return result;
  }

  protected void fillDependencies(List<BuildTarget<?>> result) {}

  @NotNull
  @Override
  public String getPresentableName() {
    return "defrac:"+targetType.getDisplayName();
  }

  @Nullable
  @Override
  public BuildRootDescriptor findRootDescriptor(String rootId, BuildRootIndex rootIndex) {
    for(BuildRootDescriptor descriptor : rootIndex.getTargetRoots(this, null)) {
      if(descriptor.getRootId().equals(rootId)) {
        return descriptor;
      }
    }

    return null;
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }

    if(obj == null || !(obj instanceof DefracBuildTarget)) {
      return false;
    }

    final DefracBuildTarget that = (DefracBuildTarget)obj;
    return getTargetType() == that.getTargetType() && getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return 31 * getId().hashCode() + getTargetType().hashCode();
  }
}
