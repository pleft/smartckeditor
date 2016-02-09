package com.pleft.client.ckeditor;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.widgets.form.fields.events.*;

/**
 * Extending {@link com.pleft.client.ckeditor.CKEditor} with SmartGWT capabilities
 * such as focus and blur (SmartGWT-wise) events.
 * <P>
 * Note:<br/>
 * parentFormItem property is intended for using {@link com.pleft.client.ckeditor.CKEditor}
 * as a {@link com.smartgwt.client.widgets.form.fields.FormItem} component.
 * It's main purpose is to provide the gluecode to use {@link com.pleft.client.ckeditor.CKEditor}
 * in a SmartGWT {@link com.smartgwt.client.widgets.form.DynamicForm}.
 * For this reason when set, it propagates the focus and blur events
 * to the {@link com.smartgwt.client.widgets.form.fields.FormItem}'s handlers.
 * </P>
 */
public class SmartCKEditor extends CKEditor implements HasFocusHandlers, HasBlurHandlers {

    /* the FormItem containing the editor */
    private CKEditorItem parentFormItem;

    public SmartCKEditor(CKConfig config) {
        super(config);
    }

    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return this.addHandler(handler, FocusEvent.getType());
    }

    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return  this.addHandler(handler, BlurEvent.getType());
    }

    public CKEditorItem getParentFormItem() {
        return parentFormItem;
    }

    public void setParentFormItem(CKEditorItem parentFormItem) {
        this.parentFormItem = parentFormItem;
    }

    /**
     * propagates the blur event from the ckEditor to the FormItem which contains it.
     */
    @Override
    protected void dispatchBlur(){
        super.dispatchBlur();
        NativeEvent nativeEvent= Document.get().createBlurEvent();
        BlurEvent.fire(parentFormItem != null ? parentFormItem : this, nativeEvent);

    }

    /**
     * propagates the focus event from the ckEditor to the FormItem which contains it.
     */
    @Override
    protected void dispatchFocus() {
        super.dispatchFocus();
        NativeEvent nativeEvent= Document.get().createFocusEvent();
        FocusEvent.fire(parentFormItem != null ? parentFormItem : this, nativeEvent); //attach the FormItem as source if exists
    }
}
