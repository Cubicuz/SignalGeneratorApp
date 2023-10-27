/*
 * Copyright 2022 The Android Open Source Project
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
package com.example.signalgeneratorapp.IconCopy

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

val SaveAs: ImageVector
    get() {
        if (_saveAs != null) {
            return _saveAs!!
        }
        _saveAs = materialIcon(name = "Filled.SaveAs") {
            materialPath {
                moveTo(21.0f, 12.4f)
                verticalLineTo(7.0f)
                lineToRelative(-4.0f, -4.0f)
                horizontalLineTo(5.0f)
                curveTo(3.89f, 3.0f, 3.0f, 3.9f, 3.0f, 5.0f)
                verticalLineToRelative(14.0f)
                curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(7.4f)
                lineTo(21.0f, 12.4f)
                close()
                moveTo(15.0f, 15.0f)
                curveToRelative(0.0f, 1.66f, -1.34f, 3.0f, -3.0f, 3.0f)
                reflectiveCurveToRelative(-3.0f, -1.34f, -3.0f, -3.0f)
                reflectiveCurveToRelative(1.34f, -3.0f, 3.0f, -3.0f)
                reflectiveCurveTo(15.0f, 13.34f, 15.0f, 15.0f)
                close()
                moveTo(6.0f, 6.0f)
                horizontalLineToRelative(9.0f)
                verticalLineToRelative(4.0f)
                horizontalLineTo(6.0f)
                verticalLineTo(6.0f)
                close()
                moveTo(19.99f, 16.25f)
                lineToRelative(1.77f, 1.77f)
                lineTo(16.77f, 23.0f)
                horizontalLineTo(15.0f)
                verticalLineToRelative(-1.77f)
                lineTo(19.99f, 16.25f)
                close()
                moveTo(23.25f, 16.51f)
                lineToRelative(-0.85f, 0.85f)
                lineToRelative(-1.77f, -1.77f)
                lineToRelative(0.85f, -0.85f)
                curveToRelative(0.2f, -0.2f, 0.51f, -0.2f, 0.71f, 0.0f)
                lineToRelative(1.06f, 1.06f)
                curveTo(23.45f, 16.0f, 23.45f, 16.32f, 23.25f, 16.51f)
                close()
            }
        }
        return _saveAs!!
    }

private var _saveAs: ImageVector? = null
