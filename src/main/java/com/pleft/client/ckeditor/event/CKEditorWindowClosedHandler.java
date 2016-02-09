package com.pleft.client.ckeditor.event;

import com.google.gwt.event.shared.EventHandler;

public interface CKEditorWindowClosedHandler<T> extends EventHandler {

    void onCKEditorWindowClosed(CKEditorWindowClosedEvent<T> event);
}
