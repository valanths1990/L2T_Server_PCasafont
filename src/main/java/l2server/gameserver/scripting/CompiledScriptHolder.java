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

package l2server.gameserver.scripting;

import javax.script.CompiledScript;
import java.io.File;
import java.io.Serializable;

/**
 * @author KenM
 */
public class CompiledScriptHolder implements Serializable {
	/**
	 * Version 1
	 */
	private static final long serialVersionUID = 1L;
	
	private long lastModified;
	private long size;
	private CompiledScript compiledScript;
	
	/**
	 * @param compiledScript
	 * @param scriptFile
	 */
	public CompiledScriptHolder(CompiledScript compiledScript, File scriptFile) {
		this.compiledScript = compiledScript;
		lastModified = scriptFile.lastModified();
		size = scriptFile.length();
	}
	
	/**
	 * @return Returns the lastModified.
	 */
	public long getLastModified() {
		return lastModified;
	}
	
	/**
	 * @param lastModified The lastModified to set.
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	/**
	 * @return Returns the size.
	 */
	public long getSize() {
		return size;
	}
	
	/**
	 * @param size The size to set.
	 */
	public void setSize(long size) {
		this.size = size;
	}
	
	/**
	 * @return Returns the compiledScript.
	 */
	public CompiledScript getCompiledScript() {
		return compiledScript;
	}
	
	/**
	 * @param compiledScript The compiledScript to set.
	 */
	public void setCompiledScript(CompiledScript compiledScript) {
		this.compiledScript = compiledScript;
	}
	
	public boolean matches(File f) {
		return f.lastModified() == getLastModified() && f.length() == getSize();
	}
}
