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

import com.intellij.compiler.options.CompileStepBeforeRun;
import defrac.intellij.jps.DefracJpsUtil;
import defrac.intellij.jps.model.JpsDefracModuleExtension;
import defrac.intellij.sdk.DefracVersion;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.StopBuildException;
import org.jetbrains.jps.model.module.JpsModule;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public final class DefracJvmPackageBuilder
    extends DefracTargetBuilder<DefracJvmPackageBuildTarget, DefracJvmPackageBuildTarget.TargetType> {
  @NotNull @NonNls private static final String BUILDER_NAME = "defrac jvm:package";

  public DefracJvmPackageBuilder() {
    super(DefracJvmPackageBuildTarget.TargetType.INSTANCE);
  }

  @NotNull
  @Override
  public String getPresentableName() {
    return BUILDER_NAME;
  }

  @Override
  protected void buildTarget(@NotNull final DefracJvmPackageBuildTarget target,
                             @NotNull final DirtyFilesHolder<BuildRootDescriptor, DefracJvmPackageBuildTarget> holder,
                             @NotNull final BuildOutputConsumer outputConsumer,
                             @NotNull final CompileContext context) throws ProjectBuildException, IOException {
    if(!packageJvm(target, context, holder.hasDirtyFiles() || holder.hasRemovedFiles(), outputConsumer)) {
      throw new StopBuildException();
    }
  }

  private boolean packageJvm(@NotNull final DefracJvmPackageBuildTarget target,
                             @NotNull final CompileContext context,
                             final boolean hasDirtyFiles,
                             @NotNull final BuildOutputConsumer outputConsumer) {

    CompileStepBeforeRun.RUN_CONFIGURATION
    final JpsModule module = target.getModule();
    final JpsDefracModuleExtension extension = DefracJpsUtil.getExtension(module);

    assert extension != null;
    assert !extension.isMacroLibrary();

    final DefracVersion defracVersion = DefracJpsUtil.getDefracVersion(module, context, BUILDER_NAME);

    if(defracVersion == null) {
      return false;
    }

    File outputDir = getOuptutDir(module);

    if(outputDir == null) {
      return false;
    }

    outputDir = DefracJpsUtil.createDirIfNotExist(outputDir, context, BUILDER_NAME);;

    if(outputDir == null) {
      return false;
    }

    return runDefrac(...); //TODO(joa): implement me
  }


}
