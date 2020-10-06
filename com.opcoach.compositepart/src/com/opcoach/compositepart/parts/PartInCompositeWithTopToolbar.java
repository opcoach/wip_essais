package com.opcoach.compositepart.parts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.services.IServiceConstants;

/**
 * This part contains listeners to current active part and moves the toolbar
 * content into the parent composite toolbar if any.
 */
public class PartInCompositeWithTopToolbar {

	// The current part getting the focus
	@Inject
	private MPart currentPart;

	// initial toolbar of current parentComposite
	private MToolBar compositeToolbar;

	private List<MToolBarElement> movedElements;

	private boolean isInitialized = false;

	@Inject
	public void moveToolbarInCompositeIfNeeded(@Named(IServiceConstants.ACTIVE_PART) MPart newActivePart) {

		// Initialize once
		if (!isInitialized)
			initCompositeToolbar();

		// Nothing to do if no toolbar in composite part
		if (compositeToolbar == null || newActivePart == currentPart || newActivePart instanceof MCompositePart)
			return;

		// Current part is still the part that HAD the focus before new Activepart is
		// selected
		// Must remove the toolbar element back
		if (currentPart != null) {
			MToolBar originalToolbar = currentPart.getToolbar();
			// Must remove the moved elements from initial part to current composite toolbar
			for (MToolBarElement e : movedElements) {
				compositeToolbar.getChildren().remove(e);
				originalToolbar.getChildren().add(e);
			}
			movedElements.clear();
		}

		// Can switch the parts... and move the toolbar elements...
		if (newActivePart != null) currentPart = newActivePart;
		System.out.println("New active part is now : " + currentPart);
		MToolBar currentPartToolbar = currentPart.getToolbar();
		if (currentPartToolbar != null) {
			System.out.println("Moving toolbar elements... ");
			// Move items in part toolbar in composite toolbar... First copy to avoid
			// concurrentModification
			for (MToolBarElement e : currentPartToolbar.getChildren()) {
				movedElements.add(e);
			}
			for (MToolBarElement e : movedElements) {
				currentPartToolbar.getChildren().remove(e);
				compositeToolbar.getChildren().add(0,e);  // add in front
			}
		}
	}

	public void initCompositeToolbar() {
		MElementContainer<MUIElement> parent = currentPart.getParent();
		MCompositePart parentComposite = null;
		while (parentComposite == null && parent != null) {
			if (parent instanceof MPart) {
				MPart parentPart = (MPart) parent;
				if (parentPart instanceof MCompositePart) {
					parentComposite = (MCompositePart) parentPart;
					System.out.println("Found a composite for current Part : " + parentComposite.toString());
					compositeToolbar = parentComposite.getToolbar();
					movedElements = new ArrayList<>();
				}

			}

			parent = parent.getParent();
		}
		isInitialized = true;
	}

}
