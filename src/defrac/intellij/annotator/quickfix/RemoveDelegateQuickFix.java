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

package defrac.intellij.annotator.quickfix;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import defrac.intellij.DefracPlatform;
import defrac.intellij.psi.DefracPsiUtil;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public final class RemoveDelegateQuickFix extends RemoveAnnotationQuickFix {
  @NotNull
  private final DefracPlatform platform;

  public RemoveDelegateQuickFix(@NotNull final PsiClass klass,
                                @NotNull final DefracPlatform platform) {
    super(klass);
    this.platform = platform;
  }

  @NotNull
  @Override
  public String getText() {
    final String name;

    //TODO(joa): ugly
    switch(platform) {
      case ANDROID: name = "A5D"; break;
      case IOS:     name = "IOS"; break;
      case JVM:     name = "JVM"; break;
      case WEB:     name = "Web"; break;
      default:      name =    ""; break;
    }

    return "Remove @Delegate"+name;
  }

  @Override
  public boolean isAvailable(@NotNull final Project project, final Editor editor, final PsiFile file) {
    return super.isAvailable(project, editor, file)
        && DefracPsiUtil.hasDelegate(element, platform);
  }

  @Override
  protected boolean isAnnotation(@NotNull final PsiAnnotation annotation) {
    return DefracPsiUtil.isDelegateAnnotation(annotation, platform);
  }
}
