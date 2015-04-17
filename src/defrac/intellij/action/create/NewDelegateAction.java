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

package defrac.intellij.action.create;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Conditions;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import defrac.intellij.DefracPlatform;
import defrac.intellij.action.create.ui.MultiPlatformCreateDialog;
import defrac.intellij.config.DefracConfig;
import defrac.intellij.facet.DefracFacet;
import defrac.intellij.fileTemplate.DefracFileTemplateProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 *
 */
public class NewDelegateAction extends CreateMultiplatformClassAction<PsiFile> implements DumbAware {
  public NewDelegateAction() {
    super(Conditions.and(IS_GENERIC, IS_IN_SOURCE));
  }

  @NotNull
  @Override
  protected MultiPlatformCreateDialog.Creator<PsiFile> createGeneric(@NotNull final AnActionEvent event,
                                                                      @NotNull final Project project,
                                                                      @NotNull final DefracFacet facet,
                                                                      @NotNull final DefracConfig config,
                                                                      @NotNull final PsiDirectory dir) {
    return new MultiPlatformCreateDialog.Creator<PsiFile>() {
      @Nullable
      @Override
      public PsiFile createElement(@NotNull final String name,
                                    @NotNull final DefracPlatform platform,
                                    @NotNull final Set<DefracPlatform> enabledPlatforms) {
        return createFileFromTemplate(
            name,
            FileTemplateManager.getInstance().getInternalTemplate(DefracFileTemplateProvider.DELEGATE),
            dir, null, platform, enabledPlatforms);
      }
    };
  }
}
