package com.pleft.client.ckeditor;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Toolbar configuration for CKEditor tailored for use within Odyssey
 */
public class CKEditorOdysseyConfig extends CKConfig {

    private ToolbarLine line;

    TOOLBAR_OPTIONS[] toUndoRedo = {
            TOOLBAR_OPTIONS.Undo,
            TOOLBAR_OPTIONS.Redo,
            TOOLBAR_OPTIONS._,};

    TOOLBAR_OPTIONS[] toFontStyles = {
            TOOLBAR_OPTIONS.Bold,
            TOOLBAR_OPTIONS.Italic,
            TOOLBAR_OPTIONS.Underline,
//            TOOLBAR_OPTIONS.Strike,
            TOOLBAR_OPTIONS.Subscript,
            TOOLBAR_OPTIONS.Superscript,
            TOOLBAR_OPTIONS._,
           };

    TOOLBAR_OPTIONS[] toLists = {
            TOOLBAR_OPTIONS.NumberedList,
            TOOLBAR_OPTIONS.BulletedList,
            TOOLBAR_OPTIONS._,
           };

    TOOLBAR_OPTIONS[] toJustification = {
            TOOLBAR_OPTIONS.JustifyLeft,
            TOOLBAR_OPTIONS.JustifyCenter,
            TOOLBAR_OPTIONS.JustifyRight,
            TOOLBAR_OPTIONS.JustifyBlock,
            TOOLBAR_OPTIONS._,
            };

    TOOLBAR_OPTIONS[] toIndentOutdent = {
            TOOLBAR_OPTIONS.Outdent,
            TOOLBAR_OPTIONS.Indent,
            TOOLBAR_OPTIONS._,
    };

    TOOLBAR_OPTIONS[] toFormat = {
            TOOLBAR_OPTIONS.Format
    };

    TOOLBAR_OPTIONS[] toSpecialChars = {
            TOOLBAR_OPTIONS.SpecialChar
    };

    TOOLBAR_OPTIONS[] toMaximize = {
            TOOLBAR_OPTIONS.Maximize,
            TOOLBAR_OPTIONS._,
    };

    TOOLBAR_OPTIONS[] toSource = {
            TOOLBAR_OPTIONS.Source,
            TOOLBAR_OPTIONS._,
    };

    TOOLBAR_OPTIONS[] toTable = {
            TOOLBAR_OPTIONS.Table,
            TOOLBAR_OPTIONS._,
    };

    TOOLBAR_OPTIONS[] toAll = {
            TOOLBAR_OPTIONS.Undo,
            TOOLBAR_OPTIONS.Redo,
            TOOLBAR_OPTIONS._,
            TOOLBAR_OPTIONS.Bold,
            TOOLBAR_OPTIONS.Italic,
            TOOLBAR_OPTIONS.Underline,
            TOOLBAR_OPTIONS.Strike,
            TOOLBAR_OPTIONS.Subscript,
            TOOLBAR_OPTIONS.Superscript,
            TOOLBAR_OPTIONS._,
            TOOLBAR_OPTIONS.NumberedList,
            TOOLBAR_OPTIONS.BulletedList,
            TOOLBAR_OPTIONS._,
            TOOLBAR_OPTIONS.JustifyLeft,
            TOOLBAR_OPTIONS.JustifyCenter,
            TOOLBAR_OPTIONS.JustifyRight,
            TOOLBAR_OPTIONS.JustifyBlock,
            TOOLBAR_OPTIONS._,
            TOOLBAR_OPTIONS.Format,
            TOOLBAR_OPTIONS._,
            TOOLBAR_OPTIONS.Table,
            TOOLBAR_OPTIONS.Link,
            TOOLBAR_OPTIONS.Unlink,
            TOOLBAR_OPTIONS.Maximize
    };

