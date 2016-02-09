package com.pleft.client.ckeditor.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasCKEditorWindowCustomButtonHandlers<T> extends HasHandlers {

	HandlerRegistration addCustomButtonHandler(CKEditorWindowCustomButtonClickHandler<T> handler);
}