/**
 *  This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.pleft.client.ckeditor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.pleft.client.ckeditor.event.HasSaveHandlers;
import com.pleft.client.ckeditor.event.SaveEvent;
import com.pleft.client.ckeditor.event.SaveHandler;

/**
 * This class provides a CKEditor as a Widget
 * 
 * @author Damien Picard <damien.picard@axeiya.com>
 * @author Emmanuel COQUELIN <emmanuel.coquelin@axeiya.com>
 */
public class CKEditor extends Composite implements HasSaveHandlers<CKEditor>, HasValueChangeHandlers<String>, ClickHandler, HasAlignment,
		HasHTML, HasText {

	protected String name;
	protected JavaScriptObject editor;
	protected TextArea textArea;
	protected Element baseTextArea;
	protected JavaScriptObject dataProcessor;
	protected CKConfig config;
	protected boolean enabledInHostedMode = true;
	protected boolean replaced = false;
	protected boolean textWaitingForAttachment = false;
	protected String waitingText;
	protected boolean waitingForDisabling = false;
	protected boolean disabled = false;
	protected Element div;
	protected Node ckEditorNode;
	protected HTML disabledHTML;
	protected boolean focused = false;

	protected HorizontalAlignmentConstant hAlign = null;
	protected VerticalAlignmentConstant vAlign = null;

	/**
	 * Creates an editor with the CKConfig.basic configuration. By default, the
	 * CKEditor is enabled in hosted mode ; if not, the CKEditor is replaced by
	 * a simple TextArea
	 */
	public CKEditor() {
		this(CKConfig.basic);
	}

	/**
	 * Creates an editor with the given configuration. By default, the CKEditor
	 * is enabled in hosted mode ; if not, the CKEditor is replaced by a simple
	 * TextArea
	 * 
	 * @param config
	 *            The configuration
	 */
	public CKEditor(CKConfig config) {
		super();
		this.config = config;
		initCKEditor();
        setSize("100%", "100%");
	}

	/**
	 * Creates an editor with the CKConfig.basic configuration.
	 * 
	 * @param enabledInHostedMode
	 *            Indicates if the editor must be used in Hosted Mode
	 */
	public CKEditor(boolean enabledInHostedMode) {
		this(enabledInHostedMode, CKConfig.basic);
	}

	/**
	 * Creates an editor with the given CKConfig
	 * 
	 * @param enabledInHostedMode
	 *            Indicates if the editor must be used in Hosted Mode
	 * @param config
	 *            The configuration
	 */
	public CKEditor(boolean enabledInHostedMode, CKConfig config) {
		super();
		this.enabledInHostedMode = enabledInHostedMode;
		this.config = config;
		initCKEditor();
	}

	/**
	 * Initialize the editor
	 */
	private void initCKEditor() {
		div = Document.get().createDivElement();

		if (GWT.isScript() || enabledInHostedMode) {
			baseTextArea = Document.get().createTextAreaElement();
			name = HTMLPanel.createUniqueId();
			div.appendChild(baseTextArea);
            div.setAttribute("style", "height: " + config.getHeight() + "px;");
            baseTextArea.setAttribute("name", name);
            baseTextArea.setAttribute("style", "height: "+config.getHeight() + "px;");
			this.sinkEvents(Event.ONCLICK | Event.KEYEVENTS);

		} else {
			textArea = new TextArea();
			textArea.setHeight(Integer.toString(config.getHeight()));
			if (config.getWidth() != null)
				textArea.setWidth(config.getWidth());
			div.appendChild(textArea.getElement());
		}
		if (config.isUsingFormPanel()) {
			FormPanel form = new FormPanel();
			Button submit = new Button();
			submit.addClickHandler(this);
			submit.getElement().setAttribute("name", "submit");
			submit.setVisible(false);
			// .getElement().setAttribute("style", "visibility:hidden");

			form.getElement().appendChild(div);
			form.add(submit);
			initWidget(form);
		} else {
			SimplePanel simplePanel = new SimplePanel();
			simplePanel.getElement().appendChild(div);
			initWidget(simplePanel);
		}

	}

	@Override
	protected void onLoad() {
		initInstance();
	}

	/**
	 * Replace the text Area by a CKEditor Instance
	 */
	protected void initInstance() {
		if ((GWT.isScript() || enabledInHostedMode) && !replaced && !disabled) {
			replaced = true;
			replaceTextArea(baseTextArea, this.config.getConfigObject());

			if (textWaitingForAttachment) {
				textWaitingForAttachment = false;
				setHTML(waitingText);
			}

			if (hAlign != null) {
				setHorizontalAlignment(hAlign);
			}

			if (vAlign != null) {
				setVerticalAlignment(vAlign);
			}

			if (this.config.isFocusOnStartup()) {
				this.focused = true;
				setAddFocusOnLoad(focused);
			}

			if (waitingForDisabling) {
				this.waitingForDisabling = false;
				setDisabled(this.disabled);
			}
			
			listenToBlur();
            listenToFocus();
            listenToChanges();
            listenToDialogDefinition();
            resizeOnInstanceReady(config.getConfigObject());

			/*
			 * if (config.getBreakLineChars() != null) {
			 * setNativeBreakLineChars(config.getBreakLineChars()); }
			 * 
			 * if (config.getSelfClosingEnd() != null) {
			 * setNativeSelfClosingEnd(config.getSelfClosingEnd()); }
			 */
		}

	}

	private native void setAddFocusOnLoad(boolean focus)/*-{
		var e = this.@com.pleft.client.ckeditor.CKEditor::editor;

		e.on('dataReady', function(ev){

		if(focus){
			e.focus();
			var lastc = e.document.getBody().getLast();		
			e.getSelection().selectElement(lastc);
			var range = e.getSelection().getRanges()[0];
			range.collapse(false);
			range.setStart(lastc,  range.startOffset);
			try{
				range.setEnd(lastc , range.endOffset);
			}catch(err){
			}
			range.select();
		}

		});
	}-*/;
	
	private native void listenToBlur() /*-{
		var me = this;
		var e = this.@com.pleft.client.ckeditor.CKEditor::editor;
		e.on('blur', function(ev){
			me.@com.pleft.client.ckeditor.CKEditor::dispatchBlur()();
		});
	}-*/;

    private native void listenToFocus() /*-{
		var me = this;
		var e = this.@com.pleft.client.ckeditor.CKEditor::editor;
		e.on('focus', function(ev){
			me.@com.pleft.client.ckeditor.CKEditor::dispatchFocus()();
		});
	}-*/;

    private native void listenToChanges() /*-{
		var me = this;
		var e = this.@com.pleft.client.ckeditor.CKEditor::editor;
		e.on('change', function(ev){
			me.@com.pleft.client.ckeditor.CKEditor::dispatchChanges()();
		});
	}-*/;

    private native void listenToDialogDefinition() /*-{
    	var me = this;
		var e = this.@com.pleft.client.ckeditor.CKEditor::editor;
        $wnd.CKEDITOR.on('dialogDefinition', function (evt) {
            var dialog = evt.data.definition.dialog;
            dialog.on('show', function () {
            	me.@com.pleft.client.ckeditor.CKEditor::onDialogShow()();
                  var element = this.getElement();
                  var labelledby = element.getAttribute('aria-labelledby');
                  var nativeElement = $wnd.document.querySelector("[aria-labelledby='" + labelledby + "']");
                  nativeElement.onclick = function (evt) {
                     if ((evt.target.tagName == "INPUT" || evt.target.tagName == "SELECT" || evt.target.tagName == "TEXTAREA") &&
                          -1 != evt.target.className.indexOf("cke_dialog_ui_input")) {
                          evt.target.focus();
                     };
                  }
            });
        });
    }-*/;

    private native void resizeOnInstanceReady(JavaScriptObject config) /*-{
		var me = this;
		var e = this.@com.pleft.client.ckeditor.CKEditor::editor;
		e.on('instanceReady', function(ev){
			ev.editor.resize(config.width, config.height);
		});
	}-*/;

    /**
     * replaces textarea with ckEditor
     * @param o
     * @param config
     */
	private native void replaceTextArea(Object o, JavaScriptObject config) /*-{
		this.@com.pleft.client.ckeditor.CKEditor::editor = $wnd.CKEDITOR.replace(o,config);
	}-*/;

	@Deprecated
	/**
	 * Use getNativeHTML() instead
	 */
	private native String getNativeText() /*-{
		var e = this.@com.pleft.client.ckeditor.CKEditor::editor;
		return e.getData();
	}-*/;

	private native String getNativeHTML() /*-{
		var e = this.@com.pleft.client.ckeditor.CKEditor::editor;
		return e.getData();
	}-*/;

	public native JavaScriptObject getSelection() /*-{
		var e = this.@com.pleft.client.ckeditor.CKEditor::editor;
		return e.getSelection();
	}-*/;

	private native void setNativeFocus(boolean focus)/*-{
		if(focus){
			var e = this.@com.pleft.client.ckeditor.CKEditor::editor;
			if(e){
				e.focus();

				var lastc = e.document.getBody().getLast();		
				e.getSelection().selectElement(lastc);
				var range = e.getSelection().getRanges()[0];
				range.collapse(false);
				range.setStart(lastc,  range.startOffset);
				try{
					range.setEnd(lastc , range.endOffset);
				}catch(err){
				}
				range.select();
			}
		}
	}-*/;

	@Deprecated
	/**
	 * Use setNativeHTML(String html) instead
	 */
	private native void setNativeText(String text) /*-{
		var e = this.@com.pleft.client.ckeditor.CKEditor::editor;
		e.setData(text,new Function());
	}-*/;

	private native void setNativeHTML(String html) /*-{
		var e = this.@com.pleft.client.ckeditor.CKEditor::editor;
		e.setData(html,new Function());
	}-*/;

	/**
	 * If you want to set the width, you must do so with the configuration
	 * object before instanciating
	 */
	@Deprecated
	@Override
	public void setWidth(String width) {
		super.setWidth(width);
	}

	/**
	 * If you want to set the height, you must do so with the configuration
	 * object before instanciating
	 */
	@Deprecated
	@Override
	public void setHeight(String height) {
		super.setHeight(height);
	}

	/**
	 * Use getHTML() instead. Returns the editor text
	 * 
	 * @return the editor text
	 */
	@Deprecated
	public String getText() {
		if (GWT.isScript() || enabledInHostedMode) {
			return getNativeText();
		} else {
			return textArea.getText();
		}
	}

	/**
	 * Set the focus natively if ckEditor is attached, alerts you if it's not
	 * the case.
	 * 
	 * @param focus
	 */
	public void setFocus(boolean focus) {
		if (GWT.isScript() || enabledInHostedMode) {
			if (replaced == true) {
				setNativeFocus(focus);
			} else {
				Window.alert("You can't set the focus on startup with the method setFocus(boolean focus).\n"
						+ "If you want to add focus to your instance on startup, use the config object\n"
						+ "with the method setFocusOnStartup(boolean focus) instead.");
			}
		}
	}

	/**
	 * Use to disable CKEditor's instance
	 * 
	 * @param disabled
	 */
	public void setDisabled(boolean disabled) {

		if (this.disabled != disabled) {
			this.disabled = disabled;

			if (GWT.isScript() || enabledInHostedMode) {
				if (disabled) {
					ScrollPanel scroll = new ScrollPanel();
					disabledHTML = new HTML();
					disabledHTML.setStyleName("GWTCKEditor-Disabled");
					scroll.setWidget(disabledHTML);

					if (config.getWidth() != null)
						scroll.setWidth(config.getWidth());

						scroll.setHeight(Integer.toString(config.getHeight()));

					String htmlString = new String();

					if (replaced) {
						htmlString = getHTML();
					} else {
						htmlString = waitingText;
					}

					DivElement divElement = DivElement.as(this.getElement().getFirstChildElement());
					Node node = divElement.getFirstChild();
					while (node != null) {
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							Element element = Element.as(node);
							if (element.getTagName().equalsIgnoreCase("textarea")) {
								destroyInstance();
								replaced = false;
								divElement.removeChild(node);
								ckEditorNode = node;
							}
						}
						node = node.getNextSibling();
					}
					disabledHTML.setHTML(htmlString);
					div.appendChild(scroll.getElement());

				} else {
					if (ckEditorNode != null) {
						DivElement divElement = DivElement.as(this.getElement().getFirstChildElement());
						Node node = divElement.getFirstChild();
						while (node != null) {
							if (node.getNodeType() == Node.ELEMENT_NODE) {
								Element element = Element.as(node);
								if (element.getTagName().equalsIgnoreCase("div")) {
									divElement.removeChild(node);

								}
							}
							node = node.getNextSibling();
						}
						div.appendChild(baseTextArea);
						initInstance();

					}
				}
			} else {
				textArea.setEnabled(disabled);
			}
		}

	}

	private native void destroyInstance()/*-{
		var editor = this.@com.pleft.client.ckeditor.CKEditor::editor;
		if(editor){
		    editor.destroy(true);
		    editor=null;
		}
	}-*/;


    public void destroy() {
        destroyInstance();
    }

    private native void setNativeReadOnly(boolean readOnly) /*-{
        var editor = this.@com.pleft.client.ckeditor.CKEditor::editor;
        if(editor){
            editor.setReadOnly(readOnly);
        }
    }-*/;

    public void setReadOnly(boolean readOnly) {
        setNativeReadOnly(readOnly);
    }
	/**
	 * Returns the editor text
	 * 
	 * @return the editor text
	 */
	public String getHTML() {
		if (GWT.isScript() || enabledInHostedMode) {
			if (replaced)
				return getNativeHTML();
			else {
				return waitingText;
			}
		} else {
			return textArea.getText();
		}
	}

	/**
	 * {@link #getHTML()}
	 * 
	 * @return
	 */
	public String getData() {
		return getHTML();
	}

	/**
	 * Use setHtml(String html) instead. Set the editor text
	 * 
	 * @param text
	 *            The text to set
	 */
	@Deprecated
	public void setText(String text) {
		if (GWT.isScript() || enabledInHostedMode) {
			if (replaced)
				setNativeText(text);
			else {
				waitingText = text;
				textWaitingForAttachment = true;
			}
		} else {
			textArea.setText(text);
		}
	}

	/**
	 * Set the editor's html
	 * 
	 * @param html
	 *            The html string to set
	 */
	public void setHTML(String html) {
		if (GWT.isScript() || enabledInHostedMode) {
			if (replaced)
				setNativeHTML(html);
			else {
				waitingText = html;
				textWaitingForAttachment = true;
			}
		} else {
			textArea.setText(html);
		}
	}

	/**
	 * {@link #setHTML(String)}
	 * 
	 * @param data
	 */
	public void setData(String data) {
		setHTML(data);
	}

	/**
	 * Used for catching Save event
	 * 
	 * @param o
	 * @return
	 */
	private static native String getParentClassname(JavaScriptObject o) /*-{
		var classname = o.parentNode.getAttribute("class");
		if(classname == null)
		return o.parentNode.className;
		return classname;
	}-*/;

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getRelativeElement().getAttribute("name").equals("submit")) {
			event.stopPropagation();
			SaveEvent.fire(this, this, this.getHTML());
		}

	}

	@Override
	public HandlerRegistration addSaveHandler(SaveHandler<CKEditor> handler) {
		return addHandler(handler, SaveEvent.getType());
	}

	@Override
	public HorizontalAlignmentConstant getHorizontalAlignment() {
		return hAlign;
	}

	@Override
	public void setHorizontalAlignment(HorizontalAlignmentConstant align) {
		this.hAlign = align;
		if (replaced)
			this.getElement().getParentElement().setAttribute("align", align.getTextAlignString());
	}

	@Override
	public VerticalAlignmentConstant getVerticalAlignment() {
		return vAlign;
	}

	@Override
	public void setVerticalAlignment(VerticalAlignmentConstant align) {
		this.vAlign = align;
		if (replaced)
			this.getElement().getParentElement().setAttribute("style", "vertical-align:" + align.getVerticalAlignString());

	}

	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
		return this.addHandler(handler, ValueChangeEvent.getType());
	}
	
	/**
	 * Dispatch a blur CKEditor event to a ValueChangeEvent
	 */
	protected void dispatchBlur(){
		ValueChangeEvent.fire(this, this.getHTML());
	}

    protected void dispatchFocus() {
        //do nothing
    }

    protected void dispatchChanges() {
        ValueChangeEvent.fire(this, this.getHTML());
    }

	public void onDialogShow() {
		//do nothing
	}

    private native void jsniInsertTextAtCursorPos(String elementID, String html) /*-{
    	var instance = $wnd.CKEDITOR.instances[elementID];
    	if (instance != null) {
        	instance.insertHtml(html);
    	}
	}-*/;

    public void insertTextAtCursorPos(String html) {
        jsniInsertTextAtCursorPos(name, html);
    }

    private static native void nativeDestroyAllInstances() /*-{
        for(name in $wnd.CKEDITOR.instances) {
            var e = $wnd.CKEDITOR.instances[name];
            e.destroy(true);
            e=null;
        }
    }-*/;

    public static void destroyAllInstances() {
        nativeDestroyAllInstances();
    }

}
