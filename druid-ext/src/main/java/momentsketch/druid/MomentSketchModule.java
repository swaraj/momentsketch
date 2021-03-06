/*
 * Copyright 2016 Imply Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package momentsketch.druid;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import io.druid.initialization.DruidModule;
import io.druid.segment.serde.ComplexMetrics;
import momentsketch.druid.aggregator.MomentSketchAggregatorFactory;
import momentsketch.druid.aggregator.MomentSketchMergeAggregatorFactory;
import momentsketch.druid.aggregator.MomentSketchQuantilePostAggregator;

import java.util.Arrays;
import java.util.List;

public class MomentSketchModule implements DruidModule {
  @Override
  public List<? extends Module> getJacksonModules()
  {
    return ImmutableList.of(
        new SimpleModule(getClass().getSimpleName()
        ).registerSubtypes(
                new NamedType(
                        MomentSketchAggregatorFactory.class,
                        MomentSketchAggregatorFactory.TYPE_NAME),
                new NamedType(
                        MomentSketchMergeAggregatorFactory.class,
                        MomentSketchMergeAggregatorFactory.TYPE_NAME),
                new NamedType(
                        MomentSketchQuantilePostAggregator.class,
                        MomentSketchQuantilePostAggregator.TYPE_NAME)
        ).addSerializer(
                MomentSketchWrapper.class, new MomentSketchJsonSerializer()
        )
    );
  }

  @Override
  public void configure(Binder binder)
  {
    String typeName = MomentSketchAggregatorFactory.TYPE_NAME;
    if (ComplexMetrics.getSerdeForType(typeName) == null) {
      ComplexMetrics.registerSerde(typeName, new MomentSketchComplexMetricSerde());
    }
  }
}
