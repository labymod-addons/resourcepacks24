/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.labymod.addons.resourcepacks24.core.util;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.function.Consumer;
import net.labymod.api.util.io.web.request.Response;
import net.labymod.api.util.io.web.request.types.FileRequest;

public class DownloadProcess {

  private final FileRequest request;
  private State state;
  private Response<Path> response;
  private Consumer<DownloadProcess> callback;

  public DownloadProcess(FileRequest request) {
    this.request = request;
    this.state = State.NONE;
  }

  public void start() {
    this.setState(State.DOWNLOADING);
    this.request.execute(response -> {
      this.response = response;
      if (!response.isPresent()) {
        if (response.hasException() && response.exception()
            .getCause() instanceof FileAlreadyExistsException) {
          this.setState(State.FINISHED);
        } else {
          this.setState(State.FAILED);
        }

        return;
      }

      this.setState(State.FINISHED);
    });
  }

  public State state() {
    return this.state;
  }

  public Response<Path> getResponse() {
    return this.response;
  }

  public void setCallback(Consumer<DownloadProcess> callback) {
    this.callback = callback;
  }

  private void setState(State state) {
    this.state = state;
    if (this.callback != null) {
      this.callback.accept(this);
    }
  }

  public enum State {
    NONE,
    DOWNLOADING,
    FINISHED,
    FAILED
  }
}
