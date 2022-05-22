package ghidrancdmeasures;

import java.awt.BorderLayout;
import java.io.File;
import java.util.List;

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
import resources.Icons;

class NCDProvider extends ComponentProvider {

	private JPanel panel;
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
		JTextArea textArea = new JTextArea(5, 25);
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
				
				List<File> files = fileChooser.getSelectedFiles();
				System.out.println("Selected " + files.size() + " files!");
				
				try {
					service.compute(files);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
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