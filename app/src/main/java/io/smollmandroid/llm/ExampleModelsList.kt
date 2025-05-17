/*
 * Copyright (C) 2024 Shubham Panchal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.smollmandroid.llm

import io.smollmandroid.data.LLMModel

/**
 * A list of models that are shown in the DownloadModelActivity for the user to quickly get started
 * by downloading a model.
 */
val exampleModelsList =
    listOf(
        LLMModel(
            name = "Local Model 1",
            url = "file:///path/to/local/model1.gguf",
        ),
        LLMModel(
            name = "Local Model 2",
            url = "file:///path/to/local/model2.gguf",
        ),
        LLMModel(
            name = "Local Model 3",
            url = "file:///path/to/local/model3.gguf",
        ),
        LLMModel(
            name = "Local Model 4",
            url = "file:///path/to/local/model4.gguf",
        ),
        LLMModel(
            name = "Local Model 5",
            url = "file:///path/to/local/model5.gguf",
        ),
    )
