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

package defrac.intellij.config.ios;

import defrac.intellij.config.DefracConfigurationBase;

/**
 *
 */
@SuppressWarnings("unused,MismatchedReadAndWriteOfArray")
public final class IOSSettings extends DefracConfigurationBase {
  protected IOSDeploy deploy;
  protected String platform;
  protected SigningSettings signing;
  protected String deviceTypeId;
  protected XCodeSettings xcode;
}
