//? if !fabric {
/*package nl.enjarai.doabarrelroll;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import nl.enjarai.doabarrelroll.util.ModPermissions;

@EventBusSubscriber
public class WhyIsTherePublicTransportationInThisModloader {
    @SubscribeEvent
    public static void gatherPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(ModPermissions.NODES.toArray(new PermissionNode[0]));
    }
}
*///?}
