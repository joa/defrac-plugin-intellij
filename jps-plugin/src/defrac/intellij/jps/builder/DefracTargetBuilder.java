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
import defrac.intellij.jps.model.JpsDefracSdkProperties;
import defrac.intellij.sdk.DefracVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.TargetBuilder;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 */
public abstract class DefracTargetBuilder<T extends DefracBuildTarget, P extends DefracBuildTargetType<? extends T>>
    extends TargetBuilder<BuildRootDescriptor, T> {
  @NotNull
  protected final P targetType;

  protected DefracTargetBuilder(@NotNull final P targetType) {
    super(Collections.singletonList(targetType));
    this.targetType = targetType;
  }

  @Override
  public final void build(@NotNull T target,
                          @NotNull DirtyFilesHolder<BuildRootDescriptor, T> holder,
                          @NotNull BuildOutputConsumer outputConsumer,
                          @NotNull CompileContext context) throws ProjectBuildException, IOException {
    buildTarget(target, holder, outputConsumer, context);
  }

  protected abstract void buildTarget(@NotNull T target,
                                      @NotNull DirtyFilesHolder<BuildRootDescriptor, T> holder,
                                      @NotNull BuildOutputConsumer outputConsumer,
                                      @NotNull CompileContext context) throws ProjectBuildException, IOException;

  @Nullable
  protected File getOuptutDir(@Nullable final JpsModule module) {
    return module == null
        ? null
        : DefracJpsUtil.getFileByRelativeProjectPath(module.getProject(),
            "/target/"+targetType.getPlatform().name);
  }

  @NotNull
  protected abstract String getDefracCommand();

  protected boolean runDefrac(
      @NotNull JpsDefracModuleExtension extension,
      @NotNull JpsDefracSdkProperties sdkProperties,
      @NotNull DefracVersion version,
      @NotNull String outFilePath,
      @NotNull String[] compileTargets,
      @NotNull CompileContext context,
      @NotNull JpsProject project,
      @NotNull final JpsModule module,
      @NotNull BuildOutputConsumer outputConsumer,
      @NotNull String builderName,
      @NotNull String srcTargetName) {
    final File executable = DefracJpsUtil.getDefracExecutable(module, context, builderName);

    if(executable == null) {
      return false;
    }

    final ArrayList<String> cmd = Lists.newArrayList();

    cmd.add(executable.getAbsolutePath());

  }
}
