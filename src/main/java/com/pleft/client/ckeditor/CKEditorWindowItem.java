package com.pleft.client.ckeditor;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.pleft.client.ckeditor.event.*;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>FormItem allowing a popup window with CKEditor to edit it's content.
 * Closing the window the CKEditor instance is killed promptly.</p>
 * <p>adding a CKEditorWindowClosedHandler allows user to gain control upon closing the
 * editor window e.g. perform a DB saving of data</p>
 *
 */
public class CKEditorWindowItem extends CanvasItem implements HasCKEditorWindowClosedHandlers, HasCKEditorWindowCustomButtonHandlers {

    private static int WINDOW_WIDTH = 600;
    private static int WINDOW_HEIGHT = 400;

    private HTMLPane htmlPane;

    private ToolStrip toolStrip;
    private ToolStripButton editButton;
    private ToolStripButton viewButton;
    private ToolStripButton customButton;

    private String editWindowStatus;
    private String viewWindowStatus;

    private HandlerRegistration customButtonHandlerRegistration;
    private List<HandlerRegistration> handlerRegistrations = new LinkedList<>();

    public CKEditorWindowItem(CKEditorOdysseyConfig conf) {
        super();
        setup(conf);
    }

    public CKEditorWindowItem(String name) {
        this(name, CKEditorOdysseyConfig.getOdysseyDefault());
    }

    public CKEditorWindowItem(String name, CKEditorOdysseyConfig conf) {
        this(conf);
        this.setName(name);
    }

    public CKEditorWindowItem(String name, String title) {
        this(name);
        this.setTitle(title);
    }

    public CKEditorWindowItem(String name, String title, CKEditorOdysseyConfig conf) {
        this(name, conf);
        this.setTitle(title);
    }

    private void setup(final CKEditorOdysseyConfig conf) {
        setupHTMLPane();
        setupToolStrip(conf);
        VLayout vLayout = new VLayout();
        vLayout.addMember(toolStrip);
        vLayout.addMember(htmlPane);
        setCanvas(vLayout);
        this.setShouldSaveValue(true);
        this.setAutoDestroy(true);
    }

    private void setupHTMLPane() {
        htmlPane = new HTMLPane();
        htmlPane.setCanSelectText(true);
        htmlPane.setBackgroundColor("white");
        htmlPane.setBorder("1px solid grey");
        htmlPane.setWidth100();
        htmlPane.setPadding(10);
    }

    private void setupToolStrip(final CKEditorOdysseyConfig conf) {
        this.toolStrip = new ToolStrip();
        toolStrip.setPadding(2);
        toolStrip.setMembersMargin(2);
        toolStrip.setWidth100();
        setupEditButton(conf);
        setupViewButton();
    }

