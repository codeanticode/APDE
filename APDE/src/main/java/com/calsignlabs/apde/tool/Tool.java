package com.calsignlabs.apde.tool;

import java.lang.Runnable;

import android.view.MenuItem;

import com.calsignlabs.apde.APDE;
import com.calsignlabs.apde.KeyBinding;

public interface Tool extends Runnable {
	public void init(APDE context);
	
	/**
	 * @return the tool name to be displayed in the tool menu
	 */
	public String getMenuTitle();
	
	/**
	 * Returns a key binding that will run this tool.
	 * Return null for a tool that is not run with a key binding
	 * 
	 * @return the key binding
	 */
	public KeyBinding getKeyBinding();
	
	/**
	 * @return should this tool appear in the tools menu?
	 * 
	 * @param sketchLocation the location of the current sketch
	 */
	public boolean showInToolsMenu(APDE.SketchLocation sketchLocation);
	
	/**
	 * Returns a converted MenuItem for use in the selection Contextual Action Bar.
	 * Return false for a tool that does not appear in the selection CAB
	 * 
	 * @param convert the MenuItem to convert
	 * @return whether or not the MenuItem should appear in the selection CAB
	 */
	public boolean createSelectionActionModeMenuItem(MenuItem convert);


	/**
	 * Gets called automatically when the user responds to the permission request triggered by the
	 * tool and passes along the arguments received by onRequestPermissionsResult() in APDE's editor
	 * activity
	 *
	 * @param requestCode  the request code passed in the requestPermissions call
	 * @param permissions  the requested permissions
	 * @param grantResults the grant results for the corresponding permissions
	 */
	public void handlePermissionsResult(int requestCode, String permissions[], int[] grantResults);
}