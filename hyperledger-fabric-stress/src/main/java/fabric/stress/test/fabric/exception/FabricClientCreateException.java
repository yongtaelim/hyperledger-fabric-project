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

package fabric.stress.test.fabric.exception;

/**
 * Fabric client creation exception
 */
public class FabricClientCreateException extends Exception {
    public FabricClientCreateException(String message, Throwable parent) {
        super(message, parent);
    }

    public FabricClientCreateException(String message) {
        super(message);
    }

    public FabricClientCreateException(Throwable parent) {
        super(parent);
    }
}
