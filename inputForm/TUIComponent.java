package inputForm;

import Keyhandle.KeyHandle;

/**
 * Interface for Container Class to all components that contain call back or key handling
 * TUIComponent
 */
public interface TUIComponent {
    String fire(); // no sout, no clearLines()
    default boolean isFocusable() { return false; }
    default void onFocus() {}
    default void onBlur() {}
    default TUIComponent startActive(boolean startActive) { return this; }
    KeyHandle getStartKey();
    KeyHandle getStopKey();
}
