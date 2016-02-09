package com.pleft.client.ckeditor;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.smartgwt.client.widgets.WidgetCanvas;
import com.smartgwt.client.widgets.form.fields.CanvasItem;

public class CKEditorItem extends CanvasItem {
    private SmartCKEditor ckEditor;
    private WidgetCanvas editorCanvas;

    public CKEditorItem(CKEditorOdysseyConfig conf) {
        super();
        ckEditor = new SmartCKEditor(conf);
        ckEditor.setParentFormItem(this);
        editorCanvas = new WidgetCanvas(ckEditor);
        this.setCanvas(editorCanvas);
        this.setShouldSaveValue(true);
        this.setAutoDestroy(true);
        ckEditor.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                storeValue(event.getValue());
            }
        });
    }

    public CKEditorItem(String name, CKEditorOdysseyConfig conf) {
        this(conf);
        this.setName(name);
    }

    public CKEditorItem(String name) {
        this(name, CKEditorOdysseyConfig.getOdysseyDefault());
    }

    public CKEditorItem(String name, String title,  CKEditorOdysseyConfig conf) {
        this(name, conf);
        this.setTitle(title);
    }

    public CKEditorItem(String name, String title) {
        this(name);
        this.setTitle(title);
    }

    @Override
    public Object getValue() {
        return ckEditor.getHTML();
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        ckEditor.setHTML(value);
    }

    @Override
    public void setWidth(String width) {
        throw new UnsupportedOperationException("please set width in CKEditorOdysseyConfig argument of constructor");
    }

    @Override
    public void setHeight(String height) {
        throw new UnsupportedOperationException("please set height in CKEditorOdysseyConfig argument of constructor");
    }

    public void destroy() {
        ckEditor.destroy();
    }

    /**
     * Same behavior as setReadOnly
     * @param disabled
     */
    @Override
    public void setDisabled(Boolean disabled) {
        super.setDisabled(disabled);
        setReadOnly(disabled);
    }

    /**
     * Makes the CKEditor read-only, affects both config and runtime
     * @param readOnly
     */
    public void setReadOnly(boolean readOnly) {
        ckEditor.config.setReadOnly(readOnly);
        ckEditor.setReadOnly(readOnly);
    }
}