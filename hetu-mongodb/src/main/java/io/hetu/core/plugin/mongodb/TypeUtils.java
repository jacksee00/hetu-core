/*
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
package io.hetu.core.plugin.mongodb;

import io.prestosql.spi.type.ArrayType;
import io.prestosql.spi.type.MapType;
import io.prestosql.spi.type.RowType;
import io.prestosql.spi.type.Type;

public final class TypeUtils
{
    private TypeUtils() {}

    public static boolean isArrayType(Type type)
    {
        return type instanceof ArrayType;
    }

    public static boolean isMapType(Type type)
    {
        return type instanceof MapType;
    }

    public static boolean isRowType(Type type)
    {
        return type instanceof RowType;
    }
}