    private void setupViewButton() {
        viewButton = new ToolStripButton("View");
        viewButton.setIcon("[ISOMORPHIC]/smartckeditor/view.png");
        viewButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                HTMLPane windowPane = new HTMLPane();
                windowPane.setPadding(10);
                windowPane.setWidth100();
                windowPane.setHeight100();
                windowPane.setContents((String) getValue());

                final Window window = new Window();
                window.setTitle(CKEditorWindowItem.this.getTitle() + " View");
                window.setShowMinimizeButton(false);
                window.setShowMaximizeButton(true);
                window.setShowResizer(true);
                window.setCanDragResize(true);
                window.setIsModal(true);
                window.setShowModalMask(true);
                window.setShowFooter(true);
                window.setStatus(getViewWindowStatus());
                window.setWidth(WINDOW_WIDTH);
                window.setHeight(WINDOW_HEIGHT);
                window.centerInPage();
                window.addCloseClickHandler(new CloseClickHandler() {
                    @Override
                    public void onCloseClick(CloseClickEvent event) {
                        window.close();
                        window.destroy();
                    }
                });
                window.addItem(windowPane);
                window.show();
                window.bringToFront();
            }
        });
        toolStrip.addButton(viewButton);
    }

    private void setupEditButton(final CKEditorOdysseyConfig conf) {
        editButton = new ToolStripButton("Edit");
        editButton.setIcon("[ISOMORPHIC]/smartckeditor/edit.png");
        editButton.setShowDisabledIcon(false);
        editButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final Window window = new Window();
                window.setOverflow(Overflow.HIDDEN);
                window.setTitle("Edit " + CKEditorWindowItem.this.getTitle());
                window.setShowMinimizeButton(false);
                window.setIsModal(true);
                window.setShowModalMask(true);
                window.setShowFooter(true);
                window.setStatus(getEditWindowStatus());
                window.setWidth(WINDOW_WIDTH);
                window.setHeight(WINDOW_HEIGHT);
                window.centerInPage();

                conf.setHeight(345);    //to fit the window
                conf.setWidth("100%");
                final CKEditor ckEditor = new CKEditor(conf) {
                    @Override
                    public void onDialogShow() {
                        // to overcome the problem that smartgwt modality obstruct the dropdowns of a ckeditor dialog to be pressed
                        final NodeList<Element> allWindowsWithModalMask = findAllWindowsWithModalMask();
                        if(allWindowsWithModalMask != null ) {
                            for(int i =0; i<allWindowsWithModalMask.getLength(); i++) {
                                Element el = allWindowsWithModalMask.getItem(i);
                                String id = el.getAttribute("eventproxy");
                                if(Canvas.getById(id) != null) {
                                    hideClickMask(Canvas.getById(id).getOrCreateJsObj());
                                }
                            }
                        }
                    }
                };
                ckEditor.setHTML((String) getValue());
                ckEditor.addValueChangeHandler(new ValueChangeHandler<String>() {
                    @Override
                    public void onValueChange(ValueChangeEvent<String> event) {
                        storeValue(event.getValue());
                        CKEditorWindowItem.this.setValue(ckEditor.getHTML());
                    }
                });
                window.addCloseClickHandler(new CloseClickHandler() {
                    @Override
                    public void onCloseClick(CloseClickEvent event) {
                        ckEditor.destroy();
                        CKEditorWindowClosedEvent.fire(CKEditorWindowItem.this, CKEditorWindowItem.this);
                        window.close();
                        window.destroy();
                    }
                });
                window.addItem(ckEditor);
                window.show();
                window.bringToFront();
            }
        });
        toolStrip.addButton(editButton);
    }

    /**
     * <p>
     * Adds a custom button next to the View Button on the CKEditorWindowItem's toolstrip.
     * Currently only one custom button is supported, calling this function more than once will not create more buttons.
     * </p>
     *
     * @param customButtonProperties
     * @param ckEditorWindowCustomButtonClickHandler
     */
    public void addCustomButton(final CustomButtonProperties customButtonProperties, CKEditorWindowCustomButtonClickHandler ckEditorWindowCustomButtonClickHandler) {
        if(hasCustomButton()) {
            toolStrip.removeMember(customButton);
            customButtonHandlerRegistration.removeHandler();
        }
        customButton = new ToolStripButton(customButtonProperties.getTitle());
        customButton.setIcon(customButtonProperties.getIcon());
        customButton.setShowDisabledIcon(false);
        customButtonHandlerRegistration = addCustomButtonHandler(ckEditorWindowCustomButtonClickHandler);

        customButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CKEditorCustomButtonClickEvent.fire(CKEditorWindowItem.this, CKEditorWindowItem.this, customButtonProperties);
            }
        });
        toolStrip.addButton(customButton);
    };

    @Override
    public void setValue(String value) {
        super.setValue(value);

        //null value needs some extra treatment in htmlPane
        String htmlValue = "<p>"+(getHint()!=null ? getHint() : "")+"</p>";
        if(value != null && value.trim().length()>0) {
            htmlValue = value;
        }
        htmlPane.setContents(htmlValue);
    }

    public void setReadOnly(boolean value) {
        editButton.setDisabled(value);
    }

    @Override
    public HandlerRegistration addWindowClosedHandler(CKEditorWindowClosedHandler handler) {
        final HandlerRegistration handlerRegistration = doAddHandler(handler, CKEditorWindowClosedEvent.getType());
        handlerRegistrations.add(handlerRegistration);
        return handlerRegistration;
    }

    @Override
    public HandlerRegistration addCustomButtonHandler(CKEditorWindowCustomButtonClickHandler handler) {
        final HandlerRegistration handlerRegistration = doAddHandler(handler, CKEditorCustomButtonClickEvent.getType());
        handlerRegistrations.add(handlerRegistration);
        return handlerRegistration;
    }

    public String getEditWindowStatus() {
        return editWindowStatus;
    }

    public void setEditWindowStatus(String editWindowStatus) {
        this.editWindowStatus = editWindowStatus;
    }

    public String getViewWindowStatus() {
        return viewWindowStatus;
    }

    public void setViewWindowStatus(String viewWindowStatus) {
        this.viewWindowStatus = viewWindowStatus;
    }

    @Override
    public void clearValue() {
        super.clearValue();
        String htmlValue = "<p></p>";
        htmlPane.setContents(htmlValue);
    }

    public boolean hasCustomButton() {
        return customButton!=null;
    }

    public void removeHandlers() {
        for(HandlerRegistration handlerRegistration: handlerRegistrations) {
            handlerRegistration.removeHandler();
        }
    }

    protected native NodeList<Element> findAllWindowsWithModalMask() /*-{
        return $wnd.document.querySelectorAll("[class='windowBackground']");
    }-*/;

    protected native void hideClickMask(JavaScriptObject windowCanvas) /*-{
        windowCanvas.hideClickMask();
    }-*/;
}