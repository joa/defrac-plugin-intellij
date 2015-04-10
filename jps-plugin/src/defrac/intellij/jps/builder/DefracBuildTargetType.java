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
import defrac.intellij.jps.DefracJpsUtil;
import defrac.intellij.jps.model.JpsDefracModuleExtension;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.builders.BuildTargetLoader;
import org.jetbrains.jps.builders.BuildTargetType;
import org.jetbrains.jps.model.JpsModel;
import org.jetbrains.jps.model.module.JpsModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public abstract class DefracBuildTargetType<T extends DefracBuildTarget> extends BuildTargetType<T> {
  @NotNull
  private final String displayName;

  @NotNull
  private final DefracPlatform platform;

  DefracBuildTargetType(@NotNull final String typeId,
                        @NotNull final String displayName,
                        @NotNull final DefracPlatform platform) {
    super(typeId);
    this.displayName = displayName;
    this.platform = platform;
  }

  @Contract("null -> false")
  public boolean isSuitablePlatform(@Nullable final DefracPlatform platform) {
    return this.platform == platform;
  }

  @NotNull
  public String getDisplayName() {
    return displayName;
  }

  @NotNull
  @Override
  public List<T> computeAllTargets(@NotNull final JpsModel model) {
    final List<T> targets = new ArrayList<T>();

    for(final JpsModule module : model.getProject().getModules()) {
      final JpsDefracModuleExtension extension = DefracJpsUtil.getExtension(module);

      if(extension != null) {
        final T target = createBuildTarget(extension);

        if(target != null) {
          targets.add(target);
        }
      }
    }

    return targets;
  }

  @NotNull
  @Override
  public BuildTargetLoader<T> createLoader(@NotNull final JpsModel model) {
    final HashMap<String, T> targetMap = new HashMap<String, T>();

    for(final T target : computeAllTargets(model)) {
      targetMap.put(target.getId(), target);
    }

    return new BuildTargetLoader<T>() {
      @Nullable
      @Override
      public T createTarget(@NotNull String targetId) {
        return targetMap.get(targetId);
      }
    };
  }

  @Nullable
  public abstract T createBuildTarget(@NotNull final JpsDefracModuleExtension extension);

  @NotNull
  public DefracPlatform getPlatform() {
    return platform;
  }
}
