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

import com.google.common.collect.ImmutableSet;
import com.intellij.ide.IdeView;
import com.intellij.ide.actions.CreateFileAction;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import defrac.intellij.DefracPlatform;
import defrac.intellij.action.DefracAction;
import defrac.intellij.action.create.ui.MultiPlatformCreateDialog;
import defrac.intellij.config.DefracConfig;
import defrac.intellij.facet.DefracFacet;
import org.apache.velocity.runtime.parser.ParseException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;
import java.util.Set;

import static defrac.intellij.fileTemplate.DefracFileTemplateUtil.PLATFORM_ALL;
import static defrac.intellij.fileTemplate.DefracFileTemplateUtil.getPlatformKey;

/**
 *
 */
public abstract class CreateMultiplatformClassAction<T extends PsiElement> extends DefracAction {
  private static final Logger LOG = Logger.getInstance(CreateMultiplatformClassAction.class.getName());

  @Nullable
  public static PsiFile createFileFromTemplate(@Nullable String name,
                                               @NotNull FileTemplate template,
                                               @NotNull PsiDirectory dir,
                                               @Nullable String defaultTemplateProperty,
                                               @NotNull final DefracPlatform platform,
                                               @NotNull final Set<DefracPlatform> enabledPlatforms) {
    if(name != null) {
      final CreateFileAction.MkDirs mkdirs = new CreateFileAction.MkDirs(name, dir);
      name = mkdirs.newName;
      dir = mkdirs.directory;
    }

    PsiElement element;
    Project project = dir.getProject();

    try {
      final Properties properties = FileTemplateManager.getInstance().getDefaultProperties(project);

      //minus one for generic
      final boolean isAllPlatforms = enabledPlatforms.size() == (DefracPlatform.values().length - 1);

      for(final DefracPlatform enabledPlatform : enabledPlatforms) {
        properties.setProperty(getPlatformKey(enabledPlatform), Boolean.TRUE.toString());
      }

      if(isAllPlatforms) {
        properties.setProperty(PLATFORM_ALL, Boolean.TRUE.toString());
      }

      element =
          FileTemplateUtil.createFromTemplate(template, name, properties, dir);

      final PsiFile psiFile = element.getContainingFile();
      final VirtualFile virtualFile = psiFile.getVirtualFile();

      if(virtualFile != null) {
        if(platform.isGeneric()) {
          FileEditorManager.getInstance(project).openFile(virtualFile, true);
        }

        if(defaultTemplateProperty != null) {
          PropertiesComponent.getInstance(project).setValue(defaultTemplateProperty, template.getName());
        }

        return psiFile;
      }
    } catch(final ParseException parseException) {
      Messages.showErrorDialog(project, "Error parsing Velocity template: "+parseException.getMessage(), "Create File from Template");
      return null;
    } catch(final IncorrectOperationException incorrectOperation) {
      throw incorrectOperation;
    } catch(final Exception exception) {
      LOG.error(exception);
    }

    return null;
  }

  public CreateMultiplatformClassAction(Condition<AnActionEvent> condition) {
    super(condition);
  }

  @Override
  public void actionPerformed(@NotNull final AnActionEvent event) {
    final DataContext dataContext = event.getDataContext();

    final IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
    if(view == null) {
      return;
    }

    final Project project = event.getProject();
    if(project == null) {
      return;
    }

    final DefracFacet facet = getFacet(event);
    final DefracConfig config = facet.getConfig();
    if(config == null) {
      return;
    }

    final PsiDirectory dir = view.getOrChooseDirectory();
    if(dir == null) {
      return;
    }

    //TODO(joa): choose directories for platforms...

    final MultiPlatformCreateDialog<T> dialog =
        MultiPlatformCreateDialog.create(
            event.getProject(),
            ImmutableSet.copyOf(config.getTargets()),
            createGeneric(event, project, facet, config, dir),
            createAndroid(event, project, facet, config, dir),
            createIOS(event, project, facet, config, dir),
            createJVM(event, project, facet, config, dir),
            createWeb(event, project, facet, config, dir));

    final MultiPlatformCreateDialog.Result<T> result = dialog.getResult();

    if(result != null && result.generic != null) {
      view.selectElement(result.generic);
    }
  }

  @NotNull
  protected abstract MultiPlatformCreateDialog.Creator<T> createGeneric(@NotNull final AnActionEvent event,
                                                                        @NotNull final Project project,
                                                                        @NotNull final DefracFacet facet,
                                                                        @NotNull final DefracConfig config,
                                                                        @NotNull final PsiDirectory dir);

  @NotNull
  protected MultiPlatformCreateDialog.Creator<T> createAndroid(@NotNull final AnActionEvent event,
                                                               @NotNull final Project project,
                                                               @NotNull final DefracFacet facet,
                                                               @NotNull final DefracConfig config,
                                                               @NotNull final PsiDirectory dir) {
    return MultiPlatformCreateDialog.nullCreator();
  }

  @NotNull
  protected MultiPlatformCreateDialog.Creator<T> createIOS(@NotNull final AnActionEvent event,
                                                           @NotNull final Project project,
                                                           @NotNull final DefracFacet facet,
                                                           @NotNull final DefracConfig config,
                                                           @NotNull final PsiDirectory dir) {
    return MultiPlatformCreateDialog.nullCreator();
  }

  @NotNull
  protected MultiPlatformCreateDialog.Creator<T> createJVM(@NotNull final AnActionEvent event,
                                                           @NotNull final Project project,
                                                           @NotNull final DefracFacet facet,
                                                           @NotNull final DefracConfig config,
                                                           @NotNull final PsiDirectory dir) {
    return MultiPlatformCreateDialog.nullCreator();
  }

  @NotNull
  protected MultiPlatformCreateDialog.Creator<T> createWeb(@NotNull final AnActionEvent event,
                                                           @NotNull final Project project,
                                                           @NotNull final DefracFacet facet,
                                                           @NotNull final DefracConfig config,
                                                           @NotNull final PsiDirectory dir) {
    return MultiPlatformCreateDialog.nullCreator();
  }
}
