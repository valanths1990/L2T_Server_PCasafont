/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package l2server.loginserver;

import l2server.Config;
import l2server.log.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KenM
 */
public class GameServerListener extends FloodProtectedListener {

	private static List<GameServerThread> gameServers = new ArrayList<>();

	public GameServerListener() throws IOException {
		super(Config.GAME_SERVER_LOGIN_HOST, Config.GAME_SERVER_LOGIN_PORT);
		setName(getClass().getSimpleName());
	}

	/**
	 * @see l2server.loginserver.FloodProtectedListener#addClient(java.net.Socket)
	 */
	@Override
	public void addClient(Socket s) {
		if (Config.DEBUG) {
			Log.info("Received gameserver connection from: " + s.getInetAddress().getHostAddress());
		}
		GameServerThread gst = new GameServerThread(s);
		gameServers.add(gst);
	}

	public void removeGameServer(GameServerThread gst) {
		gameServers.remove(gst);
	}
}
