//? if fabric {
package nl.enjarai.doabarrelroll;

import nl.enjarai.cicada.api.conversation.ConversationManager;
import nl.enjarai.cicada.api.util.CicadaEntrypoint;
import nl.enjarai.cicada.api.util.JsonSource;

public class CicadaInitializer implements CicadaEntrypoint {
    @Override
    public void registerConversations(ConversationManager conversationManager) {
        conversationManager.registerSource(
                JsonSource.fromUrl("https://raw.githubusercontent.com/enjarai/do-a-barrel-roll/meta/cicada/conversations.json")
                        .or(JsonSource.fromResource("cicada/do_a_barrel_roll/conversations.json")),
                DoABarrelRoll.LOGGER::info
        );
    }
}
//?}
