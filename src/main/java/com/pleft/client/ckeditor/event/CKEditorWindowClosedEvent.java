package com.pleft.client.ckeditor.event;

import com.google.gwt.event.shared.GwtEvent;
import com.pleft.client.ckeditor.CKEditorWindowItem;

public class CKEditorWindowClosedEvent<T> extends GwtEvent<CKEditorWindowClosedHandler<T>> {

    private static Type<CKEditorWindowClosedHandler<?>> TYPE = new Type<CKEditorWindowClosedHandler<?>>();
    public static Type<CKEditorWindowClosedHandler<?>> getType() {
        return TYPE != null ? TYPE : (TYPE = new Type<CKEditorWindowClosedHandler<?>>());
    }

    private final T target;

    protected CKEditorWindowClosedEvent(T target) {
        this.target = target;
    }

    public static <T> void fire(HasCKEditorWindowClosedHandlers<T> source, T target) {
        if (TYPE != null) {
            CKEditorWindowClosedEvent<T> event = new CKEditorWindowClosedEvent<T>(target);
            source.fireEvent(event);
        }
    }

    @Override
    public Type<CKEditorWindowClosedHandler<T>> getAssociatedType() {
        return (Type) TYPE;
    }

    @Override
    protected void dispatch(CKEditorWindowClosedHandler handler) {
        handler.onCKEditorWindowClosed(this);
    }

    public T getTarget() {
        return target;
    }

    public String getEditorValue() {
        if(getSource() instanceof CKEditorWindowItem) {
            return (String) ((CKEditorWindowItem) getSource()).getValue();
        }

        return null;
    }
}
