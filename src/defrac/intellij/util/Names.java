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

package defrac.intellij.util;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 *
 */
public final class Names {
  @NotNull @NonNls public static final String defrac_annotation_Delegate = "defrac.annotation.Delegate";
  @NotNull @NonNls public static final String defrac_annotation_DelegateA5D = "defrac.annotation.DelegateA5D";
  @NotNull @NonNls public static final String defrac_annotation_DelegateIOS = "defrac.annotation.DelegateIOS";
  @NotNull @NonNls public static final String defrac_annotation_DelegateJVM = "defrac.annotation.DelegateJVM";
  @NotNull @NonNls public static final String defrac_annotation_DelegateWeb = "defrac.annotation.DelegateWeb";
  @NotNull @NonNls public static final String defrac_annotation_Macro = "defrac.annotation.Macro";
  @NotNull @NonNls public static final String defrac_annotation_MacroA5D = "defrac.annotation.MacroA5D";
  @NotNull @NonNls public static final String defrac_annotation_MacroIOS = "defrac.annotation.MacroIOS";
  @NotNull @NonNls public static final String defrac_annotation_MacroJVM = "defrac.annotation.MacroJVM";
  @NotNull @NonNls public static final String defrac_annotation_MacroWeb = "defrac.annotation.MacroWeb";

  @NotNull @NonNls public static final String defrac_macro_Macro = "defrac.macro.Macro";

  @NotNull public static final String[] ALL_DELEGATES = {
      defrac_annotation_Delegate,
      defrac_annotation_DelegateA5D,
      defrac_annotation_DelegateIOS,
      defrac_annotation_DelegateJVM,
      defrac_annotation_DelegateWeb
  };

  @NotNull public static final String[] ALL_MACROS = {
      defrac_annotation_Macro,
      defrac_annotation_MacroA5D,
      defrac_annotation_MacroIOS,
      defrac_annotation_MacroJVM,
      defrac_annotation_MacroWeb
  };

  private Names() {}
}