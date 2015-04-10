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

package defrac.intellij.jps.model;

import com.intellij.util.xmlb.XmlSerializer;
import defrac.intellij.util.Names;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.serialization.JpsModelSerializerExtension;
import org.jetbrains.jps.model.serialization.JpsProjectExtensionSerializer;
import org.jetbrains.jps.model.serialization.artifact.JpsArtifactPropertiesSerializer;
import org.jetbrains.jps.model.serialization.artifact.JpsPackagingElementSerializer;
import org.jetbrains.jps.model.serialization.facet.JpsFacetConfigurationSerializer;
import org.jetbrains.jps.model.serialization.library.JpsSdkPropertiesSerializer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class JpsDefracModelSerializerExtension extends JpsModelSerializerExtension {
  @SuppressWarnings("unchecked")
  private static final List<? extends JpsFacetConfigurationSerializer<JpsDefracModuleExtension>> FACET_PROPERTIES_LOADERS =
      Arrays.asList(new JpsFacetConfigurationSerializer<JpsDefracModuleExtension>(JpsDefracModuleExtension.KIND,
          Names.FACET_TYPE_ID,
          Names.FACET_NAME) {
        @Override
        public JpsDefracModuleExtension loadExtension(@NotNull final Element facetConfigurationElement,
                                                      final String name,
                                                      final JpsElement parent,
                                                      final JpsModule module) {
          final JpsDefracModuleProperties properties =
              XmlSerializer.deserialize(facetConfigurationElement, JpsDefracModuleProperties.class);

          assert properties != null;

          return new JpsDefracModuleExtension(properties);
        }
        @Override
        protected void saveExtension(JpsDefracModuleExtension extension, Element facetConfigurationTag, JpsModule module) {
          XmlSerializer.serializeInto(extension.getProperties(), facetConfigurationTag);
        }
      });

  private static final JpsSdkPropertiesSerializer<JpsSimpleElement<JpsDefracSdkProperties>> SDK_PROPERTIES_LOADER =
      new JpsSdkPropertiesSerializer<JpsSimpleElement<JpsDefracSdkProperties>>("defrac SDK", JpsDefracSdkType.INSTANCE) {
        @NotNull
        @Override
        public JpsSimpleElement<JpsDefracSdkProperties> loadProperties(@Nullable final Element propertiesElement) {
          final String buildTarget;
          final String jdkName;

          if(propertiesElement != null) {
            buildTarget = propertiesElement.getAttributeValue("sdk");
            jdkName = propertiesElement.getAttributeValue("jdk");
          } else {
            buildTarget = null;
            jdkName = null;
          }

          return JpsElementFactory.getInstance().createSimpleElement(new JpsDefracSdkProperties(buildTarget, jdkName));
        }

        @Override
        public void saveProperties(@NotNull final JpsSimpleElement<JpsDefracSdkProperties> properties,
                                   @NotNull final Element element) {
          final String jdkName = properties.getData().getJdkName();

          if(jdkName != null) {
            element.setAttribute("jdk", jdkName);
          }

          final String buildTarget = properties.getData().getDefracVersionName();

          if(buildTarget != null) {
            element.setAttribute("sdk", buildTarget);
          }
        }
      };

  @NotNull
  @Override
  public List<? extends JpsFacetConfigurationSerializer<?>> getFacetConfigurationSerializers() {
    return FACET_PROPERTIES_LOADERS;
  }

  @NotNull
  @Override
  public List<? extends JpsPackagingElementSerializer<?>> getPackagingElementSerializers() {
    return Collections.emptyList();
  }

  @NotNull
  @Override
  public List<? extends JpsArtifactPropertiesSerializer<?>> getArtifactTypePropertiesSerializers() {
    return Collections.emptyList();
  }

  @NotNull
  @Override
  public List<? extends JpsProjectExtensionSerializer> getProjectExtensionSerializers() {
    return Collections.emptyList();
  }

  @SuppressWarnings("unchecked")
  @NotNull
  @Override
  public List<? extends JpsSdkPropertiesSerializer<?>> getSdkPropertiesSerializers() {
    return Arrays.asList(SDK_PROPERTIES_LOADER);
  }
}
