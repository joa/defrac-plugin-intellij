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

import com.intellij.openapi.actionSystem.AnActionEvent;
import defrac.intellij.action.DefracAction;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public class NewMacroAction extends DefracAction {
  public NewMacroAction() {
    super(IS_GENERIC);
  }

  @Override
  public void actionPerformed(@NotNull final AnActionEvent event) {
    // TODO: insert action logic here
  }
}
