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

package l2server.loginserver.network.serverpackets;

import l2server.Config;
import l2server.log.Log;

import java.util.logging.Logger;

/**
 * Fromat: d
 * d: response
 */
public final class GGAuth extends L2LoginServerPacket {
	static final Logger log = Logger.getLogger(GGAuth.class.getName());
	public static final int SKIP_GG_AUTH_REQUEST = 0x0b;
	
	private int response;
	
	public GGAuth(int response) {
		this.response = response;
		if (Config.DEBUG) {
			Log.warning("Reason Hex: " + Integer.toHexString(response));
		}
	}
	
	/**
	 */
	@Override
	protected void write() {
		writeC(0x0b);
		writeD(response);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
		writeD(0x00);
	}
}
