package com.opcoach.compositepart.parts;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.renderers.swt.TrimmedPartLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;

public class SamplePart  extends PartInCompositeWithTopToolbar {

	private TableViewer tableViewer;

	@Inject
	private MPart part;
	
	@PostConstruct
	public void createComposite(Composite parent) {
		//parent.setLayout(new GridLayout(1, false));
		TrimmedPartLayout layout = new TrimmedPartLayout(parent);
		parent.setLayout(layout);
		Composite centerComposite = layout.clientArea;
		 centerComposite.setLayout(new GridLayout(1,false));
		
		
		Text txtInput = new Text(centerComposite, SWT.BORDER);
		txtInput.setMessage("Enter text to mark part as dirty");
		txtInput.addModifyListener(e -> part.setDirty(true));
		txtInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		tableViewer = new TableViewer(centerComposite);

		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(createInitialDataModel());
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		MTrimBar trimbar = part.getTrimBars().get(0);
		trimbar.getTransientData().put(IPresentationEngine.RENDERING_PARENT_KEY, parent);

	}
	
	
	@Inject
	@Optional
	private void showTrimbar(
			@UIEventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) Event event, MApplication app) {
		System.out.println("Showing trimbar");
		//part.getTrimBars().get(0).setVisible(true);
		part.getTrimBars().get(0).setToBeRendered(false);
		part.getTrimBars().get(0).setToBeRendered(true);
}

	@Focus
	public void setFocus() {
		tableViewer.getTable().setFocus();
	} 

	@Persist
	public void save() {
		part.setDirty(false);
	}
	
	private List<String> createInitialDataModel() {
		return Arrays.asList("Sample item 1", "Sample item 2", "Sample item 3", "Sample item 4", "Sample item 5");
	}
}