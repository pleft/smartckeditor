package com.pleft.client.ckeditor.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasCKEditorWindowClosedHandlers<T> extends HasHandlers {

	HandlerRegistration addWindowClosedHandler(CKEditorWindowClosedHandler<T> handler);
}