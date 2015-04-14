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
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import defrac.intellij.DefracBundle;
import defrac.intellij.jps.model.JpsDefracModuleExtension;
import defrac.intellij.jps.model.JpsDefracSdkProperties;
import defrac.intellij.jps.model.JpsDefracSdkType;
import defrac.intellij.sdk.DefracVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
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

  @Nullable
  public static File getDefracExecutable(@NotNull final JpsModule module,
                                         @Nullable final CompileContext context,
                                         @NotNull final String builderName) {
    final JpsSdk<JpsSimpleElement<JpsDefracSdkProperties>> sdk = module.getSdk(JpsDefracSdkType.INSTANCE);

    if(sdk == null) {
      if(context != null) {
        context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR,
            DefracBundle.message("jps.error.noSDK", module.getName())));
      }

      return null;
    }

    final File executable = new File(sdk.getHomePath(), nameOfExecutable());

    if(!executable.exists()) {
      if(context != null) {
        context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR,
            DefracBundle.message("jps.error.cannotFindExecutable", sdk.getHomePath(), nameOfExecutable())));
      }

      return null;
    }

    if(!executable.canExecute()) {
      if(context != null) {
        context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR,
            DefracBundle.message("jps.error.cannotExecute", executable.getAbsolutePath())));
      }

      return null;
    }

    return executable;
  }

  @NotNull
  public static String nameOfExecutable() {
    return "defrac"+(SystemInfo.isWindows ? ".bat" : "");
  }

  @Nullable
  public static DefracVersion getDefracVersion(@NotNull final JpsModule module,
                                               @Nullable final CompileContext context,
                                               @NotNull final String builderName) {
    final JpsSdk<JpsSimpleElement<JpsDefracSdkProperties>> sdk = module.getSdk(JpsDefracSdkType.INSTANCE);

    if(sdk == null) {
      if(context != null) {
        context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR,
            DefracBundle.message("jps.error.noSDK", module.getName())));
      }

      return null;
    }

    return getDefracVersion(sdk, context, builderName);
  }

  @Nullable
  public static DefracVersion getDefracVersion(@NotNull final JpsSdk<JpsSimpleElement<JpsDefracSdkProperties>> sdk,
                                               @Nullable final CompileContext context,
                                               @NotNull final String builderName) {
    final JpsDefracSdkProperties sdkProperties = sdk.getSdkProperties().getData();
    final String versionName = sdkProperties.getDefracVersionName();

    if(isNullOrEmpty(versionName)) {
      if(context != null) {
        context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR,
            DefracBundle.message("jps.error.cannotParseSDK", sdk.getParent().getName())));
      }

      return null;
    }

    final DefracVersion result = findVersion(sdk, context, versionName, builderName);
    if(result == null) {
      if(context != null) {
        context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR,
            "Cannot parse SDK '" + sdk.getParent().getName() +
                "': unknown target " + versionName));
      }

      return null;
    }

    return result;
  }

  @Nullable
  public static DefracVersion findVersion(@NotNull final JpsSdk<JpsSimpleElement<JpsDefracSdkProperties>> sdk,
                                          @Nullable final CompileContext context,
                                          @NotNull final String versionName,
                                          @NotNull final String builderName) {
    if(isNullOrEmpty(versionName)) {
      if(context != null) {
        context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR,
            DefracBundle.message("jps.error.emptyVersion")));
      }
    }

    final File sdkDir = new File(sdk.getHomePath(), "sdk");

    if(!sdkDir.isDirectory()) {
      if(context != null) {
        context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR,
            DefracBundle.message("jps.error.noSdkDir")));
      }
    }

    final File location = new File(sdkDir, DefracVersion.mapToCurrent(versionName));

    if(!location.isDirectory()) {
      if(context != null) {
        context.processMessage(new CompilerMessage(builderName, BuildMessage.Kind.ERROR,
            DefracBundle.message("jps.error.noSuchVersion", versionName)));
      }
    }

    return new DefracVersion(versionName, location);
  }

  @Nullable
  public static File createDirIfNotExist(@NotNull File dir,
                                         @NotNull CompileContext context,
                                         @NotNull String compilerName) {
    if(!dir.exists()) {
      if(!dir.mkdirs()) {
        context.processMessage(new CompilerMessage(compilerName, BuildMessage.Kind.ERROR,
            DefracBundle.message("jps.error.cannotMkdirs", dir.getPath())));
        return null;
      }
    }

    return dir;
  }

  private DefracJpsUtil() {}
}