    public CKEditorOdysseyConfig(ConfigToolbarType... configToolbarTypes) {
        line = new ToolbarLine();
        LinkedList<TOOLBAR_OPTIONS> toList = new LinkedList<TOOLBAR_OPTIONS>();
        if(configToolbarTypes == null || configToolbarTypes.length == 0) {
            toList.addAll(Arrays.asList(toAll));
        } else {
            for(ConfigToolbarType configToolbarType: configToolbarTypes) {
                switch (configToolbarType) {
                    case ALL:
                        toList.addAll(Arrays.asList(toAll));
                        break;
                    case FONT_STYLE:
                        toList.addAll(Arrays.asList(toFontStyles));
                        break;
                    case LISTS:
                        toList.addAll(Arrays.asList(toLists));
                        break;
                    case JUSTIFICATION:
                        toList.addAll(Arrays.asList(toJustification));
                        break;
                    case FORMAT:
                        toList.addAll(Arrays.asList(toFormat));
                        break;
                    case UNDO_REDO:
                        toList.addAll(Arrays.asList(toUndoRedo));
                        break;
                    case INDENT_OUTDENT:
                        toList.addAll(Arrays.asList(toIndentOutdent));
                        break;
                    case SPECIAL_CHARS:
                        toList.addAll(Arrays.asList(toSpecialChars));
                        addGreekAndSpecialChars();
                        break;
                    case MAXIMIZE:
                        toList.addAll((Arrays.asList(toMaximize)));
                        break;
                    case SOURCE:
                        toList.addAll((Arrays.asList(toSource)));
                        break;
                    case TABLE:
                        toList.addAll((Arrays.asList(toTable)));
                        break;
                }
            }
        }

        line.addAll(toList.toArray(new TOOLBAR_OPTIONS[0]));

        // Creates the toolbar
        Toolbar t = new Toolbar();
        t.add(line);

        // sets some params
        setEntities_latin(false);
        setEntities(false);
        // Set the toolbar to the config (replace the FULL preset toolbar)
        setToolbar(t);
        setBaseFloatZIndex(1000000);
        setUseFormPanel(false);
        hideLinkAdvancedTab();
        hideLinkTargetTab();
        setResizeEnabled(false);
        removePlugins("elementspath, magicline");
        setBrowserSpellChecker(true);
        setExtraPlugins("confighelper");
        hideDialogFields("table:info:txtHeight;tableProperties:info:txtHeight;table:info:txtCaption;tableProperties:info:txtCaption;table:info:txtSummary;tableProperties:info:txtSummary;");
        defaultDialogFieldsValues("{ \"table\":{ \"info\":{ \"txtWidth\":\"100%\" } }, \"tableProperties\": { \"info\": { \"txtWidth\":\"100%\" } } }");

    }

    public void addToolbarOption(TOOLBAR_OPTIONS option) {
        line.add(option);
    }

    /**
     * Creates a typical Odyssey text editor toolbar configuration
     * @return
     */
    public static CKEditorOdysseyConfig getOdysseyDefault() {
        return new CKEditorOdysseyConfig(
                ConfigToolbarType.UNDO_REDO,
                ConfigToolbarType.SPECIAL_CHARS,
                ConfigToolbarType.FONT_STYLE,
                ConfigToolbarType.LISTS,
                ConfigToolbarType.INDENT_OUTDENT,
                ConfigToolbarType.TABLE,
                ConfigToolbarType.MAXIMIZE);
    }

    public static CKEditorOdysseyConfig getEvaluationsDefault() {
        return new CKEditorOdysseyConfig(
                ConfigToolbarType.UNDO_REDO,
                ConfigToolbarType.SPECIAL_CHARS,
                ConfigToolbarType.FONT_STYLE,
                ConfigToolbarType.LISTS,
                ConfigToolbarType.INDENT_OUTDENT,
                ConfigToolbarType.JUSTIFICATION,
                ConfigToolbarType.TABLE,
                ConfigToolbarType.MAXIMIZE);
    }

    public static CKEditorOdysseyConfig getDesignerDefault() {
        final CKEditorOdysseyConfig config = new CKEditorOdysseyConfig(
                ConfigToolbarType.UNDO_REDO,
                ConfigToolbarType.SPECIAL_CHARS,
                ConfigToolbarType.FONT_STYLE,
                ConfigToolbarType.LISTS,
                ConfigToolbarType.INDENT_OUTDENT,
                ConfigToolbarType.TABLE,
                ConfigToolbarType.FORMAT,
                ConfigToolbarType.MAXIMIZE);
        config.setFormatOptions();
        return config;
    }
}
