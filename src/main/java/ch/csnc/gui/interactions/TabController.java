package ch.csnc.gui.interactions;

import burp.api.montoya.ui.UserInterface;
import ch.csnc.pingback.Pingback;

public interface TabController {
    public UserInterface getUserInterface();
    public void updateDetailsView(Pingback pingback);
    void hideDetailsView();
}
