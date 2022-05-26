package ghidrancdmeasures;

import java.awt.BorderLayout;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import docking.ActionContext;
import docking.ComponentProvider;
import docking.action.DockingAction;
import docking.action.ToolBarData;
import docking.widgets.filechooser.GhidraFileChooser;
import ghidra.framework.plugintool.Plugin;
import ghidra.util.filechooser.GhidraFileFilter;
import ghidrancdmeasures.NCDResult.NCDPairResult;
import resources.Icons;

class NCDProvider extends ComponentProvider {

	private JPanel panel;
	private JTextArea textArea;
	private DockingAction action;
	
	private NCDService service;

	public NCDProvider(Plugin plugin, String owner) {
		super(plugin.getTool(), owner, owner);
		buildPanel();
		createActions();
	}


	// Customize GUI
	private void buildPanel() {
		panel = new JPanel(new BorderLayout());
		textArea = new JTextArea(5, 25);
		textArea.setEditable(false);
		panel.add(new JScrollPane(textArea));
		setVisible(true);
	}

	private void createActions() {
		action = new DockingAction("Select Files", getName()) {
			@Override
			public void actionPerformed(ActionContext context) {
				GhidraFileChooser fileChooser = new GhidraFileChooser(getComponent());
				fileChooser.setMultiSelectionEnabled(true);
				fileChooser.setSelectedFileFilter(GhidraFileFilter.ALL);
				
				List<Path> files = fileChooser.getSelectedFiles()
						.stream().map( f -> f.toPath() ).collect(Collectors.toList());
				System.out.println("Selected " + files.size() + " files!");
				
				NCDResult results = service.compute(files);
				
				String dump = "";
				for (NCDPairResult pair: results.getPairs()) {
					dump += pair.getP1().getFileName().toString() + " / " + pair.getP2().getFileName().toString() + "\t==> " + pair.getSimilarity() + "\n";
				}
				
				textArea.setText(dump);
			}
		};
		action.setToolBarData(new ToolBarData(Icons.ADD_ICON, null));
		action.setEnabled(true);
		action.markHelpUnnecessary();
		dockingTool.addLocalAction(this, action);
	}

	@Override
	public JComponent getComponent() {
		return panel;
	}


	public NCDService getService() {
		return service;
	}


	public void setService(NCDService service) {
		this.service = service;
	}
}