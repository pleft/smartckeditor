package com.pleft.client.ckeditor.event;

import com.google.gwt.event.shared.GwtEvent;
import com.pleft.client.ckeditor.CustomButtonProperties;

public class CKEditorCustomButtonClickEvent<T> extends GwtEvent<CKEditorWindowCustomButtonClickHandler<T>> {

    private static Type<CKEditorWindowCustomButtonClickHandler<?>> TYPE = new Type<CKEditorWindowCustomButtonClickHandler<?>>();

    public static Type<CKEditorWindowCustomButtonClickHandler<?>> getType() {
        return TYPE != null ? TYPE : (TYPE = new Type<CKEditorWindowCustomButtonClickHandler<?>>());
    }

    private final T target;

    private CustomButtonProperties customButtonProperties;

    protected CKEditorCustomButtonClickEvent(T target) {
        this.target = target;
    }

    public static <T> void fire(HasCKEditorWindowCustomButtonHandlers<T> source, T target, CustomButtonProperties customButtonProperties) {
        if (TYPE != null) {
            CKEditorCustomButtonClickEvent<T> event = new CKEditorCustomButtonClickEvent<T>(target);
            event.setCustomButtonProperties(customButtonProperties);
            source.fireEvent(event);
        }
    }

    @Override
    public Type<CKEditorWindowCustomButtonClickHandler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(CKEditorWindowCustomButtonClickHandler handler) {
        handler.onCustomButtonClick(this);
    }

    public T getTarget() {
        return target;
    }

    public void setCustomButtonProperties(CustomButtonProperties customButtonProperties) {
        this.customButtonProperties = customButtonProperties;
    }

    public CustomButtonProperties getCustomButtonProperties() {
        return customButtonProperties;
    }
}
