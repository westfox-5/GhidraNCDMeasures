/* ###
 * IP: GHIDRA
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
package ghidrancdmeasures;

import ghidra.app.plugin.PluginCategoryNames;
import ghidra.app.plugin.ProgramPlugin;
import ghidra.framework.plugintool.PluginInfo;
import ghidra.framework.plugintool.PluginTool;
import ghidra.framework.plugintool.util.PluginStatus;

/**
 * Normalized Compression Distance measure calculator
 * 
 * @author westfox5
 *
 */
//@formatter:off
@PluginInfo(
	status = PluginStatus.UNSTABLE,
	packageName = "GhidraNCDMeasures",
	category = PluginCategoryNames.ANALYSIS,
	shortDescription = "Plugin short description goes here.",
	description = "Plugin long description goes here."
)
//@formatter:on
public class NCDPlugin extends ProgramPlugin {

	NCDProvider provider;

	NCDService service;
	
	/**
	 * Plugin constructor.
	 * 
	 * @param tool The plugin tool that this plugin is added to.
	 */
	public NCDPlugin(PluginTool tool) {
		super(tool, true, true);

		String pluginName = getName();
		provider = new NCDProvider(this, pluginName);
	}

	@Override
	public void init() {
		super.init();
		
		this.service = new NCDService();
		this.provider.setService(service);
	}
}
