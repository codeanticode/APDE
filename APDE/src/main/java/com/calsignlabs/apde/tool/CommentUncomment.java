package com.calsignlabs.apde.tool;


import android.annotation.SuppressLint;
import android.view.MenuItem;

import com.calsignlabs.apde.APDE;
import com.calsignlabs.apde.CodeEditText;
import com.calsignlabs.apde.KeyBinding;
import com.calsignlabs.apde.R;

public class CommentUncomment implements Tool {
	public static final String PACKAGE_NAME = "com.calsignlabs.apde.tool.CommentUncomment";
	
	private APDE context;
	
	@Override
	public void init(APDE context) {
		this.context = context;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void run() {
		if(!context.isExample()) {
			CodeEditText code = context.getCodeArea();
			
			String codeText = code.getText().toString();
			
			boolean trailingNewline = codeText.charAt(codeText.length() - 1) == '\n';
			
			String[] lines = codeText.split("\n");
			
			int startLine = code.lineForOffset(code.getSelectionStart());
			int endLine = code.lineForOffset(code.getSelectionEnd()) + 1;
			
			String[] toComment = new String[endLine - startLine];
			System.arraycopy(lines, startLine, toComment, 0, endLine - startLine);
			
			boolean commenting = false;
			
			for(String line : toComment) {
				if(!line.startsWith("//")) {
					commenting = true;
					break;
				}
			}
			
			//One branch, two loops
			//(Theoretically) faster than one loop, two branches?
			//Maybe I should test this...
			if(commenting) {
				for(int i = 0; i < toComment.length; i ++) {
					//Comment this line
					toComment[i] = "//" + toComment[i];
				}
			} else {
				for(int i = 0; i < toComment.length; i ++) {
					//Uncomment this line
					toComment[i] = toComment[i].substring(2);
				}
			}
			
			System.arraycopy(toComment, 0, lines, startLine, endLine - startLine);
			
			String text = "";
			for(String line : lines) {
				text += line + "\n";
			}
			
			if(!trailingNewline) {
				text = text.substring(0, text.length() - 1);
			}
			
			code.setUpdateText(text);
			code.clearTokens();
			
			code.setSelection(code.offsetForLine(startLine), code.offsetForLineEnd(endLine - 1) - (trailingNewline || endLine < lines.length ? 1 : 0));
			//The current implementation of this function is ugly, but we don't have any alternatives...
			code.startSelectionActionMode();
		}
	}
	
	@Override
	public String getMenuTitle() {
		return context.getResources().getString(R.string.tool_comment_uncomment);
	}
	
	@Override
	public KeyBinding getKeyBinding() {
		return context.getEditor().getKeyBindings().get("comment");
	}
	
	@Override
	public boolean showInToolsMenu(APDE.SketchLocation sketchLocation) {
		return false;
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean createSelectionActionModeMenuItem(MenuItem convert) {
		convert.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		
		return true;
	}

	@Override
	public void handlePermissionsResult(int requestCode, String permissions[], int[] grantResults) {}
}