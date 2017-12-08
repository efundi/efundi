package edu.nwu.sakai.studentlink.client;

public class JQueryMaskUtility {

    /**
     * Create a jQuery mask definition. You only need to create it once, but you can re-use it in the same application.
     * @param identifier The symbol (should not be already defined) that will be used to identify this mask.
     * @param range The range of valid values allowed in the input mask.
     * @see http://digitalbush.com/projects/masked-input-plugin/
     */
    public static native void createMaskDefinition(String identifier, String range)/*-{
        $wnd.jQuery(function($) {
        $.mask.definitions[identifier]='[' + range + ']';
        });
    }-*/;

    /**
     * Mask an HTML element with the given input mask.
     * @param id The id of the HTML element to be masked.
     * @param mask The input mask.
     * @see http://digitalbush.com/projects/masked-input-plugin/
     */
    public static native void mask(String id, String mask)/*-{
        $wnd.jQuery(function($) {
        $wnd.jQuery("#" + id).mask(mask);
        });
    }-*/;
}