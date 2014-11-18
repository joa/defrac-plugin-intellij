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

package defrac.intellij.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.JdkOrderEntry;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import defrac.intellij.DefracPlatform;
import defrac.intellij.jps.model.impl.JpsDefracModuleProperties;
import defrac.intellij.sdk.DefracVersion;
import defrac.intellij.util.Names;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

import static defrac.intellij.sdk.DefracSdkUtil.isDefracSdk;

/**
 *
 */
public final class DefracFacet extends Facet<DefracFacetConfiguration> {
  @NotNull
  public static final FacetTypeId<DefracFacet> ID = new FacetTypeId<DefracFacet>("DEFRAC");

  @NotNull
  public static DefracFacetType getFacetType() {
    return (DefracFacetType)FacetTypeRegistry.getInstance().findFacetType(ID);
  }

  @Nullable
  public static DefracFacet getInstance(@Nullable Module module) {
    return module == null ? null : FacetManager.getInstance(module).getFacetByType(ID);
  }

  @Nullable
  public static DefracFacet getInstance(@NotNull ConvertContext context) {
    Module module = context.getModule();
    return module != null ? getInstance(module) : null;
  }

  @Nullable
  public static DefracFacet getInstance(@Nullable final PsiElement element) {
    return element == null ? null : getInstance(element.getContainingFile());
  }

  @Nullable
  public static DefracFacet getInstance(@Nullable final PsiFile file) {
    if(file == null) {
      return null;
    }

    Module module = ModuleUtil.findModuleForPsiElement(file);

    if(module == null) {
      final PsiDirectory directory = file.getParent();

      if(directory != null) {
        module = ModuleUtil.findModuleForPsiElement(directory);
      }
    }

    return getInstance(module);
  }

  @Nullable
  public static DefracFacet getInstance(@NotNull DomElement element) {
    final Module module = element.getModule();

    if(module == null) {
      return null;
    }

    return getInstance(module);
  }

  public static boolean isInDefracSdk(@NotNull final PsiElement element) {
    final PsiFile psiFile = element.getContainingFile();

    if(psiFile == null) {
      return false;
    }

    final VirtualFile file = psiFile.getVirtualFile();

    if(file == null) {
      return false;
    }

    final ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(element.getProject()).getFileIndex();
    final List<OrderEntry> entries = projectFileIndex.getOrderEntriesForFile(file);

    for(final OrderEntry entry : entries) {
      if(entry instanceof JdkOrderEntry) {
        final Sdk sdk = ((JdkOrderEntry)entry).getJdk();

        if(isDefracSdk(sdk)) {
          return true;
        }
      }
    }

    return false;
  }

  public DefracFacet(@NotNull final Module module, final String name,
                      final @NotNull DefracFacetConfiguration configuration) {
    super(getFacetType(), module, name, configuration, null);
    configuration.setFacet(this);
  }

  @NotNull
  private JpsDefracModuleProperties getProperties() {
    return getConfiguration().getState();
  }

  @NotNull
  public File getSettingsFile() {
    final Module module = getModule();
    final Project project = module.getProject();
    final VirtualFile settingsFile = DefracRootUtil.getFileByRelativeProjectPath(project, getProperties().SETTINGS_FILE_RELATIVE_PATH);

    if(settingsFile == null) {
      return new File(Names.default_settings);
    }

    return VfsUtilCore.virtualToIoFile(settingsFile);
  }

  @NotNull
  public DefracPlatform getPlatform() {
    return getConfiguration().getPlatform();
  }

  @Nullable
  public DefracVersion getDefracVersion() {
    return getConfiguration().getDefracVersion();
  }

  @Nullable
  public Sdk getDefracSdk() {
    return getConfiguration().getDefracSdk();
  }
}
