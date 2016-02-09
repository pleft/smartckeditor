package com.pleft.client.ckeditor.event;

import com.google.gwt.event.shared.EventHandler;

public interface CKEditorWindowCustomButtonClickHandler<T> extends EventHandler {

    void onCustomButtonClick(CKEditorCustomButtonClickEvent<T> event);
}
