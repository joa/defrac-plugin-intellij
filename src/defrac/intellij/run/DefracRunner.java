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

package defrac.intellij.run;

import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.openapi.module.Module;
import defrac.intellij.DefracBundle;
import defrac.intellij.DefracPlatform;
import defrac.intellij.facet.DefracFacet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public final class DefracRunner extends DefaultProgramRunner {
  @NotNull @NonNls public static final String RUNNER_ID = "DefracRunner";

  @NotNull
  @Override
  public String getRunnerId() {
    return RUNNER_ID;
  }

  @Override
  public boolean canRun(@NotNull final String executorId, @NotNull final RunProfile profile) {
    if(profile instanceof DefracRunConfiguration) {
      final DefracRunConfiguration runConfiguration = (DefracRunConfiguration) profile;
      final Module module = runConfiguration.getConfigurationModule().getModule();

      if(module == null) {
        return false;
      }

      final DefracFacet facet = DefracFacet.getInstance(module);
      assert facet != null : DefracBundle.message("facet.error.facetMissing", module.getName());

      return facet.getPlatform() != DefracPlatform.JVM
          && !DefaultDebugExecutor.EXECUTOR_ID.equals(executorId)
          && facet.getPlatform().isAvailableOnHostOS();
    }

    return false;
  }
}
