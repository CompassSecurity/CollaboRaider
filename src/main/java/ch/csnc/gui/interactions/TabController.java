package ch.csnc.gui.interactions;

import burp.api.montoya.ui.UserInterface;
import ch.csnc.pingback.Pingback;

public interface TabController {
    UserInterface getUserInterface();

    void updateDetailsView(Pingback pingback);

    void hideDetailsView();
}
