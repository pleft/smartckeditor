package com.pleft.client.ckeditor.event;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasSaveHandlers<T> extends HasHandlers {

	HandlerRegistration addSaveHandler(SaveHandler<T> handler);
}